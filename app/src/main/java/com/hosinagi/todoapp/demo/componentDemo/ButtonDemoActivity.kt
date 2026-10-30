package com.hosinagi.todoapp.demo.componentDemo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R

class ButtonDemoActivity : AppCompatActivity(),View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_button_demo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var button2 = findViewById<View>(R.id.button2)
        button2.setOnClickListener {
            Toast.makeText(this,"2222",Toast.LENGTH_SHORT).show()
        }

        var button3 = findViewById<View>(R.id.button3)
        var button4 = findViewById<View>(R.id.button4)
        // 这里的this，代指这个ButtonDemoActivity类，让这个类给这些按钮实现逻辑
        button3.setOnClickListener(this)
        button4.setOnClickListener(this)

    }

    fun Click(view: View) {
        Toast.makeText(this,"1111",Toast.LENGTH_SHORT).show()
    }

    // 实现这个抽象接口
    override fun onClick(v: View) {
        when(v.id){
            R.id.button3 -> Toast.makeText(this,"3333",Toast.LENGTH_SHORT).show()
            R.id.button4 -> Toast.makeText(this,"4444",Toast.LENGTH_SHORT).show()
        }
    }
}