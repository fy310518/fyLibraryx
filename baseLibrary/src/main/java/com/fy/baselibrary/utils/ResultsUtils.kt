package com.fy.baselibrary.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fy.baselibrary.aop.clickfilter.ClickUtils

/**
 * description Results api 简单 封装，减少模板代码
 * Created by fangs on 2023/8/1 17:46.
 */

// 扩展
fun ComponentActivity.registerActResult(callback: ActivityResultCallback<ActivityResult>): ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        callback.onActivityResult(it)
    }
}

fun Fragment.registerActResult(callback: ActivityResultCallback<ActivityResult>) =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        callback.onActivityResult(it)
    }

fun Fragment.registerPermissionResult(callback: ActivityResultCallback<Map<String, Boolean>>) =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { premissions->
//        val granted = premissions.entries.all {
//            it.value == true
//        }
        callback.onActivityResult(premissions)
    }

fun Intent.launch(launcher: ActivityResultLauncher<Intent>) {
    launcher.launch(this)
}

fun Bundle.result(activity: Activity) {
    val intent = Intent()
    intent.putExtras(this)
    activity.setResult(Activity.RESULT_OK, intent)
    activity.finish()
}

/**
 * Android14 注册广播报错解决
 * [RECEIVER_EXPORTED 表示可以接收应用外部广播，ContextRECEIVER_NOT_EXPORTED 应用内部广播]
 */
fun Context.receiverRegister(receiver: BroadcastReceiver, filter: IntentFilter, flags: Int = Context.RECEIVER_EXPORTED) {
//    ContextCompat.registerReceiver(this, receiver, filter, flags)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        registerReceiver(receiver, filter, flags)
    } else {
        registerReceiver(receiver, filter)
    }
}


/***
 * 点击事件的View扩展 防止重复点击”
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.click(intervalMillis: Long = 900L, block: (T) -> Unit) = setOnClickListener { view ->
    if(ClickUtils.isFastClick(view, intervalMillis)){
        block(view as T)
    }
}


