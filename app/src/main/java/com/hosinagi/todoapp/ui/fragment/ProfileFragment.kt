package com.hosinagi.todoapp.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hosinagi.todoapp.databinding.FragmentProfileBinding
import com.hosinagi.todoapp.ui.activity.LoginActivity
import com.hosinagi.todoapp.utils.AvatarStore
import com.hosinagi.todoapp.utils.SessionManager
import com.permissionx.guolindev.PermissionX
import es.dmoral.toasty.Toasty
import java.io.File

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var pendingCameraFile: File? = null

    companion object {
        private const val KEY_PENDING_CAMERA_FILE = "pending_camera_file"
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val photoFile = pendingCameraFile
            pendingCameraFile = null
            if (result.resultCode == Activity.RESULT_OK && photoFile?.exists() == true) {
                saveAvatarFromFile(photoFile)
            } else {
                photoFile?.delete()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { saveAvatarFromUri(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingCameraFile = savedInstanceState
            ?.getString(KEY_PENDING_CAMERA_FILE)
            ?.let(::File)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = SessionManager.getUsername(requireContext()).orEmpty()
        binding.usernameText.text = username.ifBlank { "未登录" }
        binding.avatarText.text = username.firstOrNull()?.uppercase() ?: "?"
        loadAvatar(username)
        binding.avatarContainer.setOnClickListener {
            showAvatarSourceDialog()
        }
        binding.changeAvatarButton.setOnClickListener {
            showAvatarSourceDialog()
        }
        binding.logoutButton.setOnClickListener {
            SessionManager.logout(requireContext())
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_PENDING_CAMERA_FILE, pendingCameraFile?.absolutePath)
        super.onSaveInstanceState(outState)
    }

    private fun showAvatarSourceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("更换头像")
            .setItems(arrayOf("拍照", "从相册选择")) { _, which ->
                when (which) {
                    0 -> requestCameraPermission()
                    1 -> openGallery()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun requestCameraPermission() {
        PermissionX.init(requireActivity())
            .permissions(Manifest.permission.CAMERA)
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "拍照设置头像需要相机权限",
                    "确定",
                    "取消"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "需要相机权限才能拍照",
                    "去设置",
                    "取消"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    openCamera()
                } else {
                    Toasty.error(requireContext(), "相机权限未授予").show()
                }
            }
    }

    private fun openCamera() {
        runCatching {
            val context = requireContext()
            val cacheDir = File(context.cacheDir, "avatars").apply { mkdirs() }
            val photoFile = File.createTempFile("avatar_", ".jpg", cacheDir)
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            pendingCameraFile = photoFile
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                clipData = ClipData.newRawUri("avatar", photoUri)
                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            cameraLauncher.launch(intent)
        }.onFailure {
            pendingCameraFile?.delete()
            pendingCameraFile = null
            Toasty.error(requireContext(), "无法打开相机").show()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun saveAvatarFromUri(uri: Uri) {
        val context = requireContext().applicationContext
        val username = SessionManager.getUsername(context) ?: return
        Thread {
            val succeeded = AvatarStore.saveFromUri(context, username, uri)
            postToUi {
                handleAvatarSaveResult(succeeded)
            }
        }.start()
    }

    private fun saveAvatarFromFile(photoFile: File) {
        val context = requireContext().applicationContext
        val username = SessionManager.getUsername(context) ?: return
        Thread {
            val succeeded = AvatarStore.saveFromFile(context, username, photoFile)
            photoFile.delete()
            postToUi {
                handleAvatarSaveResult(succeeded)
            }
        }.start()
    }

    private fun handleAvatarSaveResult(succeeded: Boolean) {
        val username = SessionManager.getUsername(requireContext()).orEmpty()
        if (succeeded) {
            loadAvatar(username)
            Toasty.success(requireContext(), "头像已更新").show()
        } else {
            Toasty.error(requireContext(), "头像更新失败").show()
        }
    }

    private fun loadAvatar(username: String) {
        if (username.isBlank()) return
        val avatarFile = AvatarStore.getAvatarFile(requireContext(), username)
        if (avatarFile.exists()) {
            binding.avatarText.isVisible = false
            binding.avatarImage.isVisible = true
            Glide.with(binding.avatarImage)
                .load(avatarFile)
                .centerCrop()
                .into(binding.avatarImage)
        } else {
            binding.avatarImage.isVisible = false
            binding.avatarText.isVisible = true
        }
    }

    private fun postToUi(action: () -> Unit) {
        activity?.runOnUiThread {
            if (isAdded && _binding != null) {
                action()
            }
        }
    }

    override fun onDestroyView() {
        Glide.with(binding.avatarImage).clear(binding.avatarImage)
        _binding = null
        super.onDestroyView()
    }
}
