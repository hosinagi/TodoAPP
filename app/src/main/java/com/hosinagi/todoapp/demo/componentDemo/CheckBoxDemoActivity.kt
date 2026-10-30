package com.hosinagi.todoapp.demo.componentDemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityCheckBoxDemoBinding
import com.hosinagi.todoapp.utils.ToastUtil
import es.dmoral.toasty.Toasty


class CheckBoxDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckBoxDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckBoxDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 音乐CheckBox监听器
        binding.checkBoxMusic.setOnCheckedChangeListener { buttonView, isChecked ->
            ToastUtil.showToast("音乐：$isChecked")
        }
        // 运动CheckBox监听器
        binding.checkBoxSports.setOnCheckedChangeListener { buttonView, isChecked ->
            ToastUtil.showToast("运动：$isChecked")
        }
        // 阅读CheckBox监听器
        binding.checkBoxReading.setOnCheckedChangeListener { buttonView, isChecked ->
            ToastUtil.showToast("阅读：$isChecked")
        }
        // 旅行CheckBox监听器
        binding.checkBoxTravel.setOnCheckedChangeListener { buttonView, isChecked ->
            ToastUtil.showToast("旅行：$isChecked")
        }
    }
}