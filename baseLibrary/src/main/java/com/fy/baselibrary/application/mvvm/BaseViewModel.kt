package com.fy.baselibrary.application.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fy.baselibrary.application.ioc.ConfigUtils
import com.fy.baselibrary.utils.notify.L

/**
 * description 定义 MVVM ViewModel 基类
 * Created by fangs on 2021/9/10 9:32.
 */
class BaseViewModel: AndroidViewModel(ConfigUtils.getAppCtx() as Application) {

    override fun onCleared() {
        super.onCleared()
        L.e("ViewModel", "Activity被杀后, ViewModel 被销毁")
    }
}