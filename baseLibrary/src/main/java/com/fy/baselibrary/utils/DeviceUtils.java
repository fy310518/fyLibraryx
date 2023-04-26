package com.fy.baselibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.security.EncryptUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 获取设备信息 工具类
 * Created by fangs on 2017/3/22.
 */
public class DeviceUtils {

    private DeviceUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取 手机 版本号
     */
    public static String getDeviceVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getModel() {
        String model = Build.MODEL;

        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceMake() {
        return Build.BRAND;
    }

    /**
     * 获取设备 Android ID
     * @return the android id of device
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        Context context = ConfigUtils.getAppCtx();
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     * @param ctx
     * @return 手机IMEI
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return "";
    }

    /**
     * 获取Sim卡运营商名称
     * <p>中国移动、如中国联通、中国电信</p>
     *
     * @return 移动网络运营商名称
     */
    public static String getSimOperatorByMnc() {
        TelephonyManager tm = (TelephonyManager) ConfigUtils.getAppCtx().getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm != null ? tm.getSimOperator() : null;
        if (operator == null) return "未知";
        switch (operator) {
            case "46000":
            case "46002":
            case "46007":
                return "中国移动";
            case "46001":
                return "中国联通";
            case "46003":
                return "中国电信";
            default:
                return "未知";
        }
    }

    /**
     * 获取当前APP所在手机的 IP 地址
     * @return ""
     */
    public static String getCurIp() {
        WifiManager wifiManager = (WifiManager) ConfigUtils.getAppCtx().getApplicationContext().
                getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();


        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                ((ipAddress >> 24) & 0xFF);
    }

    /**
     * 获取当前APP所在手机的 MAC 地址
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getCurMac() {
        WifiManager wifi = (WifiManager) ConfigUtils.getAppCtx()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifi != null ? wifi.getConnectionInfo() : null;

        return info != null ? info.getMacAddress() : "";
    }

    /**
     * 获取设备的UUID
     * @return
     */
    public static String getUUID() {
        Context context = ConfigUtils.getAppCtx();
        UUID uuid = null;
        // Android SOCKS_ACCOUNT_ID
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId
                        .getBytes("utf8"));
            } else {
                // IMEI
                String deviceId = getIMEI(context);

                if (TextUtils.isEmpty(deviceId)) {
                    // MAC
                    WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifi.getConnectionInfo();
                    deviceId = info.getMacAddress().replace(":", "");
                }

                uuid = deviceId != null ?
                        UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) :
                        UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return uuid.toString();
    }

    /**
     * 获取 DEVICE_ID, ANDROID_ID, MACId；三个值合并为一个字符串进行MD5作为设备唯一标识
     * @return
     */
    public static String getUniqueDeviceID() {
        Context context = ConfigUtils.getAppCtx();
        String deviceId = getIMEI(context);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = "";
        }

        String androidid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
                .toLowerCase(); //统一使用小写，是否存在不同API获取的结果大小写不同的情况未知
        if (TextUtils.isEmpty(androidid)) {
            androidid = "";
        } else {
            if ("9774d56d682e549c".equals(androidid)) {
                androidid = "";
            }
        }

        String mac = getCurMac();

        String uniqueID = deviceId + androidid + mac;
        if (TextUtils.isEmpty(uniqueID)) {
            //为空的话生成随机字符串
            uniqueID = EncryptUtils.getMD5(UUID.randomUUID().toString());
        } else {
            uniqueID = EncryptUtils.getMD5(uniqueID);
        }

        return uniqueID;
    }


    /**
     * 打印设备内存信息
     */
    public static void getDevices() {
        //应用程序最大可用内存
        int maxMemory = ((int) Runtime.getRuntime().maxMemory()) / 1024 / 1024;
        //应用程序已获得内存
        long totalMemory = ((int) Runtime.getRuntime().totalMemory()) / 1024 / 1024;
        //应用程序已获得内存中未使用内存
        long freeMemory = ((int) Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        System.out.println("---> maxMemory=" + maxMemory + "M,totalMemory=" + totalMemory + "M,freeMemory=" + freeMemory + "M");

        L.e("maxMemory" + maxMemory + "totalMemory" + totalMemory + "freeMemory" + freeMemory);
    }

    /**
     * 判断设备是否 rooted.
     * @return {@code true}: yes{@code false}: no
     */
    public static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }
}
