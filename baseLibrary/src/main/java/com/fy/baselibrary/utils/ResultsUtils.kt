package com.fy.baselibrary.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

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

fun Intent.launch(launcher: ActivityResultLauncher<Intent>) {
    launcher.launch(this)
}

fun Bundle.result(activity: Activity) {
    val intent = Intent()
    intent.putExtras(this)
    activity.setResult(Activity.RESULT_OK, intent)
    activity.finish()
}