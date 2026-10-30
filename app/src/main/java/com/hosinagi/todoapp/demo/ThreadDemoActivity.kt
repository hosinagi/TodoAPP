package com.hosinagi.todoapp.demo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import kotlin.concurrent.thread

class ThreadDemoActivity : AppCompatActivity() {
    // 创建Handler对象
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_thread_demo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // 按钮1点击事件，执行耗时操作
    fun method1(view: View) {
        Thread.sleep(10000)
        Toast.makeText(this,"method1被点击了",Toast.LENGTH_SHORT).show()
    }
    fun method2(view: View) {
        Toast.makeText(this,"method2被点击了",Toast.LENGTH_SHORT).show()
    }
    // 开一个子线程，在子线程中更新UI
    fun method3(view: View) {
        thread {
            val btn = view as Button
            btn.text = "按钮4"
        }
    }
    // 切换到主线程更新UI，Activity.runOnUiThread(Runnable)
    fun method4(view: View) {
        thread {
            val btn = view as Button
            runOnUiThread {
                // 在这里面已经是主线程了
                btn.text = "按钮5"
            }
        }
    }
    // 切换到主线程更新UI，view.post(Runnable)
    fun method5(view: View) {
        thread {
            val btn = view as Button
            view.post {
                // 在这里面已经是主线程了
                btn.text = "按钮6"
            }
        }
    }
    // Handler.post切换到主线程
    fun method6(view: View) {
        thread {
            val btn = view as Button
            handler.post {
                // 主线程
                btn.text = "按钮7"
            }
        }
    }


}