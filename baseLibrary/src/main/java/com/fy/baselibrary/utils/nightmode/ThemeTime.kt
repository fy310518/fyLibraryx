package com.fy.baselibrary.utils.nightmode

/**
 * description 主题设置时间
 * Created by fangs on 2021/4/1 10:22.
 */
data class ThemeTime(
    // 开始小时
    val beginHour: Int,
    // 开始分钟
    val beginMinute: Int,
    // 结束小时
    val endHour: Int,
    // 结束分钟
    val endMinute: Int,
    // 开始时间
    var startTime: String = "00:00",
    // 结束时间
    var stopTime: String = "00:00"
) {
    init {
        startTime = "${upTo2String(beginHour)}:${upTo2String(beginMinute)}"
        stopTime = "${upTo2String(endHour)}:${upTo2String(endMinute)}"
    }

    fun toTimerString() : String = "$startTime~$stopTime"

    // 保存成长度为2的字符串
    private fun upTo2String(value: Int): String {
        val valueStr = "$value"
        val result = StringBuilder()
        if (valueStr.length < 2) {
            for (i in 0 until 2 - valueStr.length) {
                result.append("0")
            }
        }
        result.append(valueStr)
        return result.toString().substring(result.length - 2)
    }
}