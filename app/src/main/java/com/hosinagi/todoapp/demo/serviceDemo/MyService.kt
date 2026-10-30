package com.hosinagi.todoapp.demo.serviceDemo

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class MyService : Service() {

    // 定义TAG，用于日志输出
    private val TAG = "MyService"

    // 计数
    private var count = 0
    // 计数是否启动
    private var isRunning = false

    // handler实现计数，创建handler
    private val handler = Handler(Looper.getMainLooper())
    // 创建runnable，可执行任务
    private val runnable = object : Runnable {
        override fun run() {
            count++
            Log.d(TAG,"计数：$count")
            handler.postDelayed(this, 1000)
        }
    }
    // 继承Binder类，创建Binder对象，这边要注意必须传入Service对象
    // 因为getService()要获取的是上一次运行服务的本身，否则每次调用都会新建MyService服务
    // 导致服务运行的数据无法获取
    class MyBinder(private val service: MyService) : Binder() {
        fun getService(): MyService = service
    }
    // 实例化Binder类，传入上一次运行的本身
    private val binder = MyBinder(this)


    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: onBind被执行")
        return binder
    }
    override fun onCreate() {
        Log.d(TAG, "onCreate: onCreate被执行")
        super.onCreate()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: onStartCommand被执行")
        // 这里启动计数功能，每秒计数一次
        startCounting()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind: onUnbind被执行")
        return super.onUnbind(intent)
    }

    // 启动计数功能
    private fun startCounting() {
        // 判断计数是否启动，启动则直接返回，未启动则开启
        if(isRunning) return
        isRunning = true
        handler.post(runnable)
    }

    // 停止计数功能
    private fun stopCounting() {
        isRunning = false
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: onDestroy被执行")
        // Service销毁则停止计数
        stopCounting()
        super.onDestroy()
    }

    // 获取计数器数值
    fun getCount() : Int = count
}