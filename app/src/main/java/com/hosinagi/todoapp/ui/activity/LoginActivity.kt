package com.hosinagi.todoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.app.db.DataBaseHelper
import com.hosinagi.todoapp.databinding.ActivityLoginBinding
import com.hosinagi.todoapp.utils.SessionManager
import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity() {
    // 创建Binding绑定组件和变量,并在onCreate中初始化
    private lateinit var binding: ActivityLoginBinding
    // 创建DataBaseHelper数据库类对象，调用数据库功能
    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(30, systemBars.top, 30, systemBars.bottom)
//            insets
//        }
        // 数据库对象实例化
        dbHelper = DataBaseHelper(this)
        // 输入框监听封装,用于实时消除错误提示
        setupListeners()
        // 设置登录按钮点击事件
        binding.btnLogin.setOnClickListener {
            // 封装登录方法
            login()
        }
        // 设置跳转注册按钮点击事件
        binding.jumpToRegister.setOnClickListener {
            val intent : Intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // 输入框监听方法
    private fun setupListeners() {
        // 用户名输入框监听
        binding.usernameInput.doAfterTextChanged {
            if(!it.isNullOrEmpty() && binding.usernameLayout.error != null) {
                binding.usernameLayout.error = null
            }
        }
        // 密码输入框监听
        binding.passwordInput.doAfterTextChanged {
            if(!it.isNullOrEmpty() && binding.passwordLayout.error != null) {
                binding.passwordLayout.error = null
            }
        }
    }

    // 登录方法
    private fun login() {
        // 获取输入框的内容并赋值给变量
        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()
        // 非空校验
        // 这里username永远不可能为null，所以不用isNullOrEmpty
        if(username.isEmpty()) {
            binding.usernameLayout.error = "用户名不能为空"
            return // 为空则结束程序
        }
        if(password.isEmpty()) {
            binding.passwordLayout.error = "密码不能为空"
            return
        }
        // 与注册页保持一致，密码长度为6-20位
        if(password.length !in 6..20) {
            binding.passwordLayout.error = "密码长度在6-20个字符之间"
            return
        }
        // 通过校验，开始登录
        val result = dbHelper.login(username,password)
        if(result){
            // 登录成功后同时保存登录状态和当前用户名
            SessionManager.saveLogin(this, username)
            // result为true，登录成功，弹窗提示
            Toasty.success(this, "登录成功", Toasty.LENGTH_SHORT).show()
            // 跳转到主页面
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            // 关闭销毁当前登录页面
            finish()
        }else{
            // result为false，登录失败，弹窗提示
            Toasty.error(this, "用户名或密码错误", Toasty.LENGTH_SHORT).show()
        }
    }
}
