package com.hosinagi.todoapp.demo.musicDemo

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityMusicDetailBinding
import java.util.Locale

class MusicDetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MUSIC_INFO = "musicInfo"
    }

    // 定义传过来的音乐数据变量
    private var musicInfo: MusicInfo? = null

    // binding
    private lateinit var binding: ActivityMusicDetailBinding

    // 当前是否在播放音乐
    private var isPlaying = false
    private var isBound = false
    private var isReceiverRegistered = false

    // 定义musicService变量接收服务实例
    private var musicService: MusicService? = null

    // Handler获取主线程Looper
    private val handler = Handler(Looper.getMainLooper())

    // 定义广播接收者
    private val receiver = object : BroadcastReceiver() {
        // 符合intentFilter过滤action成功的广播会在这里被接收，数据由Intent携带
        override fun onReceive(context: Context?, intent: Intent?) {
            // 判断广播action是否为"MUSIC_END",是的话音乐播放结束
            if(intent?.action == MusicService.ACTION_MUSIC_END){
                // 播放完成
                isPlaying = false
                updateButton() // 更新按钮样式为播放
                handler.removeCallbacks(runnable)// 结束进度条更新
                binding.seekBar.progress = 0 // 设置进度条进度为0
                binding.tvCurrentTime.text = formatTime(0) // 设置当前播放时间为00:00
            }
        }

    }

    // 构建可执行的Runnable，实现进度条UI的更新和播放时长的UI更新，一秒一次
    private val runnable = object : Runnable{
        override fun run() {
            // 主要更新SeekTo进度条和播放时长的UI
            // 通过自己编写的方法获取当前播放位置，并设置给seekBar的progress属性
            binding.seekBar.progress = musicService?.getCurrentPosition() ?: 0
            // 通过自己编写的方法获取当前播放位置，并设置给tvCurrentTime的text属性
            binding.tvCurrentTime.text = formatTime(musicService?.getCurrentPosition()?.toLong() ?: 0L)
            // 每隔一秒更新一次,每隔一秒发送一次消息
            handler.postDelayed(this, 1000)
        }

    }

    // 实现ServiceConnection对象，绑定服务需要
    private val conn = object : ServiceConnection {
        // 服务连接时调用
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // 获取Binder，这个IBinder就是MusicService.MusicBinder
            val binder = service as MusicService.MusicBinder
            // 通过Binder里的方法，获取MusicService服务实例
            musicService = binder.getService()
            // 播放音乐方法
            startPlayMusic()
        }
        // 服务断开时调用
        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    // 播放音乐方法
    private fun startPlayMusic() {
        musicInfo?.let {
            musicService?.playMusic(it.path)
            // 使用了playMusic方法后就表示当前正在播放音乐，将isPlaying设置为true
            isPlaying = true
            // 获取音乐总时长并设置给seekBar的max属性
            binding.seekBar.max = musicService?.getDuration() ?: 0
            // 播放/暂停按钮的随播放状态样式变化方法
            updateButton()
            // 实现进度条UI的更新和播放时长的UI更新，一秒一次，得用Handler
            updateProgressUpdate()
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMusicDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 获取传过来的音乐数据
        musicInfo = IntentCompat.getParcelableExtra(
            intent,
            EXTRA_MUSIC_INFO,
            MusicInfo::class.java
        )

        // 设置音乐基础信息
        binding.tvTitle.text = musicInfo?.title
        binding.tvArtist.text = musicInfo?.artist
        // 设置音乐总时长，需要转换类型
        binding.tvTotalTime.text = formatTime(musicInfo?.duration ?: 0)
        // 使用绑定服务方法
        bindMusicService()
        // 设置播放/暂停按钮点击事件
        setPlayButtonListener()
        // SeekBar进度条拖动监听
        setSeekBarChangeListener()
        // 注册广播接收者
        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(MusicService.ACTION_MUSIC_END),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        isReceiverRegistered = true
    }

    // 定义方法格式化时间
    fun formatTime(duration: Long): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
    }

    // 绑定服务方法
    private fun bindMusicService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, conn, BIND_AUTO_CREATE)
        isBound = true
        // 在绑定服务中添加后台播放服务,一起启动
        startService(intent)
    }

    // 播放/暂停按钮的样式变化方法
    private fun updateButton() {
        if(isPlaying){
            binding.playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
        }else{
            binding.playPauseButton.setImageResource(android.R.drawable.ic_media_play)
        }
    }
    // 设置播放/暂停按钮点击事件
    private fun setPlayButtonListener() {
        binding.playPauseButton.setOnClickListener {
            if(isPlaying){
                // 正在播放，封装暂停播放方法
                pauseMusic()
            }else{
                // 暂停播放，封装恢复播放方法
                resumeMusic()
            }
        }
    }
    // 暂停播放方法
    private fun pauseMusic() {
        // 使用MediaPlayer的pause方法暂停音乐播放
        musicService?.pauseMusic()
        // 暂停播放后将isPlaying设置为false
        isPlaying = false
        // 执行按钮的更新UI方法，根据isPlaying重新设置播放图标
        updateButton()
        // 停止进度条UI的更新，用handler移除更新UI任务
        handler.removeCallbacks(runnable)

    }
    // 恢复播放方法
    private fun resumeMusic() {
        // 使用MediaPlayer的start方法恢复音乐播放
        musicService?.resumeMusic()
        // 恢复播放后将isPlaying设置为true
        isPlaying = true
        // 执行按钮的更新UI方法，根据isPlaying重新设置播放图标
        updateButton()
        // 重新启动handler的投递信息，和第一次投递一样
        updateProgressUpdate()
    }

    // 实现进度条UI的更新和播放时长的UI更新，一秒一次，得用Handler
    private fun updateProgressUpdate() {
        // 第一次执行runnable，后续都通过逻辑体内handler.postDelayed(this, 1000)来实现隔秒执行
        // handler此时表示主线程Looper，post方法会将runnable放入主线程消息队列
        handler.post(runnable)
    }

    // SeekBar进度条拖动监听
    private fun setSeekBarChangeListener() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            // 进度条改变时触发
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 只有当是用户拖动时才去更新播放时长的UI显示
                if(fromUser){
                    // 只要拖动就会触发逻辑，每次拖动就每次触发重新设置显示的播放时长
                    binding.tvCurrentTime.text = formatTime(progress.toLong())
                }
            }
            // 进度条开始拖动时触发
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 开始拖动时先暂时停止进度条UI每隔一秒一次的更新
                handler.removeCallbacks(runnable)
            }
            // 进度条停止拖动时触发
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 获取停止拖动时的进度位置，即当前播放位置
                val progressPosition = seekBar?.progress ?: 0
                // 根据当前播放位置，使用seekTo()方法跳跃到指定位置播放
                musicService?.seekTo(progressPosition)

                // 停止拖动时根据播放状态，重新启动进度条UI每隔一秒一次的更新
                if(isPlaying){
                    // 正在播放，重新启动进度条UI每隔一秒一次的更新
                    updateProgressUpdate()
                }
            }
        })
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        if (isReceiverRegistered) {
            unregisterReceiver(receiver)
            isReceiverRegistered = false
        }
        if (isBound) {
            unbindService(conn)
            isBound = false
        }
        super.onDestroy()
    }

}
