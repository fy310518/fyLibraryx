package com.fy.baselibrary.utils.notify

import android.util.Log
import androidx.annotation.NonNull
import com.fy.baselibrary.application.ioc.ConfigUtils

/**
 * description Log统一管理类
 * Created by fangs on 2017/3/1.
 */
class L {

    companion object{
        var isDebug = ConfigUtils.isDEBUG() // 是否需要打印bug
        private const val TAG = "fy"

        @JvmStatic
        fun i(@NonNull msg: String){
            if (isDebug) Log.i(TAG, msg)
        }

        @JvmStatic
        fun d(@NonNull msg: String) {
            if (isDebug) Log.d(TAG, msg)
        }

        @JvmStatic
        fun e(@NonNull msg: String) {
            if (isDebug) Log.e(TAG, msg)
        }

        @JvmStatic
        fun v(@NonNull msg: String) {
            if (isDebug) Log.v(TAG, msg)
        }


        // 下面是传入自定义tag的函数
        @JvmStatic
        fun i(tag: String?, @NonNull msg: String) {
            if (isDebug) Log.i(tag, msg)
        }

        @JvmStatic
        fun d(tag: String?, @NonNull msg: String) {
            if (isDebug) Log.d(tag, msg)
        }

        @JvmStatic
        fun e(tag: String?, @NonNull msg: String) {
            if (isDebug) Log.e(tag, msg)
        }

        @JvmStatic
        fun v(tag: String?, @NonNull msg: String) {
            if (isDebug) Log.v(tag, msg)
        }
    }
}