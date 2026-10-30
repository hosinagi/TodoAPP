package com.hosinagi.todoapp.ui.fragment

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.FragmentAudioBinding
import com.hosinagi.todoapp.demo.musicDemo.MusicAdapter
import com.hosinagi.todoapp.demo.musicDemo.MusicDetailActivity
import com.hosinagi.todoapp.demo.musicDemo.MusicInfo
import com.permissionx.guolindev.PermissionX
import es.dmoral.toasty.Toasty
import kotlin.concurrent.thread

class AudioFragment : Fragment() {
    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!

    private lateinit var musicAdapter: MusicAdapter
    private var isLoading = false
    private var hasLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        musicAdapter = MusicAdapter()
        binding.audioList.adapter = musicAdapter
        musicAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(music: MusicInfo) {
                val intent = Intent(requireContext(), MusicDetailActivity::class.java).apply {
                    putExtra(MusicDetailActivity.EXTRA_MUSIC_INFO, music)
                }
                startActivity(intent)
            }
        })

        binding.swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (hasAudioPermission()) {
                loadMusicList()
            } else {
                requestAudioPermission()
            }
        }
        binding.refreshButton.setOnClickListener {
            if (hasAudioPermission()) {
                loadMusicList()
            } else {
                requestAudioPermission()
            }
        }
        binding.actionButton.setOnClickListener {
            if (hasAudioPermission()) {
                loadMusicList()
            } else {
                requestAudioPermission()
            }
        }

        if (hasAudioPermission()) {
            loadMusicList()
        } else {
            requestAudioPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null && !hasLoaded && !isLoading && hasAudioPermission()) {
            loadMusicList()
        }
    }

    override fun onDestroyView() {
        binding.audioList.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private fun hasAudioPermission(): Boolean {
        val permission = audioPermission()
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun audioPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun requestAudioPermission() {
        PermissionX.init(requireActivity())
            .permissions(audioPermission())
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "扫描本地音频需要读取音频权限",
                    "确定",
                    "取消"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "需要音频读取权限才能扫描本地音乐",
                    "去设置",
                    "取消"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    loadMusicList()
                } else {
                    showPermissionRequired()
                }
            }
    }

    private fun loadMusicList() {
        if (isLoading || _binding == null) return
        isLoading = true
        hasLoaded = false
        binding.loadingIndicator.isVisible = true
        binding.errorState.isVisible = false
        binding.emptyState.isVisible = false
        binding.audioList.isVisible = false

        val resolver = requireContext().applicationContext.contentResolver
        thread {
            val result = runCatching { queryAudioList(resolver) }
            postToUi {
                isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                binding.loadingIndicator.isVisible = false
                result.onSuccess { musicList ->
                    hasLoaded = true
                    musicAdapter.updateMusicList(musicList)
                    binding.audioList.isVisible = musicList.isNotEmpty()
                    binding.emptyState.isVisible = musicList.isEmpty()
                    binding.errorState.isVisible = false
                }.onFailure {
                    hasLoaded = false
                    showLoadError("扫描本地音频失败，请检查权限后重试")
                }
            }
        }
    }

    private fun queryAudioList(contentResolver: ContentResolver): List<MusicInfo> {
        val musicList = mutableListOf<MusicInfo>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
        )
        val selection = "${MediaStore.Audio.Media.DURATION} > ?"
        val selectionArgs = arrayOf("60000")
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                    ?.takeIf { it.isNotBlank() }
                    ?: "未命名音频"
                val artist = cursor.getString(artistColumn)
                    ?.takeIf { it.isNotBlank() && it != MediaStore.UNKNOWN_STRING }
                    ?: "未知艺术家"
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getLong(sizeColumn)
                val contentUri = ContentUris.withAppendedId(collection, id).toString()
                musicList.add(MusicInfo(id, title, artist, duration, size, contentUri))
            }
        }
        return musicList
    }

    private fun showPermissionRequired() {
        hasLoaded = false
        isLoading = false
        binding.swipeRefreshLayout.isRefreshing = false
        binding.loadingIndicator.isVisible = false
        binding.audioList.isVisible = false
        binding.emptyState.isVisible = false
        binding.errorState.isVisible = true
        binding.errorMessage.text = "需要音频读取权限才能扫描手机中的本地音频"
        binding.actionButton.text = getString(R.string.grant_audio_access)
        binding.actionButton.setIconResource(android.R.drawable.ic_lock_lock)
    }

    private fun showLoadError(message: String) {
        binding.audioList.isVisible = false
        binding.emptyState.isVisible = false
        binding.errorState.isVisible = true
        binding.errorMessage.text = message
        binding.actionButton.text = getString(R.string.retry)
        binding.actionButton.setIconResource(android.R.drawable.ic_menu_rotate)
    }

    private fun postToUi(action: () -> Unit) {
        activity?.runOnUiThread {
            if (isAdded && _binding != null) {
                action()
            }
        }
    }
}
