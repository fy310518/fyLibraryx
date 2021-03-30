package com.fy.baselibrary.startactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fy.baselibrary.application.IBaseActivity;
import com.fy.baselibrary.utils.notify.L;

/**
 * 不可见Activity 用于控制程序 退出(入口activity)
 * Created by fangs on 2017/4/26.
 */
public abstract class StartActivity extends AppCompatActivity implements IBaseActivity {

    @Override
    public void initData(Activity activity, Bundle savedInstanceState) {

        exitOrIn(getIntent());
    }

    /**
     * 应用 自己业务处理 方法
     */
    public abstract void businessJump();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        L.e("StartActivity", "onNewIntent- false");

        boolean b = (Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0;
        boolean c = intent.getBooleanExtra("exitApp", false);
        if (b && c) {
            exitApp();
        }
    }

    // 避免从桌面启动程序后，会重新实例化入口类的activity
    private void isStartActivityOnly() {
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    exitApp();
                    return;
                } else {
                    businessJump();
                }
            }
        } else {
            businessJump();
        }
    }

    /**
     * 根据intent 判断进入应用还是退出应用
     * @param intent
     */
    private void exitOrIn(Intent intent){
        boolean b = (Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags())!= 0;
        boolean c = intent.getBooleanExtra("exitApp", false);
        if (b && c) {
            exitApp();
        } else {
            isStartActivityOnly();
        }
    }

    /**
     * 退出应用
     */
    private void exitApp() {
        L.e("ActivityCallbacks", "Application--Create() 退出-----");
        finish();
        System.exit(0);
    }
}
