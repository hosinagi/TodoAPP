package com.hosinagi.todoapp.utils

import android.content.Context
import android.widget.Toast
import com.hosinagi.todoapp.MyApplication
import com.hosinagi.todoapp.appContext

class ToastUtil {

    companion object{
        fun showToast(message:String) {
            Toast.makeText(appContext,message,Toast.LENGTH_SHORT).show()
        }

    }
}