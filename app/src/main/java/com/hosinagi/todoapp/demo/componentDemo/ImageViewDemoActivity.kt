package com.hosinagi.todoapp.demo.componentDemo

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.hosinagi.todoapp.R

class ImageViewDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_image_view_demo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 绑定组件和变量
        var image3 = findViewById<ImageView>(R.id.Image3)
        // 使用Glide加载图片，实现三个方法
        Glide
            .with(this)
            .load("https://ts2.tc.mm.bing.net/th/id/OIP-C.cFLsuEYcB58go30W7K7qRAHaEo?r=0&rs=1&pid=ImgDetMain&o=7&rm=3")
            .into(image3)
    }
}