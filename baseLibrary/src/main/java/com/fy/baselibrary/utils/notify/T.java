package com.fy.baselibrary.utils.notify;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.DensityUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.drawable.TintUtils;

/**
 * Toast统一管理类 (解决多次弹出toast)
 * Created by fangs on 2017/3/1.
 */
public class T {

    private static Toast toast;
    public static boolean isShowSystem = false; // 是否显示系统 toast
    public static int gravity = Gravity.BOTTOM;

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
        if (!isShowSystem) {
            Builder.create(message).setImageResource(imageResource).show();
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
        String msgContent = ResUtils.getStr(message);
        if (!isShowSystem){
            Builder.create(message).setImageResource(imageResource).show();
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
        if (null != toast) toast.cancel();

        toast = Toast.makeText(ConfigUtils.getAppCtx(), "", Toast.LENGTH_LONG);
        toast.setText(message);

        NotificationManagerCompat.from(ConfigUtils.getAppCtx()).areNotificationsEnabled();

        toast.show();
    }

    // 取消 toast
    public static void cancel(){
        if (null != toast) toast.cancel();
    }


    /**
     * 显示 自定义 toast
     */
    @SuppressLint("ResourceType")
    private static void showToastWithImg(Builder builder) {
        if(builder.isCancelPrevious) {
            cancel();
        }
        toast = Toast.makeText(ConfigUtils.getAppCtx(), "", Toast.LENGTH_LONG);
        View llToast = LayoutInflater.from(ConfigUtils.getAppCtx()).inflate(R.layout.toast_view, null);

        TextView txtToast = llToast.findViewById(R.id.txtToast);
        ImageView imgToast = llToast.findViewById(R.id.imgToast);

        txtToast.setText(builder.message);
        if (builder.txtColor > 0) {
            txtToast.setTextColor(ResUtils.getColor(builder.txtColor));
        }

        if (builder.imageResource > 0) {
            imgToast.setVisibility(View.VISIBLE);
            imgToast.setImageResource(builder.imageResource);
        } else {
            imgToast.setVisibility(View.GONE);
        }

        if (builder.bgDrawable > 0){
            Drawable bgDrawable;
            if (builder.bgTintColor > 0){
                bgDrawable = TintUtils.getTintDrawable(builder.bgDrawable, 0, builder.bgTintColor);
            } else {
                bgDrawable = TintUtils.getDrawable(builder.bgDrawable, 0);
            }
            llToast.setBackground(bgDrawable);
        }

        NotificationManagerCompat.from(ConfigUtils.getAppCtx()).areNotificationsEnabled();

        toast.setView(llToast);

        toast.setGravity(gravity, 0, DensityUtils.dp2px(150));
        toast.setDuration(builder.duration);
        toast.show();
    }


    public static class Builder {
        private @DrawableRes int imageResource;
        private @NonNull CharSequence message;
        private @ColorRes int txtColor;

        private @DrawableRes int bgDrawable;
        private @ColorRes int bgTintColor;

        private boolean isCancelPrevious = true; // 是否 直接关闭 前面的 toast
        private int duration = Toast.LENGTH_LONG;

        private Builder(@NonNull CharSequence message) {
            this.message = message;
        }

        public Builder setTxtColor(int txtColor) {
            this.txtColor = txtColor;
            return this;
        }

        public Builder setImageResource(int imageResource) {
            this.imageResource = imageResource;
            return this;
        }

        public Builder setBgColor(@DrawableRes int bgDrawable) {
            this.bgDrawable = bgDrawable;
            return this;
        }

        public Builder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder setCancelPrevious(boolean cancelPrevious) {
            isCancelPrevious = cancelPrevious;
            return this;
        }

        public Builder setBgColor(@DrawableRes int bgDrawable, @ColorRes int bgTintColor) {
            this.bgDrawable = bgDrawable;
            this.bgTintColor = bgTintColor;
            return this;
        }

        /**
         * 构建参数 对象
         * @param message
         * @return
         */
        public static Builder create(CharSequence message){
            return new Builder(message);
        }

        public static Builder create(@StringRes int message){
            return new Builder(ResUtils.getStr(message));
        }

        /**
         * 显示 自定义 toast
         */
        public void show() {
            T.showToastWithImg(this);
        }
    }


}
