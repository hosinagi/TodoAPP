package com.hosinagi.todoapp.demo.tablayoutDemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hosinagi.todoapp.databinding.FragmentContentBinding

class TabContentFragment: Fragment() {
    // 定义一个binding
    private lateinit var binding: FragmentContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentBinding.inflate(inflater, container, false)
        val title = arguments?.getString("title")
        title?.let { binding.contentTv.text = it }
        return binding.root
    }

    // 定义一个伴生对象，返回展示不同内容的Fragment
    companion object {
        fun newInstance(title: String): Fragment {
            val fragment = TabContentFragment()
            val bundle = Bundle()
            bundle.putString("title", title)
            fragment.arguments = bundle
            return fragment
        }
    }
}