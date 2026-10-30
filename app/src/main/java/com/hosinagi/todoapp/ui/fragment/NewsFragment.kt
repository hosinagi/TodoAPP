package com.hosinagi.todoapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.FragmentNewsBinding
import com.hosinagi.todoapp.demo.netDemo.NewsBean
import com.hosinagi.todoapp.demo.netDemo.RetrofitClient
import com.hosinagi.todoapp.demo.newsListDemo.NewsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter
    private var newsCall: Call<NewsBean>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        // 根目录root就是布局
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsAdapter = NewsAdapter(mutableListOf())
        binding.newsList.layoutManager = LinearLayoutManager(requireContext())
        binding.newsList.adapter = newsAdapter
        binding.swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchNews(showLoading = false)
        }
        binding.retryButton.setOnClickListener {
            fetchNews(showLoading = true)
        }
        fetchNews(showLoading = true)
    }

    private fun fetchNews(showLoading: Boolean) {
        if (_binding == null) return
        if (showLoading) {
            binding.loadingIndicator.isVisible = true
            binding.newsList.isVisible = false
            binding.emptyState.isVisible = false
        }
        binding.errorState.isVisible = false

        newsCall?.cancel()
        newsCall = RetrofitClient.newsApi.getNews().also { call ->
            call.enqueue(object : Callback<NewsBean> {
                override fun onResponse(call: Call<NewsBean>, response: Response<NewsBean>) {
                    if (_binding == null) return
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.loadingIndicator.isVisible = false
                    if (!response.isSuccessful) {
                        showLoadError("服务返回错误：${response.code()}")
                        return
                    }

                    val news = response.body()?.news.orEmpty()
                    newsAdapter.updateNews(news)
                    binding.newsList.isVisible = news.isNotEmpty()
                    binding.emptyState.isVisible = news.isEmpty()
                    binding.errorState.isVisible = false
                }

                override fun onFailure(call: Call<NewsBean>, throwable: Throwable) {
                    if (_binding == null || call.isCanceled) return
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.loadingIndicator.isVisible = false
                    showLoadError("无法连接资讯服务，请确认服务端已启动")
                }
            })
        }
    }

    private fun showLoadError(message: String) {
        binding.newsList.isVisible = false
        binding.emptyState.isVisible = false
        binding.errorState.isVisible = true
        binding.errorMessage.text = message
    }

    override fun onDestroyView() {
        newsCall?.cancel()
        newsCall = null
        binding.newsList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
