package com.hosinagi.todoapp.demo.newsListDemo

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityRecyclerViewDemoBinding
import com.hosinagi.todoapp.demo.netDemo.NewsBean
import com.hosinagi.todoapp.demo.netDemo.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecyclerViewDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerViewDemoBinding

    // 创建Adapter对象
    private lateinit var newsAdapter: NewsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecyclerViewDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 实例化Adapter
        newsAdapter = NewsAdapter(mutableListOf())
        // 将Adapter设置给RecyclerView布局组件
        binding.recyclerView.adapter = newsAdapter
        // 设置RecyclerView的布局方向
        // 线性布局
        //binding.recyclerView.layoutManager = LinearLayoutManager(this)
        // 网格布局
//        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        // 错落网格布局
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        // 调用fetchNews()方法获取新闻数据
        fetchNews()
    }

    // 使用retrofit网络请求获取新闻数据
    fun fetchNews() {
        // 使用之前定义好的RetrofitClient接口newsApi进行网络请求
        RetrofitClient.newsApi.getNews().enqueue(object : retrofit2.Callback<NewsBean> {
            override fun onResponse(call: Call<NewsBean?>, response: Response<NewsBean?>) {
                // 请求失败
                if (!response.isSuccessful) {
                    Log.e("请求失败", "onResponse ${response.code()}")
                    return
                }
                // 请求成功
                val newsBean = response.body()
                newsBean?.let { newsAdapter.updateNews(newsBean.news) }

            }

            override fun onFailure(call: Call<NewsBean?>, t: Throwable) {
                Log.e("请求失败", "onFailure ${t.message}")
            }
        })
    }
}