package com.fy.baselibrary.utils.config

import android.app.Activity
import android.graphics.Color
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * description 状态栏，导航栏 工具类
 * Created by fangs on 2023/8/3 10:05.
 */
class StatusBarUtils {

    companion object{

        /**
         * 控制键盘 显示/隐藏
         * @param isVisible 是否显示
         */
        fun setKeyBoardVisible(activity: Activity, editText: EditText, isVisible: Boolean){
            val window = activity.window

            WindowCompat.getInsetsController(window, editText).let { controller ->
                if(isVisible){
                    controller.show(WindowInsetsCompat.Type.ime())
                } else {
                    controller.hide(WindowInsetsCompat.Type.ime())
                }
            }
        }

        /**
         * 控制状态栏、导航栏 显示/隐藏
         * @param isVisible 是否显示
         */
        fun setStatusBarVisible(activity: Activity, isVisible: Boolean) {
            val window = activity.window

            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                if (isVisible) {
                    controller.show(WindowInsetsCompat.Type.statusBars())
                    controller.show(WindowInsetsCompat.Type.navigationBars())
                } else {
                    controller.hide(WindowInsetsCompat.Type.statusBars())
                    controller.hide(WindowInsetsCompat.Type.navigationBars())
                }
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        /**
         *  设置状态栏 背景颜色
         *  这里还是直接操作window的statusBarColor
         */
        fun setStatusBarColor(activity: Activity, @ColorInt statusBarColor: Int, @ColorInt navigationBarColor: Int) {
            activity.window.statusBarColor = statusBarColor
            setStatusBarTextColor(activity, statusBarColor)

            activity.window.navigationBarColor = navigationBarColor
            val luminanceValue = ColorUtils.calculateLuminance(navigationBarColor)
            WindowInsetsControllerCompat(activity.window, activity.window.decorView).let { controller ->
                if (navigationBarColor == Color.TRANSPARENT) {
                    controller.isAppearanceLightNavigationBars = true
                } else {
                    controller.isAppearanceLightNavigationBars = luminanceValue >= 0.5
                }
            }
        }

        /**
         *  沉浸式状态栏
         *  @param contentColor 内容颜色:获取内容的颜色，传入系统，它自动修改字体颜色(黑/白)
         */
        fun immersiveStatusBar(activity: Activity, @ColorInt contentColor: Int) {
            val window = activity.window.apply {
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = Color.TRANSPARENT
            }
            // 设置状态栏字体颜色
            setStatusBarTextColor(activity, contentColor)
            WindowCompat.setDecorFitsSystemWindows(window, false)

//            activity.findViewById<FrameLayout>(android.R.id.content).apply {
//                ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
//                    val params = view.layoutParams as LinearLayout.LayoutParams
//                    params.topMargin = insets.systemWindowInsetTop
//                    insets
//                }
//            }
        }

        /**
         *  设置状态栏字体颜色
         *  此api只能控制字体颜色为 黑/白
         *  @param color 这里的颜色是指背景颜色
         */
        private fun setStatusBarTextColor(activity: Activity, @ColorInt color: Int) {
            // 计算颜色亮度
            val luminanceValue = ColorUtils.calculateLuminance(color)
            WindowInsetsControllerCompat(activity.window, activity.window.decorView).let { controller ->
                if (color == Color.TRANSPARENT) {
                    // 如果是透明颜色就默认设置成黑色
                    controller.isAppearanceLightStatusBars = true
                } else {
                    // 通过亮度来决定字体颜色是黑还是白
                    controller.isAppearanceLightStatusBars = luminanceValue >= 0.5
                }
            }
        }

    }
}