package com.fy.baselibrary.utils.notify;

import android.annotation.SuppressLint;
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

/**
 * Toast统一管理类 (解决多次弹出toast)
 * Created by fangs on 2017/3/1.
 */
public class T {

    private static Toast toast;
    private int gravity = Gravity.BOTTOM;

    private T() {}


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
    @SuppressLint("ResourceType")
    public static void show(CharSequence message, @DrawableRes final int imageResource) {
        if (null == toast) {
            toast = Toast.makeText(ConfigUtils.getAppCtx(), "", Toast.LENGTH_LONG);
        }

        if (imageResource > 0) {
            showToastWithImg(message, imageResource);
        } else {
            show(message.toString());
        }
    }

    /**
     * 长时间显示Toast
     * @param message
     */
    @SuppressLint("ResourceType")
    public static void show(@StringRes int message, @DrawableRes final int imageResource) {
        if (null == toast) {
            toast = Toast.makeText(ConfigUtils.getAppCtx(), "", Toast.LENGTH_LONG);
        }

        String msgContent = ResUtils.getStr(message);
        if (imageResource > 0){
            showToastWithImg(msgContent, imageResource);
        } else {
            show(msgContent);
        }
    }

    /**
     * 显示系统 toast
     *
     * @param message 消息
     */
    @SuppressLint("ShowToast")
    private static void show(String message) {
        NotificationManagerCompat.from(ConfigUtils.getAppCtx()).areNotificationsEnabled();
        toast.setText(message);

        toast.cancel();
        toast.show();
    }

    /**
     * 显示 自定义 toast
     */
    @SuppressLint("ResourceType")
    private static void showToastWithImg(@NonNull final CharSequence message, @DrawableRes final int imageResource) {
        View llToast = LayoutInflater.from(ConfigUtils.getAppCtx()).inflate(R.layout.toast_view, null);
        TextView txtToast = llToast.findViewById(R.id.txtToast);
        ImageView imgToast = llToast.findViewById(R.id.imgToast);

        txtToast.setText(message);
        if (imageResource > 0) {
            imgToast.setVisibility(View.VISIBLE);
            imgToast.setImageResource(imageResource);
        } else {
            imgToast.setVisibility(View.GONE);
        }


        NotificationManagerCompat.from(ConfigUtils.getAppCtx()).areNotificationsEnabled();
        toast.setView(llToast);

//        toast.setGravity(gravity, 0, 200);
        toast.cancel();
        toast.show();
    }

}
