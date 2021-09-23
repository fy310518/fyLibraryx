package com.fy.baselibrary.utils.notify

import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import com.fy.baselibrary.application.ioc.ConfigUtils
import com.fy.baselibrary.utils.ResUtils

/**
 * description Toast 工具类 (解决多次弹出toast)
 * Created by fangs on 2017/3/1.
 */
class T {
    companion object{
        /**
         * 显示toast 开关
         */
        private var isShow = true
        private var toast: Toast? = null

        @JvmStatic
        fun showShort(@StringRes message: Int) {
            show(ResUtils.getStr(message), Toast.LENGTH_SHORT)
        }

        @JvmStatic
        fun show(@StringRes message: Int) {
            show(ResUtils.getStr(message))
        }

        /**
         * 显示系统 toast【默认长时间】
         * @param message 消息
         */
        @JvmStatic
        fun show(@NonNull message: CharSequence, duration: Int = Toast.LENGTH_LONG) {
            if (!isShow) return

            toast?.cancel()
            toast = Toast.makeText(ConfigUtils.getAppCtx(), "", duration)

            toast?.setText(message)
            toast?.show()
        }

    }
}