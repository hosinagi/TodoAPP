package com.hosinagi.todoapp.demo.netDemo

import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityNetDemoBinding
import es.dmoral.toasty.Toasty
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import kotlin.concurrent.thread

class NetDemoActivity : AppCompatActivity() {
    // 创建binding对象
    private lateinit var binding: ActivityNetDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNetDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // get同步请求
    fun request1(view: View) {
//        thread {
//            // 1.创建http客户端，创建OKHTTPClient实例
//            val client = OkHttpClient()
//            // 2.创建Request对象
//            val request = Request.Builder()
//                .url("https://www.baidu.com")
//                .build()
//            // 3.发送请求，获取响应结果
//            val response = client.newCall(request).execute()
//            // 4.处理响应结果，并展示到TextView中
//            val result = response.body?.string()
//            // 切换到主线程中更新UI
//            runOnUiThread {
//                binding.contentTv.text = result
//            }
//        }
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://www.baidu.com")
                    .build()
                val response = client.newCall(request).execute()
                response.use {
                    // 服务端正常响应
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        runOnUiThread {
                            binding.contentTv.text = responseBody
                        }
                    }else{
                        Log.e("请求失败", "服务端返回异常")
                    }
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("请求失败", it) }
            }
        }.start()
    }

    // get异步请求
    fun request2(view: View) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://www.baidu.com")
                .build()
            val response = client.newCall(request).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.message?.let { Log.e("请求失败", it) }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        // 服务端正常响应
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            runOnUiThread {
                                binding.contentTv.text = responseBody
                            }
                        }else{
                            Log.e("请求失败", "服务端返回异常")
                        }
                    }
                }
            })

        } catch (e: Exception) {
            e.message?.let { Log.e("请求失败", it) }
        }
    }

    // get请求JSON数据
    fun request3(view: View) {
        try {
            // 0.创建日志拦截器
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY//设置日志级别

            // 1.创建http客户端，创建OKHTTPClient实例
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // 2.创建Request对象，使用Builder模式，设置url地址
            val request = Request.Builder()
                .url("https://v.api.aa1.cn/api/zhihu-news/")
                .build()
            // 3.发送异步请求，获取响应结果
            val response = client.newCall(request).enqueue(object : Callback{
                // 请求失败逻辑
                override fun onFailure(call: Call, e: IOException) {
                    e.message?.let { Log.e("请求失败", it) }
                }

                // 请求成功逻辑
                override fun onResponse(call: Call, response: Response) {
                    // response.use使用完成自动释放response
                    response.use {
                        // 服务端正常响应
                        if (response.isSuccessful) {
                            // 4.处理响应结果，将响应体body=JSON数据，转换为NewsBean对象
                            val responseBody = response.body?.string()
                            // 切换到主线程中更新UI
                            runOnUiThread {
                                val newsBean : NewsBean = Gson().fromJson(responseBody, NewsBean::class.java)
                                Toasty.info(this@NetDemoActivity, newsBean.date).show()
                            }
                        }else{
                            Log.e("请求失败", "服务端返回异常")
                        }
                    }
                }
            })

        } catch (e: Exception) {
            e.message?.let { Log.e("请求失败", it) }
        }
    }

    // get请求服务器接口
    fun request4(view: View) {
        try {
            // 0.创建日志拦截器
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY//设置日志级别

            // 1.创建http客户端，创建OKHTTPClient实例
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // 2.创建Request对象，使用Builder模式，设置url地址
            val request = Request.Builder()
                .url("http://10.0.2.2:8081/user/hello") //自带的模拟器
                //.url("http://192.168.52.1:8081/user/hello") //真机调试
                .build()
            // 3.发送异步请求，获取响应结果
            val response = client.newCall(request).enqueue(object : Callback{
                // 请求失败逻辑
                override fun onFailure(call: Call, e: IOException) {
                    e.message?.let { Log.e("请求失败", it) }
                }

                // 请求成功逻辑
                override fun onResponse(call: Call, response: Response) {
                    // response.use使用完成自动释放response
                    response.use {
                        // 服务端正常响应
                        if (response.isSuccessful) {
                            // 4.处理响应结果，将响应体body=JSON数据，转换为NewsBean对象
                            val responseBody = response.body?.string()
                            // 切换到主线程中更新UI
                            runOnUiThread {
                                binding.contentTv.text = responseBody
//                                val newsBean : NewsBean = Gson().fromJson(responseBody, NewsBean::class.java)
//                                Toasty.info(this@NetDemoActivity, newsBean.date).show()
                            }
                        }else{
                            Log.e("请求失败", "服务端返回异常")
                        }
                    }
                }
            })

        } catch (e: Exception) {
            e.message?.let { Log.e("请求失败", it) }
        }
    }
    // post请求后端接口,表单
    fun request5(view: View) {
        // 1.创建OKHttpClient实例，创建http客户端
        val client = OkHttpClient()
        // 2.创建表单
        val fromBody = FormBody.Builder()
            .add("username","admin")
            .add("password","123456")
            .build()
        // 3.创建Request对象，使用Builder模式，设置url地址和post发送数据
        val request = Request.Builder()
            .url("http://10.0.2.2:8081/user/login")
            .post(fromBody)
            .build()
        // 4.发送异步请求，获取响应结果
        val response = client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.message?.let { Log.e("请求失败", it) }
            }
            // 5.在成功请求逻辑中处理请求结果
            override fun onResponse(call: Call, response: Response) {
                // response.use使用完成自动释放response
                response.use {
                    // 服务端正常响应
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        runOnUiThread {
                            binding.contentTv.text = responseBody
                        }
                    } else {
                        Log.e("请求失败", "服务端返回异常")
                    }
                }
            }
        })
    }
    // post请求后端接口,JSON
    fun request6(view: View) {
        // 1.创建OKHttpClient实例，创建http客户端
        val client = OkHttpClient()
        // 2.构建JSON请求数据
        val json = """
            {
                "username": "admin",
                "password": "123456"
            }
            """
        // 指定JSON的类型，创建JSON请求体
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        // 3.创建Request对象，使用Builder模式，设置url地址和post发送数据
        val request = Request.Builder()
            .url("http://10.0.2.2:8081/user/login2")
            .post(requestBody)
            .build()
        // 4.发送异步请求，获取响应结果
        val response = client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.message?.let { Log.e("请求失败", it) }
            }

            // 5.在成功请求逻辑中处理请求结果
            override fun onResponse(call: Call, response: Response) {
                // response.use使用完成自动释放response
                response.use {
                    // 服务端正常响应
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        runOnUiThread {
                            binding.contentTv.text = responseBody
                        }
                    } else {
                        Log.e("请求失败", "服务端返回异常")
                    }
                }
            }
        })

    }

    // retrofit发送get请求
    fun request7(view: View) {
        RetrofitClient.newsApi.getNews().enqueue(object: retrofit2.Callback<NewsBean> {
            // 请求成功逻辑
            override fun onResponse(
                call: retrofit2.Call<NewsBean?>,
                response: retrofit2.Response<NewsBean?>
            ) {
                val newsBean = response.body()
                if (newsBean != null) {
                    runOnUiThread {
                        binding.contentTv.text = newsBean.date
                    }
                }
            }
            // 请求失败逻辑
            override fun onFailure(
                call: retrofit2.Call<NewsBean?>,
                t: Throwable
            ) {
                //t.printStackTrace() // 打印异常信息日志
                Toasty.error(this@NetDemoActivity, "请求失败").show()
            }
        })
    }

    // retrofit发送post请求，表单
    fun request8(view: View) {
        RetrofitClient.newsApi.loginFrom("admin","111111").enqueue(object: retrofit2.Callback<Any> {
            // 请求成功逻辑
            override fun onResponse(
                call: retrofit2.Call<Any?>,
                response: retrofit2.Response<Any?>
            ) {
                val result = response.body()
                binding.contentTv.text = result.toString()
            }
            // 请求失败逻辑
            override fun onFailure(
                call: retrofit2.Call<Any?>,
                t: Throwable
            ) {
                //t.printStackTrace() // 打印异常信息日志
                Toasty.error(this@NetDemoActivity, "请求失败").show()
            }
        })
    }

    // retrofit发送post请求，JSON
    fun request9(view: View) {
        val user = User("admin","123456")
        RetrofitClient.newsApi.loginJson(user).enqueue(object: retrofit2.Callback<Any> {
            // 请求成功逻辑
            override fun onResponse(
                call: retrofit2.Call<Any?>,
                response: retrofit2.Response<Any?>
            ) {
                val result = response.body()
                binding.contentTv.text = result.toString()
            }
            // 请求失败逻辑
            override fun onFailure(
                call: retrofit2.Call<Any?>,
                t: Throwable
            ) {
                //t.printStackTrace() // 打印异常信息日志
                Toasty.error(this@NetDemoActivity, "请求失败").show()
            }
        })
    }
}