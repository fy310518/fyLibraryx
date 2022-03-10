package com.fy.baselibrary.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;

import com.fy.baselibrary.application.mvvm.BaseViewModel;
import com.fy.baselibrary.application.mvvm.IBaseMVVM;
import com.fy.baselibrary.statuslayout.LoadSirUtils;
import com.fy.baselibrary.statuslayout.OnSetStatusView;
import com.fy.baselibrary.statuslayout.StatusLayoutManager;
import com.fy.baselibrary.utils.AnimUtils;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.notify.L;

import io.reactivex.subjects.BehaviorSubject;

/**
 * activity 生命周期回调 (api 14+)
 * 注意：使用本框架 activity 与 activity 之间传递数据 统一使用 Bundle
 * Created by fangs on 2017/5/18.
 */
public class BaseActivityLifecycleCallbacks extends BaseLifecycleCallback {
    public static final String TAG = "lifeCycle --> ";
    public static int actNum;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        BaseActivityLifecycleCallbacks.actNum++;
        L.e(TAG + activity.getClass().getSimpleName(), "Create()   " + activity.getTaskId());
//        正式发布时候 解开以下注释
//        if (OSUtils.getRomType() == OSUtils.EMUI && onCheck(activity)){//是华为手机则 执行
//            activity.finish();
//            return;
//        }

        ResUtils.setFontDefault(activity);

        BaseActivityBean activityBean = new BaseActivityBean();
        activityBean.setSubject(BehaviorSubject.create());

        ViewDataBinding vdb = null;
        BaseViewModel bvm = null;
        IBaseMVVM act = null;
        if (activity instanceof IBaseMVVM) {
            act = (IBaseMVVM) activity;

            vdb = DataBindingUtil.setContentView(activity, act.setContentLayout());
            if (activity instanceof LifecycleOwner) vdb.setLifecycleOwner((LifecycleOwner) activity);
            bvm = AnimUtils.createViewModel(activity);

//            注册屏幕旋转监听
            if (Constant.isOrientation) {
                BaseOrientoinListener orientoinListener = new BaseOrientoinListener(activity);
                boolean autoRotateOn = (Settings.System.getInt(activity.getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1);

                //检查系统是否开启自动旋转
                if (autoRotateOn) orientoinListener.enable();
                activityBean.setOrientoinListener(orientoinListener);
            }

            //设置 activity 多状态布局
            if (activity instanceof OnSetStatusView) {
                StatusLayoutManager slManager = LoadSirUtils.initStatusLayout(activity);
                activityBean.setSlManager(slManager);
            }
        }


        activity.getIntent().putExtra("ActivityBean", activityBean);
        //基础配置 执行完成，再执行 初始化 activity 操作
        if (null != act) act.initData(bvm, vdb, savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        L.e(TAG + activity.getClass().getSimpleName(), "--Start()");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        String simpleName = activity.getClass().getSimpleName();
        L.e(TAG + simpleName, "--Resume()");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        String simpleName = activity.getClass().getSimpleName();
        L.e(TAG + simpleName, "--Pause()");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        L.e(TAG + activity.getClass().getSimpleName(), "--Stop()");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        L.e(TAG + activity.getClass().getSimpleName(), "--SaveInstanceState()");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        L.e(TAG + activity.getClass().getSimpleName(), "--Destroy()");

        BaseActivityBean activityBean = (BaseActivityBean) activity.getIntent()
                .getSerializableExtra("ActivityBean");

        if (null != activityBean) {
            //销毁 屏幕旋转监听
            if (null != activityBean.getOrientoinListener())
                activityBean.getOrientoinListener().disable();

            if (null != activityBean.getSubject())
                activityBean.getSubject().onNext(Constant.DESTROY);
        }
    }


    /**
     * 判断 应用是否被杀死（拦截 华为手机 设置中关闭权限 应用崩溃重启 黑屏问题）
     * @param activity
     * @return
     */
    private boolean onCheck(Activity activity) {
        boolean isrun;
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);

        if (BaseActivityLifecycleCallbacks.actNum == 1 &&
                info.numRunning == 1 &&
                !info.topActivity.getClassName().equals("com.fy.baselibrary.startactivity.StartActivity")) {
            //被杀死重启
            isrun = true;
            L.e(TAG, activity.getClass().getName() + "关闭此界面");
        } else {
            isrun = false;//手动启动
        }

        return isrun;
    }

}
