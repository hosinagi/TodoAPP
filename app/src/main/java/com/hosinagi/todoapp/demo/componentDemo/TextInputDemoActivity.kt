package com.hosinagi.todoapp.demo.componentDemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityTextInputDemoBinding

class TextInputDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextInputDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTextInputDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(40, systemBars.top, 40, systemBars.bottom)
            insets
        }

        // 这里对输入位数进行判断的组件是Edit底层组件，因为EditText可以获取到用户的输入，就是此函数中的text
        // onTextChanged就是当用户改变时进行监听，用户输入改变时会触发逻辑
        binding.phoneEdit.doOnTextChanged { text, start, before, count ->
            if (text?.length != 11) {
                // 这里对错误提示的error属性进行赋值，用if触发
                binding.phoneInput.error = "请输入11位手机号码"
            }else{
                binding.phoneInput.error = null
            }
        }
    }
}