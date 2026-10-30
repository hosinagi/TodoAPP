package com.hosinagi.todoapp.demo.broadcastDemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityBroadcastTwoBinding

class BroadcastTwoActivity : AppCompatActivity() {
    // binding
    private lateinit var binding: ActivityBroadcastTwoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBroadcastTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 发送广播点击事件
        binding.btnSendBroadcast.setOnClickListener {
            // 构建Intent用action打上标记，用于识别意图
            val intent = Intent("com.hosinagi.message")
            // 添加要传递的信息
            intent.putExtra("message", "Jiang")
            // 官方API，发送广播
            sendBroadcast(intent)

        }
    }
}