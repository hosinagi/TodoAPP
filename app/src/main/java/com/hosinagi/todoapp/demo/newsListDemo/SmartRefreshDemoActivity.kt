package com.hosinagi.todoapp.demo.newsListDemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivitySmartRefreshDemoBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

class SmartRefreshDemoActivity : AppCompatActivity() {
    // 创建binding对象
    private lateinit var binding: ActivitySmartRefreshDemoBinding

    // 创建Adapter对象
    private lateinit var adapter: SmartRefreshAdapter

    // 当前页数
    private var page : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySmartRefreshDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 创建Adapter的实例化
        adapter = SmartRefreshAdapter(mutableListOf())
        // 将Adapter绑定到RecyclerView上
        binding.RecyclerView.adapter = adapter
        // 设置RecyclerView的方向
        binding.RecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 下拉刷新的监听器binding.SmartRefreshLayout.setOnRefreshListener {  }
        // 上拉加载的监听器binding.SmartRefreshLayout.setOnLoadMoreListener {  }
        // 设置同时监听下拉刷新和上拉加载的监听器
        binding.SmartRefreshLayout.setOnRefreshLoadMoreListener(object: OnRefreshLoadMoreListener{
            // 下拉刷新的监听器
            override fun onRefresh(refreshLayout: RefreshLayout) {
                page = 1
                loadData(true)
            }
            // 上拉加载的监听器
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                page++
                loadData(false)
            }
        })

        // 自动启动一下下拉刷新
        binding.SmartRefreshLayout.autoRefresh()

    }

    // 写一个简单的获取数据方法
    fun generateData(): List<RefreshDemoItem> {
        val itemCount = if(page == 3) 3 else 10
        return List(itemCount){index ->
            val itemIndex = (page - 1) * 10 + index + 1
            RefreshDemoItem("Title $itemIndex", "Content $itemIndex")}
    }

    // 根据是上拉加载还是下拉刷新将数据使用不同的方法更新到Adapter中
    fun loadData(isRefresh: Boolean) {
        // 当page超过限制时，结束加载更多
        if (!isRefresh && page > 3){
            binding.SmartRefreshLayout.finishLoadMoreWithNoMoreData()
            return
        }

        if (isRefresh) {
            adapter.updateData(generateData())
            binding.SmartRefreshLayout.finishRefresh()// 结束刷新
        } else {
            adapter.loadMoreData(generateData())
            binding.SmartRefreshLayout.finishLoadMore()// 结束加载
        }
    }
}