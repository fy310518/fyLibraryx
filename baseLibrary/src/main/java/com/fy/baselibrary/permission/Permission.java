package com.fy.baselibrary.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

import androidx.collection.ArrayMap;

import com.fy.baselibrary.R;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.os.OSUtils;

/**
 * description  单独列出 不同版本 变更的权限
 * Created by fangs on 2020/9/4 16:18.
 */
public class Permission {
    private Permission() {}

//    Android 的权限大致分为三种：
//    普通权限：只需要在清单文件中注册即可
//    危险权限：需要在代码中动态申请，以弹系统 Dialog 的形式进行请求
//    特殊权限：需要在代码中动态申请，以跳系统 Activity 的形式进行请求
    /** 收集Android 各版本 特殊权限 */

    /** android14 选择性照片和视频访问权限 */
    public static final String READ_USER_SELECTED = "android.permission.READ_MEDIA_VISUAL_USER_SELECTED";
    /** 外部存储权限（特殊权限，需要 Android 13 及以上） */
    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";
    /** 外部存储权限（特殊权限，需要 Android 11 及以上） */
    public static final String MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE";

    /** 应用安装权限（特殊权限，需要 Android 8.0 及以上） */
    public static final String REQUEST_INSTALL_PACKAGES = "android.permission.REQUEST_INSTALL_PACKAGES";
    /** 通知栏权限（特殊权限，需要 Android 7.0 及以上） */
    public static final String NOTIFICATION_SERVICE = "android.permission.ACCESS_NOTIFICATION_POLICY";
    /** 悬浮窗权限（特殊权限，需要 Android 6.0 及以上） */
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";
    /** 系统设置权限（特殊权限，需要 Android 6.0 及以上） */
    public static final String WRITE_SETTINGS = "android.permission.WRITE_SETTINGS";
    /** vpn service 权限（特殊权限） */
    public static final String VPN_SERVICE = "android.permission.BIND_VPN_SERVICE";


    /** 在后台获取位置（需要 Android 10.0 及以上） */
    public static final String ACCESS_BACKGROUND_LOCATION = "android.permission.ACCESS_BACKGROUND_LOCATION";
    /** 读取照片中的地理位置（需要 Android 10.0 及以上）*/
    public static final String ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION";
    /** 使用传感器 */
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";
    /** 获取活动步数（需要 Android 10.0 及以上） */
    public static final String ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION";

    /**
     * 处理拨出电话
     * 在 Android 10 已经废弃，请直接使用 {link ANSWER_PHONE_CALLS}
     */
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";
    /** 接听电话（需要 Android 8.0 及以上） */
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";
    /** 读取手机号码（需要 Android 8.0 及以上） */
    public static final String READ_PHONE_NUMBERS = "android.permission.READ_PHONE_NUMBERS";

    /** 读取电话状态 */
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";




    @SuppressLint("InlinedApi")
    public static final ArrayMap<String, String> permissionMap = new ArrayMap<String, String>(){{
        put(Manifest.permission.READ_CALENDAR, Manifest.permission_group.CALENDAR);
        put(Manifest.permission.WRITE_CALENDAR, Manifest.permission_group.CALENDAR);
        put(Manifest.permission.READ_CALL_LOG, Manifest.permission_group.CALL_LOG);
        put(Manifest.permission.WRITE_CALL_LOG, Manifest.permission_group.CALL_LOG);
        put(Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission_group.CALL_LOG);
        put(Manifest.permission.CAMERA, Manifest.permission_group.CAMERA);
        put(Manifest.permission.READ_CONTACTS, Manifest.permission_group.CONTACTS);
        put(Manifest.permission.WRITE_CONTACTS, Manifest.permission_group.CONTACTS);
        put(Manifest.permission.GET_ACCOUNTS, Manifest.permission_group.CONTACTS);
        put(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission_group.LOCATION);
        put(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission_group.LOCATION);
        put(Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission_group.LOCATION);
        put(Manifest.permission.RECORD_AUDIO, Manifest.permission_group.MICROPHONE);
        put(Manifest.permission.READ_PHONE_STATE, Manifest.permission_group.PHONE);
        put(Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission_group.PHONE);
        put(Manifest.permission.CALL_PHONE, Manifest.permission_group.PHONE);
        put(Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission_group.PHONE);
        put(Manifest.permission.ADD_VOICEMAIL, Manifest.permission_group.PHONE);
        put(Manifest.permission.USE_SIP, Manifest.permission_group.PHONE);
        put(Manifest.permission.ACCEPT_HANDOVER, Manifest.permission_group.PHONE);
        put(Manifest.permission.BODY_SENSORS, Manifest.permission_group.SENSORS);
        put(Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission_group.ACTIVITY_RECOGNITION);
        put(Manifest.permission.SEND_SMS, Manifest.permission_group.SMS);
        put(Manifest.permission.RECEIVE_SMS, Manifest.permission_group.SMS);
        put(Manifest.permission.READ_SMS, Manifest.permission_group.SMS);
        put(Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission_group.SMS);
        put(Manifest.permission.RECEIVE_MMS, Manifest.permission_group.SMS);
        put(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE);
        put(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE);
        put(Manifest.permission.ACCESS_MEDIA_LOCATION, Manifest.permission_group.STORAGE);

//        if (OSUtils.isAndroid13()) {
            put(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission_group.READ_MEDIA_VISUAL);
            put(Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission_group.READ_MEDIA_VISUAL);
            put(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission_group.READ_MEDIA_AURAL);

            put(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission_group.NOTIFICATIONS);
    }};

    /**
     * 特殊权限 用途 说明
     */
    public static final ArrayMap<String, String[]> specialPermission = new ArrayMap<String, String[]>(){{
        put(SYSTEM_ALERT_WINDOW, new String[]{ResUtils.getStr(R.string.fyFloatDialog), "悬浮在其它应用上方"});
        put(WRITE_SETTINGS, new String[]{ResUtils.getStr(R.string.fySysSet), "修改系统的设置数据"});
        put(NOTIFICATION_SERVICE, new String[]{ResUtils.getStr(R.string.fyNotify), "显示和发送通知"});
        put(REQUEST_INSTALL_PACKAGES, new String[]{ResUtils.getStr(R.string.installApp), "调用系统安装器安装应用"});
        put(MANAGE_EXTERNAL_STORAGE, new String[]{ResUtils.getStr(R.string.fySdCard), "读写设备上的媒体及文件"});
        put(VPN_SERVICE, new String[]{ResUtils.getStr(R.string.fyVPNConn), "VPN 服务"});
    }};

}
