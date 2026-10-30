package com.hosinagi.todoapp.customViews

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.hosinagi.todoapp.databinding.DialogCustom2Binding
import com.hosinagi.todoapp.utils.ToastUtil

class CustomDialog(context: Context) : Dialog(context) {
    // 延迟初始化binding
    private lateinit var binding : DialogCustom2Binding
    // 定义变量存储执行逻辑，可能为空
    private var onContentConfirmed : ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化binding，绑定布局
        binding = DialogCustom2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        // 设置窗口的样式,通过window获取窗口
        val params = window?.attributes
        // 获取手机屏幕的宽度
        val screenWidth = context.resources.displayMetrics.widthPixels
        // 设置window属性宽度为手机屏幕的80%
        params?.width = (screenWidth * 0.8).toInt()
        window?.attributes = params

        // 设置点击窗口外部区域不消失
        setCanceledOnTouchOutside(false)

        // 设置取消按钮的点击事件
        binding.btnCancel.setOnClickListener {
            dismiss() // 关闭对话框
        }
        // 设置确认按钮的点击事件
        binding.btnConfirm.setOnClickListener {
            val content = binding.etContent.text.toString()
            // 判断输入内容content是否为空，为空则提醒
            if (content.isBlank()) {
                ToastUtil.showToast("请输入内容")
                return@setOnClickListener
            }
            // invoke的作用是，调用这个变量存储的执行逻辑，并将后面的数据作为参数传入
            // 实际上就是调用onContentConfirmed(content)
            onContentConfirmed?.invoke(content)

            dismiss()

        }

    }
    // 设置接口，用于外部调用，传入执行逻辑
    fun setContentConfirmedListener(listener: (String) -> Unit) {
        // 外部传入的执行逻辑赋值给变量onContentConfirmed
        onContentConfirmed = listener
    }


}