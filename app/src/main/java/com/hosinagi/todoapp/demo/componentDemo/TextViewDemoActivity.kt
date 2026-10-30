package com.hosinagi.todoapp.demo.componentDemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityTextViewDemoBinding

class TextViewDemoActivity : AppCompatActivity() {

    lateinit var binding: ActivityTextViewDemoBinding //定义binding类型

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTextViewDemoBinding.inflate(layoutInflater) // 初始化binding
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.TextV1.text = "hello world2"

        //设置跑马灯
        binding.TextV2.isSelected = true //Kotlin写法
        // binding.TextV2.setSelected(true) Java写法

    }
}