package com.hosinagi.todoapp.demo.newsListDemo

import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import com.hosinagi.todoapp.databinding.ItemRefreshListBinding

class SmartRefreshAdapter(private val list: MutableList<RefreshDemoItem>) : RecyclerView.Adapter<SmartRefreshAdapter.VH>() {
    // 构建VH类ViewHolder
    class VH(var binding: ItemRefreshListBinding) : RecyclerView.ViewHolder(binding.root)

    // 创建ViewHolder，返回ViewHolder缓存item视图,用了binding.root此时就代表item的根视图
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRefreshListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }
    // 绑定数据，根据下标position获取数据并绑定到ViewHolder上
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.binding.apply {
            titleText.text = item.title
            contentText.text = item.content
        }
    }

    override fun getItemCount(): Int = list.size

    // 下拉刷新或首次加载数据时将数据更新到容器中的方法
    fun updateData(refresh: List<RefreshDemoItem>) {
        list.clear()
        list.addAll(refresh)
        notifyDataSetChanged()// 通知数据更新
    }

    // 上拉加载更多时将数据追加到容器中的方法
    fun loadMoreData(newItem:List<RefreshDemoItem>) {
        // 这时就不用去清空列表了
        val oldSize = list.size
        list.addAll(newItem)
        notifyItemRangeInserted(oldSize, newItem.size)// 通知数据追加
    }
}

// 数据实体类
data class RefreshDemoItem(
    val title: String,
    val content: String
)