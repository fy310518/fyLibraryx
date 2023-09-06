package com.fy.baselibrary.application.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.notify.L;

/**
 * description 定义 MVVM ViewModel 基类
 * Created by fangs on 2022/7/4 17:01.
 */
public class BaseViewModel extends AndroidViewModel {

    public BaseViewModel() {
        super((Application) ConfigUtils.getAppCtx());
    }

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        L.e("ViewModel", "Activity被杀后, ViewModel 被销毁");
    }
}
