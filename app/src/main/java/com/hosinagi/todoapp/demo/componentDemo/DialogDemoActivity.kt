package com.hosinagi.todoapp.demo.componentDemo

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.appContext
import com.hosinagi.todoapp.customViews.CustomDialog
import com.hosinagi.todoapp.databinding.DialogCustomBinding
import com.hosinagi.todoapp.utils.ToastUtil
import es.dmoral.toasty.Toasty

class DialogDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dialog_demo)
    }

    // 警示性窗口按钮点击事件
    fun onAlertDialogClick(view: View) {
        AlertDialog.Builder(this)
            .setTitle("温馨提示") // 设置标题
            .setMessage("您确定要删除吗？") // 设置内容
            // 设置确定按钮
            .setPositiveButton("确定"){ _, _ ->
                Toasty.info(appContext, "确认点击了", Toasty.LENGTH_SHORT).show()
            }
            // 设置取消按钮
            .setNegativeButton("取消"){ _, _ ->
                Toasty.info(appContext, "取消点击了", Toasty.LENGTH_SHORT).show()
            }
            .show()
    }

    // 定义变量记录选择的索引
    private var singleChoiceIndex = 0
    // 单选窗口按钮点击事件
    fun onAlertDialogClick2(view: View) {
        // 首先要创建单选选项的数组
        val items = arrayOf<String>("浅色主题", "深色主题", "跟随默认")
        AlertDialog.Builder(this)
            .setTitle("请选择应用的主题")
            .setSingleChoiceItems(items, singleChoiceIndex) {dialog, index ->
                ToastUtil.showToast("您选择的是${items[index]}")
                singleChoiceIndex = index
                dialog.dismiss() // 关闭弹窗
            }
            // 设置确定按钮
            .setPositiveButton("确定"){ dialog, _ ->
                dialog.dismiss() // 关闭弹窗
            }
            // 设置取消按钮
            .setNegativeButton("取消"){ dialog, _ ->
                dialog.dismiss() // 关闭弹窗
            }
            .show()
    }

    // 定义一个数组对应选中状态
    private val MutilChoiceItems = booleanArrayOf(false, false, false, false, false, false)
    // 多选窗口按钮点击事件
    fun onAlertDialogClick3(view: View) {
        val items = arrayOf("阅读","运动","音乐","电影","游戏","旅游")
        AlertDialog.Builder(this)
            .setTitle("请选择喜欢的爱好")
            .setMultiChoiceItems(items,MutilChoiceItems) { dialog, index, isChecked ->
                // 记录当前位置是否选中
                MutilChoiceItems[index] = isChecked
            }
            .setPositiveButton("确定"){ dialog, _ ->
                val selectItems = items.filterIndexed { index, s -> MutilChoiceItems[index] == true }
                ToastUtil.showToast("您选择的是${selectItems.joinToString("、")}")
                dialog.dismiss() // 关闭弹窗
            }
            // 设置取消按钮
            .setNegativeButton("取消"){ dialog, _ ->
                dialog.dismiss() // 关闭弹窗
            }
            .show()
    }

    // 简单自定义窗口按钮点击事件
    fun onAlertDialogClick4(view: View) {
        // 使用自定义的布局，将布局绑定到代码中
        val dialogBinding = DialogCustomBinding.inflate(layoutInflater)
        // 使用AlertDialog使用布局
        AlertDialog.Builder(this)
            .setTitle("用户信息")
            .setView(dialogBinding.root)
            // 设置确定按钮
            .setPositiveButton("确定"){ dialog, _ ->
                // 获取用户输入数据
                val username = dialogBinding.getUsername.text.toString()
                val phone = dialogBinding.getPhone.text.toString()
                val gender = when(dialogBinding.rgGender.checkedRadioButtonId) {
                    R.id.rbMale -> "男"
                    R.id.rbFemale -> "女"
                    else -> "未知"
                }
                ToastUtil.showToast("您输入的是用户名：$username，手机号：$phone，性别：$gender")
                dialog.dismiss() // 关闭弹窗
            }
            // 设置取消按钮
            .setNegativeButton("取消"){ dialog, _ ->
                dialog.dismiss() // 关闭弹窗
            }
            .show()
    }

    var content : String = ""

    // 复杂自定义窗口按钮点击事件
    fun onAlertDialogClick5(view: View) {
        val dialog = CustomDialog(this)
        dialog.show()
        // 按类型写好执行逻辑，传入这个接口，实际逻辑不在此执行
        dialog.setContentConfirmedListener{ content ->
            ToastUtil.showToast("您输入的是：$content")
        }
    }
}