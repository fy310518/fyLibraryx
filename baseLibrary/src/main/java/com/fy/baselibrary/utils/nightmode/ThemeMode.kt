package com.fy.baselibrary.utils.nightmode

import android.content.Context
import com.fy.baselibrary.R

/**
 * description 主题开启模式
 * Created by fangs on 2021/4/1 10:23.
 */
enum class ThemeMode(val intValue: Int, val stringValueId: Int) {
    MODE_FOLLOW_SYSTEM(-1, R.string.theme_mode_),
    MODE_ALWAYS_ON(0, R.string.theme_mode_0),
    MODE_ALWAYS_OFF(1, R.string.theme_mode_1),
    MODE_TIMER(2, R.string.theme_mode_2);

    companion object {
        @JvmStatic
        fun parseOfString(context: Context, String: String): ThemeMode {
            return when(String) {
                context.resources.getString(MODE_ALWAYS_ON.stringValueId) -> MODE_ALWAYS_ON
                context.resources.getString(MODE_ALWAYS_OFF.stringValueId) -> MODE_ALWAYS_OFF
                context.resources.getString(MODE_TIMER.stringValueId) -> MODE_TIMER
                else -> MODE_FOLLOW_SYSTEM
            }
        }

        @JvmStatic
        fun parseOfInt(intValue: Int) : ThemeMode {
            return when(intValue) {
                MODE_ALWAYS_ON.intValue -> MODE_ALWAYS_ON
                MODE_ALWAYS_OFF.intValue -> MODE_ALWAYS_OFF
                MODE_TIMER.intValue -> MODE_TIMER
                else -> MODE_FOLLOW_SYSTEM
            }
        }
    }
}