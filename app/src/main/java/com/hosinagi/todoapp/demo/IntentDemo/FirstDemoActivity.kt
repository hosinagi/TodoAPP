package com.hosinagi.todoapp.demo.IntentDemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R

class FirstDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frist_demo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    // 实现Intent页面跳转
    fun Jump1(view: View) {
        // 创建Intent类对象，并传入上下文和要跳转的页面
        val intent : Intent = Intent(this,SecondDemoActivity::class.java)
        // 开启页面跳转
        startActivity(intent)
    }
}