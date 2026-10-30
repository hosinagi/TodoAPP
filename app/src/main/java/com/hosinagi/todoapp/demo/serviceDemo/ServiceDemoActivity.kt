package com.hosinagi.todoapp.demo.serviceDemo

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityServiceDemoBinding
import es.dmoral.toasty.Toasty

class ServiceDemoActivity : AppCompatActivity() {
    // binding
    private lateinit var binding: ActivityServiceDemoBinding
    // myService
    private var myService : MyService? = null

    // ServiceConnection
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // 服务连接成功
            val binder = service as MyService.MyBinder
            // 执行MyBinder中的getService方法，返回MyService实例
            myService = binder.getService()
            // Toasty打印服务连接成功
            Toasty.success(this@ServiceDemoActivity, "服务连接成功").show()
        }
        // 正常情况下不会触发，因为服务被销毁，conn也不存在，逻辑无法执行
        override fun onServiceDisconnected(name: ComponentName?) {
            // 服务断开连接
            myService = null
            Toasty.error(this@ServiceDemoActivity, "服务断开连接").show()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityServiceDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 点击事件总和方法
        setClickListener()
    }

    private fun setClickListener() {
        // 启动服务点击事件
        binding.btnStartService.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            startService(intent)
        }
        // 停止服务点击事件
        binding.btnStopService.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            stopService(intent)
        }
        // 绑定服务启动点击事件
        binding.btnBindService.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            bindService(intent, conn, BIND_AUTO_CREATE)
        }
        // 解绑服务点击事件
        binding.btnUnbindService.setOnClickListener {
            // 不能重复解绑，判断myService不为null时才去解绑
            if(myService != null){
                unbindService(conn)
                // 在点击事件中去完成myService = null的操作
                myService = null
            }
        }
        // 获取服务数据点击事件
        binding.btnGetServiceData.setOnClickListener {
            if(myService != null) {
                // myService不为null时表示服务存在
                binding.tvServiceData.text = myService!!.getCount().toString()
            }else{
                Toasty.error(this@ServiceDemoActivity, "请先绑定服务").show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑服务
        if(myService != null){
            unbindService(conn)
        }
    }
}