package com.hosinagi.todoapp.demo.componentDemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityRadioButtonDemoBinding
import com.hosinagi.todoapp.utils.ToastUtil

class RadioButtonDemoActivity : AppCompatActivity() {
    lateinit var binding: ActivityRadioButtonDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRadioButtonDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(20, systemBars.top, 20, systemBars.bottom)
            insets
        }
        // 设置基础RadioGroup的监听器
        binding.languageRadioGroup.setOnCheckedChangeListener { group, checkId ->
            // 监听器会监听到选择的RadioButton的ID=checkId
            // 根据Id去写对应的逻辑
            val item = when(checkId){
                R.id.kotlinRadioButton -> "Kotlin"
                R.id.javaRadioButton -> "Java"
                R.id.pythonRadioButton -> "Python"
                R.id.cppRadioButton -> "C++"
                else -> "未选择"
            }
            // 将选择的选项显示在TextView中
            binding.selectedLanguageTextView.text = "你选择的编程语言是：$item"
        }

        // 设置自定义RadioGroup的监听器
        binding.customStyleRadioGroup.setOnCheckedChangeListener { group, checkId ->
            // 监听器会监听到选择的RadioButton的ID=checkId
            val item = when(checkId){
                R.id.option1RadioButton -> "运动健身"
                R.id.option2RadioButton -> "阅读写作"
                R.id.option3RadioButton -> "音乐艺术"
                else -> "未选择"
            }
            // 将选择的选项显示在TextView中
            binding.customStyleRadioGroupTextView.text = "你选择的兴趣爱好是：$item"
        }
    }
}