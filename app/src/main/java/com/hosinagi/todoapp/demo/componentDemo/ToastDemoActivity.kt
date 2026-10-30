package com.hosinagi.todoapp.demo.componentDemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityToastDemoBinding
import com.hosinagi.todoapp.utils.ToastUtil
import es.dmoral.toasty.Toasty

class ToastDemoActivity : AppCompatActivity() {
    private lateinit var binding : ActivityToastDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityToastDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.normalToastBtn.setOnClickListener {
            Toast.makeText(this,"Normal Toast",Toast.LENGTH_LONG).show()
        }

        //success
        binding.ToastyBtn1.setOnClickListener {
            Toasty.success(this, "登录失败", Toast.LENGTH_SHORT, true).show()
        }

        // error
        binding.ToastyBtn2.setOnClickListener {
            Toasty.error(this, "登录失败", Toast.LENGTH_SHORT,true).show()
        }

        // info
        binding.ToastyBtn3.setOnClickListener {
            Toasty.info(this, "info提示", Toast.LENGTH_SHORT, true).show()
        }

        // warning
        binding.ToastyBtn4.setOnClickListener {
            Toasty.warning(this, "warning提示", Toast.LENGTH_SHORT, true).show()
        }

        // normal
        binding.ToastyBtn5.setOnClickListener {
            Toasty.normal(this, "normal提示", Toast.LENGTH_SHORT).show()
        }

        // 封装的Toast用于演示上下文
        binding.ToastyBtn6.setOnClickListener {
            ToastUtil.showToast("登录成功")
        }
    }
}