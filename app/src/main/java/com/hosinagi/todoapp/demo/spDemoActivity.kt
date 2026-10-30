package com.hosinagi.todoapp.demo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.hosinagi.todoapp.R
import es.dmoral.toasty.Toasty

class spDemoActivity : AppCompatActivity() {
    // 定义SharePreferences的对象,但没有创建初始化实例对象
//    private lateinit var sp : SharedPreferences

    // 使用by lazy 延迟初始化
    private val sp: SharedPreferences by lazy { getSharedPreferences("sp_demo", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sp_demo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 在onCreate中初始化，创建实例化对象sp
//        sp = getSharedPreferences("sp_demo", MODE_PRIVATE)
    }
    // sp保存数据
    fun saveData(view: View) {
        val list : List<Person> = listOf(
            Person("张三", 18),
            Person("李四", 19),
            Person("王五", 20)
        )
        // 传统方式
//        val editor = sp.edit()
//        editor.putString("name","张三")
//        editor.putInt("age",18)
//        editor.putBoolean("isMale",true)
//        editor.apply()
        // 使用kotlin扩展方法
        sp.edit {
            putString("name","张三")
            putInt("age",18)
            putBoolean("isMale",true)
//            putString("person", Gson().toJson(Person("李四",18)))
//            putString("personList", Gson().toJson(list))
        }
        Toasty.success(this, "保存成功", Toasty.LENGTH_SHORT).show()
    }

    // sp读取数据
    fun readData(view: View) {
        val name = sp.getString("name", "null")
        val age = sp.getInt("age", 0)
        val isMale = sp.getBoolean("isMale", false)
//        val str = sp.getString("person","")
//        val person = Gson().fromJson(str,Person::class.java)
//        val strlist = sp.getString("personList","")
//        val list = Gson().fromJson(strlist, Array<Person>::class.java)
        Toasty.info(this, "引用列表数据类型：${name}").show()
    }

    // sp清除数据
    fun clearData(view: View) {
        sp.edit {
            // remove删除指定的key
            remove("name")
            // clear删除所有数据
            clear()
        }
    }
}

// Person数据类
data class Person(
    val name: String,
    val age: Int
)