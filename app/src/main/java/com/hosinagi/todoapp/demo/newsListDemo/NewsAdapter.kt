package com.hosinagi.todoapp.demo.newsListDemo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hosinagi.todoapp.databinding.ItemNewsBinding
import com.hosinagi.todoapp.demo.netDemo.New

class NewsAdapter(val list: MutableList<New>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    // ViewHolder 缓存item视图,用了binding.root此时就代表item的根视图
    class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    // 创建ViewHolder，返回ViewHolder缓存item视图,用了binding.root此时就代表item的根视图
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        Log.d("调试","onCreateViewHolder : 调用了")
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    // 绑定数据，根据下标position获取数据并绑定到ViewHolder上
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        Log.d("调试","onBindViewHolder : 位置${position}调用了")
        // 获取当前Item的数据，根据下标
        val newItem = list[position]
        // 此时holder：NewsViewHolder就相当于item的视图容器，通过holder.binding可以操作item的视图组件
        holder.binding.titleTextView.text = newItem.title
        holder.binding.contentTextView.text = newItem.hint
        // Glide展示图片
        Glide
            .with(holder.itemView.context)
            .load(newItem.thumbnail)
            .into(holder.binding.newsImageView)
    }

    // 返回item数量，容器一共有多少条数据
    override fun getItemCount(): Int = list.size

    // 创建一个将新闻数据更新到容器中的方法
    fun updateNews(news: List<New>) {
        list.clear()
        list.addAll(news)
        notifyDataSetChanged()//通知容器数据更新了
    }


}