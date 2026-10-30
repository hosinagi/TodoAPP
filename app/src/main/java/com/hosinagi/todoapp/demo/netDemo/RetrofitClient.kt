package com.hosinagi.todoapp.demo.netDemo

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 设置请求的base_url地址
    private const val BASE_URL = "http://10.0.2.2:8081/"

    // 设置日志拦截器
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 创建okhttp客户端，并添加日志拦截器
    private val okhttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // 创建retrofit客户端
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okhttpClient)
        // 自动将JSON数据转为Kotlin对象（数据类）
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 请求的API，外部从这里去调用
    val newsApi : NewsApi = retrofit.create(NewsApi::class.java)

}