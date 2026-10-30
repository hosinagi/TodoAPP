package com.hosinagi.todoapp.demo.memoryDemo

import android.os.Bundle
import android.os.Debug
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.databinding.ActivityMemoryDemoBinding

class MemoryDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoryDemoBinding
    private var clickCount = 0
    private val memoryHolders = mutableListOf<ByteArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMemoryDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListener()
        updateMemoryInfo()
    }

    private fun setupClickListener() {
        binding.btnAllocateMemory.setOnClickListener {
            allocateMemory()
            clickCount++
            updateMemoryInfo()
        }
    }

    private fun allocateMemory() {
        // 每次分配50MB的内存
        val memoryChunk = ByteArray(10 * 1024 * 1024)
        memoryHolders.add(memoryChunk)
        binding.tvMaxMemory.setTextColor(getColor(android.R.color.black))

    }

    private fun updateMemoryInfo() {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / (1024 * 1024)
        val usedHeapMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val nativeMemory = Debug.getNativeHeapAllocatedSize() / (1024 * 1024)

        binding.tvMaxMemory.text = "最大可用内存：${maxMemory}MB"
        binding.tvHeapMemory.text = "Java堆内存使用：${usedHeapMemory}MB"
        binding.tvNativeMemory.text = "Native内存使用：${nativeMemory}MB"
    }
}