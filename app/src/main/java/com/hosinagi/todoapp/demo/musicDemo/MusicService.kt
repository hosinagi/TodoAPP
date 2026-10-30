package com.hosinagi.todoapp.demo.musicDemo

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {
    companion object {
        const val ACTION_MUSIC_END = "com.hosinagi.todoapp.action.MUSIC_END"
    }

    // 创建Binder对象，提供方法返回MusicService给页面提供交互
    // 必须返回上一次使用的Binder对象，否则一直新建Binder，拿不到数据
    // inner内部类可以拿到外部类的this
    inner class MusicBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }
    // 创建MusicBinder实例化对象
    private val musicBinder = MusicBinder()

    // 创建MediaPlayer，可为null
    private var mediaPlayer : MediaPlayer? = null

    // 当前音乐播放路径
    private var currentPath : String? = null

    // 给客户端提供播放音乐的方法，需要传入音乐路径path
    fun playMusic(path : String) {
        val player = mediaPlayer ?: return
        if (currentPath != path) {
            currentPath = path
            player.reset()
            if (path.startsWith("content://")) {
                player.setDataSource(applicationContext, Uri.parse(path))
            } else {
                player.setDataSource(path)
            }
            player.prepare()
        }
        if (!player.isPlaying) {
            if (player.duration > 0 && player.currentPosition >= player.duration - 200) {
                player.seekTo(0)
            }
            player.start()
        }
    }

    // 给客户端提供暂停音乐的方法
    fun pauseMusic() {
        // 播放中才能暂停，通过isPlaying判断
        if(mediaPlayer?.isPlaying == true){
            mediaPlayer?.pause() // 暂停播放
        }
    }

    // 给客户端提供暂停后继续音乐的方法
    fun resumeMusic() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                if (player.duration > 0 && player.currentPosition >= player.duration - 200) {
                    player.seekTo(0)
                }
                player.start()
            }
        }
    }

    // 获取当前播放状态
    fun isPlaying() : Boolean = mediaPlayer?.isPlaying ?: false
    // 获取音乐的总时长
    fun getDuration() : Int = mediaPlayer?.duration ?: 0
    // 获取当前播放进度
    fun getCurrentPosition() : Int = mediaPlayer?.currentPosition ?: 0

    // 跳转到指定位置播放，需要传入要跳转的位置position
    fun seekTo(position : Int) {
        mediaPlayer?.seekTo(position) // 跳转到指定位置播放
    }

    override fun onCreate() {
        super.onCreate()
        // 创建MediaPlayer实例对象
        mediaPlayer = MediaPlayer()
        // 设置播放完成的监听器
        mediaPlayer!!.setOnCompletionListener {
            // 触发则播放完成，用广播传递播放完成的信息
            val intent = Intent(ACTION_MUSIC_END).setPackage(packageName)
            sendBroadcast(intent)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder {
        return musicBinder
    }
    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        // 释放MediaPlayer资源
        mediaPlayer?.release()
        mediaPlayer = null
    }



}
