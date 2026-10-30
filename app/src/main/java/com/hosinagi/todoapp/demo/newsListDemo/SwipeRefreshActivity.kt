package com.hosinagi.todoapp.demo.newsListDemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivitySwipeRefreshBinding
import com.hosinagi.todoapp.demo.netDemo.NewsBean
import com.hosinagi.todoapp.demo.netDemo.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class SwipeRefreshActivity : AppCompatActivity() {
    // 创建Binding
    private lateinit var binding: ActivitySwipeRefreshBinding
    // 创建Adapter对象
    private lateinit var adapter: NewsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySwipeRefreshBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 创建adapter实例化
        adapter = NewsAdapter(mutableListOf())
        // 将adapter设置给recyclerView
        binding.recyclerView.adapter = adapter
        // 设置列表方向（代码方式）
        //binding.recyclerView.layoutManager = LinearLayoutManager(this)
        // 设置下拉刷新监听器
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchNews() // 重新获取数据
        }
        // 自定义转圈刷新的样式颜色
        binding.swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        // 设置刷新状态为true
        binding.swipeRefreshLayout.isRefreshing = true
        // 调用fetchNews()方法拿到数据
        fetchNews()

    }

    // 使用retrofit网络请求获取新闻数据
    fun fetchNews() {
        Handler(Looper.getMainLooper()).postDelayed({
            // 使用之前定义好的RetrofitClient接口newsApi进行网络请求
            RetrofitClient.newsApi.getNews().enqueue(object : retrofit2.Callback<NewsBean> {
                override fun onResponse(call: Call<NewsBean?>, response: Response<NewsBean?>) {
                    // 停止刷新
                    binding.swipeRefreshLayout.isRefreshing = false
                    // 请求失败
                    if (!response.isSuccessful) {
                        Log.e("请求失败", "onResponse ${response.code()}")
                        return
                    }
                    // 请求成功
                    val newsBean = response.body()
                    newsBean?.let { adapter.updateNews(newsBean.news) }

                }

                override fun onFailure(call: Call<NewsBean?>, t: Throwable) {
                    // 停止刷新
                    binding.swipeRefreshLayout.isRefreshing = false
                    Log.e("请求失败", "onFailure ${t.message}")
                }
            })
        }, 2000)

    }
}