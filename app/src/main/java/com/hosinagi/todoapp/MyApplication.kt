package com.hosinagi.todoapp

import android.app.Application
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.BezierRadarHeader
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

// 定义一个变量延迟初始化去表示MyApplication.instance
val appContext by lazy { MyApplication.instance }

// MyApplication本质上就是一个Context
class MyApplication : Application() {

    companion object {
        // 这个instance的类型就是MyApplication，就是Context
        lateinit var instance: MyApplication

        // 初始化块
        init {
            // 配置全局的下拉刷新样式
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                BezierRadarHeader(appContext)
            }
            // 配置全局的上拉加载样式j
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                ClassicsFooter(appContext)
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}