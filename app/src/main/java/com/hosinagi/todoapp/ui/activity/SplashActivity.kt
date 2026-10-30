package com.hosinagi.todoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivitySplashBinding
import com.hosinagi.todoapp.utils.SessionManager
import kotlin.concurrent.thread

class SplashActivity : AppCompatActivity() {
    // 创建binding，以使用组件
    private lateinit var binding: ActivitySplashBinding
    // 创建计时器，需要继承CountDownTimer
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        // 使用字体文件
//        val customFont = Typeface.createFromAsset(assets, "a1.ttf")
//        // 为对应的TextView组件设置字体
//        binding.titleTv.typeface = customFont

        // 调用属性动画函数
        startAnimations()
        // 调用计时器函数
        startDownTime()

        // 打印一下当前的线程
        Log.d("打印日志，当前线程", Thread.currentThread().name)

        // 创建两个子线程打印
        Thread {
            Log.d("打印日志，子线程1", Thread.currentThread().name)
        }
        thread {
            Log.d("打印日志，子线程2", Thread.currentThread().name)
        }
    }

    // 创建计时器函数
    private fun startDownTime() {
        countDownTimer = object : CountDownTimer(3000, 1000) {
            // 每隔1000毫秒（自己定义）触发一次
            override fun onTick(millisUntilFinished: Long) {
                binding.TimeTV.text = ((millisUntilFinished / 1000) + 1).toString()
                binding.TimeTV.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(150)
                    .withEndAction {
                        binding.TimeTV.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .start()
                    }
                    .start()
            }
            // 倒计时结束触发一次,跳转到登录页面
            override fun onFinish() {
                // 根据sp存储的登录状态判断，跳转到登录页面或主页面
                val targetActivity = if (SessionManager.isLoggedIn(this@SplashActivity)) {
                    MainActivity::class.java
                } else {
                    LoginActivity::class.java
                }
                startActivity(Intent(this@SplashActivity, targetActivity))
                finish() // 关闭启动页，以免页面残留
            }
        }.start()
    }

    // 创建属性动画函数
    private fun startAnimations() {
        // 属性动画初始状态设置
        binding.titleTv.apply {
            alpha = 0f // 初始透明度为0，完全透明
            translationY = 500f // 初始Y轴偏移量为500，即初始位置在屏幕下方500
            scaleX = 0.8f // 初始X轴缩放比例为0.8
            scaleY = 0.8f // 初始Y轴缩放比例为0.8
            rotation = 90f // 初始旋转角度为90度
            animate()
                .alpha(1f) // 设置经过动画后的透明度，也就是动画过后的位置
                .translationY(0f) // 设置经过动画后的Y轴偏移量
                .scaleX(1f) // 设置经过动画后的X轴缩放比例
                .scaleY(1f) // 设置经过动画后的Y轴缩放比例
                .rotation(0f) // 设置经过动画后的旋转角度
                .setDuration(1000) // 设置动画时长为1000毫秒，即1秒
                .withEndAction {
                    // 进行回调：动画结束后执行下一段操作（动画或跳转）
                    animate()
                        .scaleX(1.5f)
                        .scaleY(1.5f)
                        .setDuration(800)
                        .setInterpolator(OvershootInterpolator())
                        .start()
                }
                .start() // 启动动画
        }
    }
}
