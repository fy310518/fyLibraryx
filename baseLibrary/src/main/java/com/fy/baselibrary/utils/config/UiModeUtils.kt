package com.fy.baselibrary.utils.config

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.fy.baselibrary.application.ioc.ConfigUtils
import com.fy.baselibrary.utils.JumpUtils
import com.fy.baselibrary.utils.cache.SpfAgent
import com.fy.baselibrary.utils.notify.L

/**
 * description 暗黑模式工具类
 * Created by fangs on 2024/4/15 17:03.
 */
class UiModeUtils {

    companion object{
        var dark_color_mode = "dark_color_mode" //暗黑模式 数据，-1 自动，1 深色；2 浅色
        var sys_uiMode = "SYS_Ui_Mode_Modify" //系统 Ui 模式改变

        /**
         * 判断当前是否深色模式
         * @return 深色模式返回 true，否则返回false
         */
        fun isDarkThemeOn(): Boolean {
            val uiModeManager: UiModeManager? = ConfigUtils.getAppCtx().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            if (uiModeManager != null) {
                val currentNightMode = uiModeManager.nightMode
                return currentNightMode == UiModeManager.MODE_NIGHT_YES // 深色模式
            }
            return false
        }

        fun getUiMode(): Int {
            return SpfAgent.init().getInt(dark_color_mode)
        }

        fun saveUiMode(modeValue: Int){
            SpfAgent.init().saveInt(dark_color_mode, modeValue).commit(true)
        }

        /**
         * 获取 自定义的 深色模式 代码
         */
        fun getUiModeCode(): Int {
            return if(getUiMode() == -1){
                if(UiModeUtils.isDarkThemeOn()){
                    1
                } else {
                    2
                }
            } else {
                getUiMode()
            }
        }



        /**
         * 前置配置：DayNight 主题；drawable-night ，values-night 等文件中配置 暗黑主题 样式资源；
         * 1、Application 的 super.onConfigurationChanged 后调用，标记 系统Uimode 已经改变
         */
        fun saveUiModeModify(newConfig: Configuration) {
            if (getUiMode() == -1) {
                val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
                when (currentNightMode) {
                    Configuration.UI_MODE_NIGHT_NO -> { // 夜间模式未启用，我们正在使用浅色主题
                        L.e("---", "日间模式启用")
                        SpfAgent.init().saveBoolean(sys_uiMode, true).commit(true)
                    }

                    Configuration.UI_MODE_NIGHT_YES -> { // 夜间模式启用，我们使用的是深色主题
                        L.e("---", "夜间模式启用")
                        SpfAgent.init().saveBoolean(sys_uiMode, true).commit(true)
                    }
                }
            }
        }

        /**
         * 2、Application 的 onCreate 中 初始化
         */
        fun initUiMode() {
            when (getUiMode()) {
                -1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        /**
         * 3、如果系统Uimode 已经改变 重启app 【避免系统配置改变，app 界面样式错乱】
         * ActivityLifecycleCallbacks  onActivityResumed 中调用
         */
        fun restartApp(act: Activity, actClass: Class<*>) {
            if (SpfAgent.init().getBoolean(sys_uiMode)) { // 系统uiMode 已经改变
                SpfAgent.init().remove(sys_uiMode, true)
                JumpUtils.restartApp(act, actClass)
            }
        }

        /**
         * 4、手动设置 UiMode，根据配置，切换模式，并重启app 生效
         */
        fun setAppUiMode(act: Activity, actClass: Class<*>) {
            initUiMode()

            JumpUtils.restartApp(act, actClass)
        }

    }
}