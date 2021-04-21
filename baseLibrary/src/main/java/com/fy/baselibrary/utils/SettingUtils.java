package com.fy.baselibrary.utils;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;

import com.fy.baselibrary.application.ioc.ConfigUtils;

/**
 * DESCRIPTION：系统设置工具类
 * Created by fangs on 2019/4/22 16:23.
 */
public class SettingUtils {

    private SettingUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断是否静音模式
     * @return true 静音
     */
    public static boolean isSilentMode() {
        AudioManager audioManager = (AudioManager) ConfigUtils.getAppCtx()
                .getSystemService(Context.AUDIO_SERVICE);

        return audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * 判断是否是震动模式
     * @return true 震动
     */
    public static boolean isShockMode() {
        AudioManager audioManager = (AudioManager) ConfigUtils.getAppCtx()
                .getSystemService(Context.AUDIO_SERVICE);

        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
    }

    /**
     * 设置 手机 静音、震动、模式
     * 需要权限 <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY"/>
     *
     * @param mode [AudioManager.RINGER_MODE_SILENT 静音,且无振动
     *             AudioManager.RINGER_MODE_VIBRATE 静音,但有振动
     *             AudioManager.RINGER_MODE_NORMAL 普通模式]
     */
    public static void silentSwitchOn(int mode) {
        NotificationManager notificationManager = (NotificationManager)ConfigUtils.getAppCtx().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            ConfigUtils.getAppCtx().startActivity(intent);
            return;
        }

        AudioManager audioManager = (AudioManager) ConfigUtils.getAppCtx()
                .getSystemService(Context.AUDIO_SERVICE);

        if (audioManager != null) {
            audioManager.setRingerMode(mode);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
        }
    }


    /**
     * 复制内容到剪贴板
     * @param content
     * @param context
     */
    public static void copyContentToClipboard(String content, Context context) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

}
