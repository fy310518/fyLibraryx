package com.fy.baselibrary.utils.net;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 网络相关的工具类
 * Created by fangs on 2017/3/1.
 */
public class NetUtils {

    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static final int NETWORK_TYPE_GSM = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN = 18;

    public interface NetworkType {
        String NETWORK_WIFI = "wifi";
        String NETWORK_4G = "4g";
        String NETWORK_3G = "3g";
        String NETWORK_2G = "2g";
        String NETWORK_UNKNOWN = "unknown";
        String NETWORK_NO = "no";
    }

    /**
     * 判断网络是否连接
     * @return true or false
     */
    public static boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) ConfigUtils.getAppCtx().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前网络类型
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return 网络类型
     * <ul>
     * <li>{@link NetworkType#NETWORK_WIFI   } </li>
     * <li>{@link NetworkType#NETWORK_4G     } </li>
     * <li>{@link NetworkType#NETWORK_3G     } </li>
     * <li>{@link NetworkType#NETWORK_2G     } </li>
     * <li>{@link NetworkType#NETWORK_UNKNOWN} </li>
     * <li>{@link NetworkType#NETWORK_NO     } </li>
     * </ul>
     */
    public static String getNetworkType() {
        String netType = NetworkType.NETWORK_NO;
        NetworkInfo info = getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {

                    case NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netType = NetworkType.NETWORK_2G;
                        break;

                    case NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netType = NetworkType.NETWORK_3G;
                        break;

                    case NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netType = NetworkType.NETWORK_4G;
                        break;
                    default:

                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            netType = NetworkType.NETWORK_3G;
                        } else {
                            netType = NetworkType.NETWORK_UNKNOWN;
                        }
                        break;
                }
            } else {
                netType = NetworkType.NETWORK_UNKNOWN;
            }
        }
        return netType;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;

        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取活动网络信息
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo() {
        return ((ConnectivityManager) ConfigUtils.getAppCtx().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    // 强制使用蜂窝数据网络-移动数据
    public static void forceSendRequestByMobileData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ConfigUtils.getAppCtx().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new NetworkRequest.Builder();
            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            // 强制使用蜂窝数据网络-移动数据
            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
            NetworkRequest build = builder.build();
            connectivityManager.requestNetwork(build, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    L.e("network", "" + network);
                    String body = "json";
//				String result = doJsonPost(network, "url", body);
//				L.e("result", result);
                }
            });
        }
    }

    /**
     * wifi连接时判断当前WiFi是否可用
     * @param ipString 例如：www.baidu.com
     * @return success表示网络畅通，否则网络不通
     */
    public static boolean Ping(String ipString) {
        boolean resault;
        Process p;
        try {
            // ping -c 2 -w 100 中 ，-c 是指ping的次数 2是指ping 2次 ，-w 100
            // 以秒为单位指定超时间隔，是指超时时间为100秒
            p = Runtime.getRuntime().exec("ping -c 2 -w 2 " + ipString);
            int status = p.waitFor();

            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            System.out.println("Return ============" + buffer.toString());

            if (status == 0) {
                resault = true;
            } else {
                resault = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resault = false;
        }

        return resault;
    }

    /**
     * 判断端口是否被占用
     * @param port  端口号
     * @return 返回 一个可用端口
     */
    public static int isPortOccupied(int port){
        int resultPort = port;

        StringBuffer buffer = new StringBuffer();
        InputStream input;
        BufferedReader reader;
        try {
            Process p = Runtime.getRuntime().exec("netstat -an");// 获取所有被占用端口
            input = p.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));

            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            try {
                if(null != input){
                    input.close();
                    input = null;
                }
                if(null != reader){
                    reader.close();
                    reader = null;
                }
            } catch (IOException e) {
            }

            for(int i = port; i < 65535; i++){
                if(!buffer.toString().contains(":" + i)){
                    resultPort = i;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultPort;
    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }


}
