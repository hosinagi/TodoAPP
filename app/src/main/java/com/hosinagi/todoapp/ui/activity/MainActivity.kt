package com.hosinagi.todoapp.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityMainBinding
import com.hosinagi.todoapp.ui.fragment.AudioFragment
import com.hosinagi.todoapp.ui.fragment.NewsFragment
import com.hosinagi.todoapp.ui.fragment.ProfileFragment
import com.hosinagi.todoapp.ui.fragment.TodoListFragment
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {
    // 定义binding
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val KEY_SELECTED_TAB = "selected_tab"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val selectedTab = savedInstanceState?.getInt(KEY_SELECTED_TAB, R.id.tab_home)
            ?: R.id.tab_home
        showFragment(selectedTab)
        binding.bottomNavbar.selectedItemId = selectedTab

        // 监听底部导航栏
        binding.bottomNavbar.setOnItemSelectedListener { item ->
            if (item.itemId in setOf(
                    R.id.tab_home,
                    R.id.tab_news,
                    R.id.tab_audio,
                    R.id.tab_profile
                )
            ) {
                showFragment(item.itemId)
                true
            } else {
                false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_TAB, binding.bottomNavbar.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    // 根据底部导航的ID找到对应Fragment，重建页面时复用已存在的实例
    private fun showFragment(tabId: Int) {
        val tag = when (tabId) {
            R.id.tab_home -> "todo"
            R.id.tab_news -> "news"
            R.id.tab_audio -> "audio"
            R.id.tab_profile -> "profile"
            else -> return
        }
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        // 根据Tag找对应的Fragment
        val target = fm.findFragmentByTag(tag) ?: when (tabId) {
            R.id.tab_home -> TodoListFragment()
            R.id.tab_news -> NewsFragment()
            R.id.tab_audio -> AudioFragment()
            else -> ProfileFragment()
        }

        fm.fragments.forEach { fragment ->
            if (fragment !== target && fragment.isAdded) {
                ft.hide(fragment)
            }
        }
        if (target.isAdded) {
            ft.show(target)
        } else {
            ft.add(R.id.container, target, tag)
        }
        ft.commit()
    }
}
