package com.fy.baselibrary.aop.statusbar;

import android.app.Activity;
import android.text.TextUtils;

import com.fy.baselibrary.aop.annotation.StatusBar;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.config.StatusBarUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class StatusBarAspect {

    @Pointcut("execution(@com.fy.baselibrary.aop.annotation.StatusBar * *(..))" + " && @annotation(param)")
    public void statusBar(StatusBar param) {}

    @Before("statusBar(param)")
    public void clickFilterHook(JoinPoint joinPoint, StatusBar param) throws Throwable {
        Activity activity = null;
        final Object object = joinPoint.getThis();
        if (null == object) return;

        if (object instanceof Activity) {
            activity = ((Activity)object);
        }
        if (null == activity) return;

        int statusColor, navColor;
        if (!TextUtils.isEmpty(param.statusStrColor())){
            statusColor = ResUtils.getColorId(param.statusStrColor());
        } else {
            statusColor = ResUtils.getColor(param.statusColor());
        }

        switch (param.statusOrNavModel()){
            case 0:
                if (!TextUtils.isEmpty(param.navStrColor())){
                    navColor = ResUtils.getColorId(param.navStrColor());
                } else {
                    navColor = ResUtils.getColor(param.navColor());
                }
                StatusBarUtils.Companion.setStatusBarColor(activity, statusColor, navColor);
                break;
            case 1:
                StatusBarUtils.Companion.immersiveStatusBar(activity, statusColor);
                break;
        }

    }
}
