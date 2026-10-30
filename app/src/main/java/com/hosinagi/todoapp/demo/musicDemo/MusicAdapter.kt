package com.hosinagi.todoapp.demo.musicDemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hosinagi.todoapp.databinding.ItemMusicBinding
import java.util.Locale

class MusicAdapter : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    // 创建ViewHolder,即创建子布局
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 获取数据项的数量
    override fun getItemCount(): Int = musicList.size

    // 绑定数据到每一项子布局上
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        // 因为binding私有了，无法在此直接绑定数据到布局上
        holder.bind(musicList[position])
    }


    // VH类，用于绑定子布局，绑定数据
    inner class ViewHolder(private val binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root) {
        // 创建bind方法，将数据绑定到binding.root布局上
        fun bind(musicInfo: MusicInfo) {
            binding.tvTitle.text = musicInfo.title
            binding.tvArtist.text = musicInfo.artist
            // 这里的duration是毫秒值，需要一个方法转换成分钟:秒
            binding.tvDuration.text = formatDuration(musicInfo.duration)
            // 设置点击事件跳转到详情页
            binding.root.setOnClickListener {
                // 方式一：使用intent跳转，传递数据
//                val intent = Intent(binding.root.context, MusicDetailActivity::class.java)
//                // 传递复杂数据需要对，类数据进行序列化
//                intent.putExtra("musicInfo", musicInfo)
//                binding.root.context.startActivity(intent)
                // 方式二：接口回调
                if(onItemClickListener != null){
                    onItemClickListener?.onItemClick(musicInfo)
                }

            }
        }

        // 创建一个方法，将毫秒值转换成分钟:秒
        private fun formatDuration(duration: Long): String {
            val minutes = duration / 1000 / 60
            val seconds = duration / 1000 % 60
            return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
        }
    }

    // 需要一个音乐可变列表，用于装载音频数据类
    var musicList = mutableListOf<MusicInfo>()

    // 用于更新音乐列表，把外部传进来的新的音乐列表替代当前类的旧的音乐列表
    fun updateMusicList(musicList : List<MusicInfo>) {
        // 这里用this就表示，是当前类的musicList
        this.musicList.clear()
        this.musicList.addAll(musicList)
        // notifyDataSetChanged：提示列表数据更新
        notifyDataSetChanged()

    }

    // 定义一个接口，实现外部调用，里面的方法用于传递数据
    interface OnItemClickListener {
        fun onItemClick(music: MusicInfo)
    }

    // 实现这个接口，创建一个变量接收一下，用于判断接口是否为空
    private var onItemClickListener: OnItemClickListener? = null

    // 实现接口回调，外部调用，接口的实现就在这一步
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

}
