package com.fy.baselibrary.utils.notify;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.google.android.material.snackbar.BaseTransientBottomBar;

/**
 * Toast统一管理类 (解决多次弹出toast)
 * Created by fangs on 2017/3/1.
 */
public class T {

    private volatile static T instance;
    public static synchronized T getInstance() {
        if (null == instance) {
            synchronized (T.class) {
                if (null == instance) {
                    instance = new T();
                }
            }
        }

        return instance;
    }

    private static Toast toast;
    private int gravity = Gravity.BOTTOM;
    private int duration = Toast.LENGTH_LONG;

    private T() {
        if (toast == null) {
            toast = new Toast(ConfigUtils.getAppCtx());
        }
    }


    public T setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public T setDuration(int duration) {
        this.duration = duration;
        return this;
    }


    public static void show(@StringRes int message) {
        show(message, -1);
    }
    public static void show(CharSequence message) {
        show(message, -1);
    }

    /**
     * 长时间显示Toast
     * @param message
     */
    public static void show(CharSequence message, @DrawableRes final int imageResource) {
        if (instance == null){
            show(message.toString(), Toast.LENGTH_LONG);
        } else {
            getInstance().showToastWithImg(message, imageResource);
        }
    }

    /**
     * 长时间显示Toast
     * @param message
     */
    public static void show(@StringRes int message, @DrawableRes final int imageResource) {
        String msgContent = ResUtils.getStr(message);
        if (instance == null){
            show(msgContent, Toast.LENGTH_LONG);
        } else {
            getInstance().showToastWithImg(msgContent, imageResource);
        }
    }

    /**
     * 显示系统 toast
     *
     * @param message 消息
     */
    @SuppressLint("ShowToast")
    private static void show(String message, int duration) {
        if (null == toast) {
            toast = Toast.makeText(ConfigUtils.getAppCtx(), "", duration);
        }

        NotificationManagerCompat.from(ConfigUtils.getAppCtx()).areNotificationsEnabled();
        toast.setText(message);

        toast.cancel();
        toast.show();
    }

    /**
     * 显示 自定义 toast
     */
    @SuppressLint("ResourceType")
    private void showToastWithImg(@NonNull final CharSequence message, @DrawableRes final int imageResource) {

        View llToast = LayoutInflater.from(ConfigUtils.getAppCtx()).inflate(R.layout.toast_view, null);
        TextView txtToast = llToast.findViewById(R.id.txtToast);
        ImageView imgToast = llToast.findViewById(R.id.imgToast);

        if (imageResource > 0) {
            imgToast.setVisibility(View.VISIBLE);
            imgToast.setImageResource(imageResource);
        } else {
            imgToast.setVisibility(View.GONE);
        }

        txtToast.setText(message);


        toast.cancel();
        NotificationManagerCompat.from(ConfigUtils.getAppCtx()).areNotificationsEnabled();
        toast.setView(llToast);

        toast.setGravity(gravity, 0, 200);
        toast.setDuration(duration);
        toast.show();
    }

}
