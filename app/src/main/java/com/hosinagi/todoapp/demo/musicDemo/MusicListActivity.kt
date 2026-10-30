package com.hosinagi.todoapp.demo.musicDemo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityMusicListBinding
import com.permissionx.guolindev.PermissionX
import es.dmoral.toasty.Toasty
import kotlin.concurrent.thread

class MusicListActivity : AppCompatActivity() {
    // binding
    private lateinit var binding: ActivityMusicListBinding
    // Adapter
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMusicListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 实例化Adapter
        musicAdapter = MusicAdapter()
        // 将Adapter设置给RecyclerView
        binding.rvMusic.adapter = musicAdapter
        // 扫描音频前，需要申请权限
        requestPermission()
        // 实现接口
        musicAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener{
            override fun onItemClick(music: MusicInfo) {
                // 跳转到MusicDetailActivity
                val intent = Intent(this@MusicListActivity, MusicDetailActivity::class.java)
                intent.putExtra("musicInfo", music)
                startActivity(intent)
            }
        })

    }
    // 使用PermissionX向用户申请权限
    fun requestPermission() {
        // 先判断用户的手机android版本，13以上和13以下申请的权限不同
        val androidPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO // Android13 及以上
        else
            Manifest.permission.READ_EXTERNAL_STORAGE // Android13 以下

        PermissionX.init(this)
            .permissions(androidPermission)
            // 这边就不自己写弹窗告知用户权限读取的解释了
            .request { allGranted, grantedList, deniedList ->
                if(allGranted){
                    // 权限申请成功，定义方法扫描手机上的音频
                    loadMusicList()
                }else{
                    // 权限申请失败，弹窗通知
                    Toasty.error(this,"权限申请失败，无法使用音乐功能",Toasty.LENGTH_LONG).show()
                }
            }
    }

    // 扫描手机上的音频文件
    private fun loadMusicList() {
        // 耗时操作在子线程中执行
        thread {
            // 定义queryMusicList方法扫描音频文件
            val musicList = queryMusicList()
            runOnUiThread {
                // 从queryMusicList方法中拿到新列表更新到旧列表中，并更新数据，更新UI
                musicAdapter.updateMusicList(musicList)
            }
        }
    }

    // 扫描获取音频文件，将其添加到列表中，返回列表
    private fun queryMusicList(): List<MusicInfo> {
        // 1.用于存储音频文件的列表
        val musicList = mutableListOf<MusicInfo>()
        // 2.创建ContentResolver实例化
        val contentResolver = contentResolver
        // 4.定义查询条件，查询音频时长大于1分钟的
        val selection = "${MediaStore.Audio.Media.DURATION} > ? "
        // 定义查询条件的参数
        val selectionArgs = arrayOf("60000")

        // 3.调用query方法查询音频，查询需要的字段的数据，获取出来存入list
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, //uri
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATA
            ),// 查询字段即选择获取的数据，与数据类一一对应，查询出来获取存入数据类
            selection, // 查询条件
            selectionArgs, // 查询条件的参数
            null
        )?.use { cursor ->
            // 获取查询结果的列索引，getColumnIndex是查询，后面的是查询的字段
            val idColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val durationColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val sizeColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            val dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            // 遍历cursor，根据列索引，获取对应字段的数据，并存入list中
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumnIndex)
                val title = cursor.getString(titleColumnIndex)
                val artist = cursor.getString(artistColumnIndex)
                val duration = cursor.getLong(durationColumnIndex)
                val size = cursor.getLong(sizeColumnIndex)
                val path = cursor.getString(dataColumnIndex)
                // 将获取的数据传入MusicInfo对象创建出来musicItem，并添加到列表中
                val musicItem = MusicInfo(id, title, artist, duration, size, path)
                musicList.add(musicItem)
            }
        }

        // 测试播放网络音乐
        val testMusicItem = MusicInfo(
            id = 999,
            title = "真的爱你",
            artist = "beyond",
            duration = 275000,
            size = 0, // 暂时不管
            // 模拟器专用地址
            //path = "http://10.0.2.2:8081/zhendeaini.mp3"
            // 真机专用地址，用电脑的IP地址
            path = "http://192.168.56.1:8081/zhendeaini.mp3"
        )
        // 再将这单首的网络音频添加到列表中
        musicList.add(testMusicItem)

        return musicList
    }
}