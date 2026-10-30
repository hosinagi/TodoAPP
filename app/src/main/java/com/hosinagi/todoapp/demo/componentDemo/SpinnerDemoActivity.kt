package com.hosinagi.todoapp.demo.componentDemo

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigation.NavigationBarView
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivitySpinnerDemoBinding
import com.hosinagi.todoapp.utils.ToastUtil

class SpinnerDemoActivity : AppCompatActivity() {
    // 创建binding
    private lateinit var binding: ActivitySpinnerDemoBinding

    // 是否第一次进入页面
    private var isFirst = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySpinnerDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 设置静态下拉选择框的监听器
        binding.spinnerStatic.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 选中时的监听
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 第一次进入页面时触发逻辑不执行，改为false后续可以执行
                if (isFirst) {
                    isFirst = false
                    return
                }
                // 获取用户当前选中的选项
                val item = parent?.getItemAtPosition(position)
                // 打印一下当前选中项
                ToastUtil.showToast(item.toString())
            }

            // 未选中时的监听
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // 动态加载下拉选择框的选项
        val hobby = arrayOf("阅读", "运动", "音乐", "电影", "游戏")
        // 创建适配器
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hobby)
        // 改布局
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // 设置适配器
        binding.spinnerDynamic.adapter = adapter
        // 设置动态下拉选择框的监听器
        binding.spinnerDynamic.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = hobby[position]
                ToastUtil.showToast(item)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
}