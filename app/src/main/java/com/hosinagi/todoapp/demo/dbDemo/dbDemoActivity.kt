package com.hosinagi.todoapp.demo.dbDemo

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.app.db.DataBaseHelper
import es.dmoral.toasty.Toasty

class dbDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_db_demo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    // 注册按钮点击事件
    fun registerClick(view: View) {
        val helper = DataBaseHelper(this)
        val result = helper.registerUser("test1", "123456")
        if(result) {
            Toasty.success(this,"注册成功", Toasty.LENGTH_SHORT).show()
        }else{
            Toasty.error(this,"注册失败", Toasty.LENGTH_SHORT).show()
        }
    }
    // 查询按钮点击事件
    fun queryClick(view: View) {
        val helper = DataBaseHelper(this)
        val result = helper.checkUserExists("test1")
        if(result) {
            Toasty.success(this,"用户名已存在", Toasty.LENGTH_SHORT).show()
        }else{
            Toasty.error(this,"用户名不存在", Toasty.LENGTH_SHORT).show()
        }
    }
    // 更新按钮点击事件
    fun updateClick(view: View) {
        val helper = DataBaseHelper(this)
        val resultString = helper.updateUser("test1", "000000")
        Toasty.success(this, resultString, Toasty.LENGTH_SHORT).show()
    }
    // 删除按钮点击事件
    fun deleteClick(view: View) {
        val helper = DataBaseHelper(this)
        val resultString = helper.deleteUser("test1")
        Toasty.success(this,resultString, Toasty.LENGTH_SHORT).show()
    }

    // 查询所有用户信息按钮点击事件
    fun allQueryClick(view: View) {
        val helper = DataBaseHelper(this)
        val result = helper.queryAllUsers()
        println(result)
    }

    // 登录按钮点击事件
    fun loginClick(view: View) {
        val helper = DataBaseHelper(this)
        val result = helper.login("test2", "123456")
        if(result) {
            Toasty.success(this,"登录成功", Toasty.LENGTH_SHORT).show()
        }else{
            Toasty.error(this,"登录失败", Toasty.LENGTH_SHORT).show()
        }
    }

}