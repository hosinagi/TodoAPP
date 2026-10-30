package com.hosinagi.todoapp.demo.musicDemo

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityVideoViewBinding

class VideoViewActivity : AppCompatActivity() {
    // binding
    private lateinit var binding: ActivityVideoViewBinding
    // ExoPlayer
    private var exoPlayer: ExoPlayer? = null

    // 定义播放视频地址
    private val videoUri = "http://10.0.2.2:8081/xiyouji.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // VideoView 播放视频
        // 初始化videoView
        //binding.videoView.setVideoPath(videoUri) // 设置本地视频文件路径方法
        binding.videoView.setVideoURI(Uri.parse(videoUri)) // 设置网络视频/本地视频 地址方法

        // 设置点击事件，VideoView播放视频
        binding.btnVideoView.setOnClickListener {
            // 通过VideoView的start()方法播放视频
            binding.videoView.start()
            // 为了实现两个播放器的互斥，需要暂停另一个播放器
            exoPlayer?.pause()
        }

        // MediaController控制视频播放，初始化
        val mediaController = MediaController(this)
        // 实现media Controller和videoView的关联，双向绑
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)

        // ExoPlayer 播放视频

        // 创建ExoPlayer实例，使用apply在其中设置一些属性
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            // 设置数据源
            setMediaItem(MediaItem.fromUri(videoUri))
            // 准备
            prepare()

        }
        // 将ExoPlayer绑定到PlayerViewUI控件上
        binding.exoPlayerView.player = exoPlayer

        // 设置点击事件，开始播放
        binding.btnExoPlayer.setOnClickListener {
            exoPlayer?.play() // 开始播放
            // 为了实现两个播放器的互斥，需要暂停另一个播放器
            binding.videoView.pause()
        }

    }
    // onPause生命周期的语义是失去焦点/不再是与用户交互的主窗口
    override fun onPause() {
        super.onPause()
        // 当Activity退出页面时，暂停播放
        binding.videoView.pause()
        exoPlayer?.pause()
    }

    //释放资源
    override fun onDestroy() {
        super.onDestroy()
        // 使用exoPlayer自带的release()方法释放资源
        exoPlayer?.release()
        exoPlayer = null // 释放引用
    }
}