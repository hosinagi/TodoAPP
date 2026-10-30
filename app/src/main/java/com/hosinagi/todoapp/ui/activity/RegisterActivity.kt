package com.hosinagi.todoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.app.db.DataBaseHelper
import com.hosinagi.todoapp.databinding.ActivityRegisterBinding
import es.dmoral.toasty.Toasty

class RegisterActivity : AppCompatActivity() {
    // 创建ActivityRegisterBinding，绑定组件和布局
    private lateinit var binding: ActivityRegisterBinding
    // 创建DataBaseHelper数据库类，用于数据库操作
    private lateinit var dataBaseHelper: DataBaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 初始化binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 默认的Padding设置
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // 初始化数据库实例
        dataBaseHelper = DataBaseHelper(this)
        // 注册按钮点击事件
        binding.registerBtn.setOnClickListener {
            // 逻辑定义在外部函数，保持整洁
            register()
        }
        // 已有账号，跳转登录页面点击事件
        binding.jumpToLogin.setOnClickListener {
            val intent : Intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            // 关闭注册页面
            finish()
        }

        // 设置输入框监听器
        setupListeners()
    }

    // 注册功能逻辑
    private fun register() {
        //1. 获取输入框内容
        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val passwordAgain = binding.passwordAgainInput.text.toString()

        //2. 非空校验
        when {
            username.isEmpty() -> { binding.usernameLayout.error = "用户名不能为空" }
            password.isEmpty() -> { binding.passwordLayout.error = "密码不能为空" }
            passwordAgain.isEmpty() -> { binding.passwordAgainLayout.error = "请再次输入密码" }
            password != passwordAgain -> { binding.passwordAgainLayout.error = "两次输入的密码不一致" }
            // 判断密码长度是否在6到12位之间
            password.length !in 6..20 -> {
                binding.passwordLayout.error = "密码长度在6-20个字符之间"
                binding.passwordAgainLayout.error = "密码长度在6-20个字符之间"
            }
            // 检查用户是否已经注册
            dataBaseHelper.checkUserExists(username) -> {
                Toasty.error(this, "此用户名已存在", Toasty.LENGTH_SHORT).show()
            }
            // 这里else表示以上所有错误逻辑都不符合，代表符合注册规范,实现注册功能
            else -> {
                // 将用户信息通过registerUser方法写入到数据库中
                val result = dataBaseHelper.registerUser(username, password)
                // 用返回值判断是否注册成功
                if(result) {
                    // 注册成功，弹窗提示，并实现跳转到登录页
                    Toasty.success(this, "注册成功", Toasty.LENGTH_SHORT).show()
                    // 跳转到登录页
                    val intent : Intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    // 关闭当前注册页
                    finish()
                }else{
                    Toasty.error(this, "注册失败", Toasty.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 监听器内部逻辑
    private fun setupListeners() {
        // doAfterTextChanged(),用户操作一次，触发一次逻辑
        binding.usernameInput.doAfterTextChanged {
            // 用户框监听
            // 这里it表示输入框内内容，isNullOrEmpty表示内容为空,取反
            if(!it.isNullOrEmpty() && binding.usernameLayout.error != null) {
                // 输入框内容不为空，且错误提示不为空，则清空用户名错误
                binding.usernameLayout.error = null
            }
        }
        binding.passwordInput.doAfterTextChanged {
            // 密码框监听
            if(!it.isNullOrEmpty() && binding.passwordLayout.error != null) {
                binding.passwordLayout.error = null
            }
        }
        binding.passwordAgainInput.doAfterTextChanged {
            // 确认密码框监听
            if(!it.isNullOrEmpty() && binding.passwordAgainLayout.error != null) {
                binding.passwordAgainLayout.error = null
            }
        }
    }
}