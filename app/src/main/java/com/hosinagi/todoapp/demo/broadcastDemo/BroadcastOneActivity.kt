package com.hosinagi.todoapp.demo.broadcastDemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityBroadcastOneBinding
import es.dmoral.toasty.Toasty

class BroadcastOneActivity : AppCompatActivity() {
    // binding
    private lateinit var binding: ActivityBroadcastOneBinding
    // 页面广播接收者 BroadcastReceiver
    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // 判断广播是否是自己要的，通过action判断，只接受符合action的广播
            if("com.hosinagi.message" == intent.action){
                // 获取广播中的信息
                val message = intent.getStringExtra("message")
                binding.tvMessage.text = message
                // 打印一个Toasty，显示接收到信息了
                Toasty.success(this@BroadcastOneActivity, "接收到信息${message}").show()
            }
        }
    }

    // 网络状态变化广播接收者
    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // 编写一个方法，定义网络状态变化的处理逻辑
            updateNetworkStatus()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBroadcastOneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 广播接收器
        registerReceiver()

        // 跳转点击事件
        binding.btnGoToB.setOnClickListener {
            startActivity(Intent(this, BroadcastTwoActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销广播接收者
        unregisterReceiver(messageReceiver)
    }

    // 注册广播接收者
    fun registerReceiver() {
        // 注册广播 页面间通信
        // 当Android版本大于等于13时，需要打上flag，表示广播接收者是导出的，允许其它应用发信息给它
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU){
            // 注册广播接收者
            registerReceiver(messageReceiver, IntentFilter("com.hosinagi.message"), Context.RECEIVER_EXPORTED)
        }else{
            //registerReceiver(messageReceiver, IntentFilter("com.hosinagi.message"))
        }

        // 注册广播 监听网络状态变化
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU){
            // 注册广播接收者
            registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), Context.RECEIVER_EXPORTED)
        }else{
            //registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    // 网络状态变化的处理逻辑
    private fun updateNetworkStatus() {
        // 获取当前的网络状态
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // 获取当前活跃的Network对象，network就表示当前的网络
        val network = cm.activeNetwork

        // 通过服务提供的API，获取网络的能力描述
        val networkCapabilities = cm.getNetworkCapabilities(network)

        // 获取关键能力标志,判断网络是否可用
        val connected = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        // 将网络状态显示在TextView上
        binding.tvNetworkStatus.text = if (connected) "已连接" else "未连接"
    }
}