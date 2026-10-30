package com.hosinagi.todoapp.demo.tablayoutDemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityTabLayoutDemoBinding
import es.dmoral.toasty.Toasty

class TabLayoutDemoActivity : AppCompatActivity() {
    // 定义binding
    private lateinit var binding: ActivityTabLayoutDemoBinding

    // tab标题list数组
    private val tabTitlesList = listOf("关注", "推荐", "发现", "热榜","生活")

    // tab图标icon数组
    private val tabIconsList = listOf(
        R.drawable.home,
        R.drawable.news,
        R.drawable.home,
        R.drawable.news,
        R.drawable.home,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTabLayoutDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 使用for循环遍历tabTitlesList数组，创建tab并添加到TabLayout中
        for(title in tabTitlesList) {
            // 创建tab
            val tab = binding.tabLayout.newTab()
            // 将title赋值给tab的text
            tab.text = title
            // 将tab添加到TabLayout中
            binding.tabLayout.addTab(tab)
        }

        // 使用forEach循环遍历tabIconsList数组，拿到title和其下标index,设置tab的图标和标题
        tabTitlesList.forEachIndexed { index, title ->
            val tab = binding.tabLayout2.newTab()
            tab.text = title
            tab.setIcon(tabIconsList[index])
            binding.tabLayout2.addTab(tab)
        }

        // 设置tabLayout的监听器
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            // 选中监听器
            override fun onTabSelected(tab: TabLayout.Tab) {
                Toasty.info(this@TabLayoutDemoActivity,"${tab.text}", Toasty.LENGTH_SHORT).show()
            }
            // 取消监听器
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            // 重新选中监听器
            override fun onTabReselected(tab: TabLayout.Tab) {
            }

        })

        // ViewPager2 + TabLayout
        binding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            // 创建每一个Fragment
            override fun createFragment(position: Int): Fragment = TabContentFragment.newInstance(tabTitlesList[position])
            // 配置Fragment的数量
            override fun getItemCount(): Int = tabTitlesList.size
        }

        // 绑定ViewPager2和TabLayout
        TabLayoutMediator(binding.tabLayout,binding.viewPager2) {tab, position ->
            tab.text = tabTitlesList[position]
        }.attach()

    }
}
