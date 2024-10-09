package com.fy.baselibrary.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.media.UriUtils;
import com.fy.baselibrary.utils.notify.T;

import java.io.File;
import java.util.ArrayList;

/**
 * 界面跳转工具类
 * Created by fangs on 2017/5/9.
 */
public class JumpUtils {

    private JumpUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 从fragment 跳转到指定的 activity
     * @param fragment
     * @param bundle
     * @param actClass
     */
    public static void jump(Fragment fragment, Class actClass, Bundle bundle) {
        Intent intent = new Intent(fragment.getContext(), actClass);

        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(fragment.getActivity()).toBundle();
            }
        }

        fragment.startActivity(intent, options);
    }

    /**
     * 从 activity 跳转到指定的 activity
     * @param actClass
     * @param bundle
     */
    public static void jump(Activity act, Class actClass, Bundle bundle) {
        Intent intent = new Intent(act, actClass);

        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(act).toBundle();
            }
        }

        act.startActivity(intent, options);
//        第一个参数 下一界面进入效果；第二个参数 当前界面退出效果
//        act.overridePendingTransition(R.anim.anim_slide_left_in, R.anim.anim_slide_left_out);
    }

    /**
     * 从fragment 跳转到指定 Action 的activity
     * @param fragment
     * @param action
     * @param bundle
     */
    public static void jump(Fragment fragment, @NonNull String action, Bundle bundle) {
        String act = action.startsWith(".") ? AppUtils.getLocalPackageName() + action : action;

        Intent intent = new Intent(act);

        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(fragment.getActivity()).toBundle();
            }
        }

        fragment.startActivity(intent, options);
    }

    /**
     * 从 activity 跳转到指定 Action 的activity
     * @param activity
     * @param action
     * @param bundle
     */
    public static void jump(Activity activity, String action, Bundle bundle) {
        String act = action.startsWith(".") ? AppUtils.getLocalPackageName() + action : action;

        Intent intent = new Intent(act);

        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
            }
        }

        activity.startActivity(intent, options);
    }

    /**
     * 跳转到指定 Action 的activity 带回调结果的跳转
     * @param action    要跳转到的 action
     * @param bundle
     * @param requestCode 请求码
     */
    public static void jump(Activity activity, String action, Bundle bundle, int requestCode) {
        String act = action.startsWith(".") ? AppUtils.getLocalPackageName() + action : action;

        Intent intent = new Intent(act);
        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
            }
        }

        activity.startActivityForResult(intent, requestCode, options);
    }

    /**
     * 跳转到指定 activity  带回调结果的跳转
     * @param actClass    要跳转到的Activity
     * @param bundle
     * @param requestCode 请求码
     */
    public static void jump(Activity act, Class actClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(act, actClass);
        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(act).toBundle();
            }
        }

        act.startActivityForResult(intent, requestCode, options);
    }


// 传统方式 fragment 通过startActivityForResult() 启动新的activity 有接收不到返回结果的问题，解决方案如下：
//  一.只嵌套了一层Fragment（比如activity中使用了viewPager，viewPager中添加了几个Fragment） 在这种情况下要注意几个点：
//  1.在Fragment中使用startActivityForResult的时候，不要使用getActivity().startActivityForResult,而是应该直接使startActivityForResult()。
//  2.如果activity中重写了onActivityResult，那么activity中的onActivityResult一定要加上：
//      super.onActivityResult(requestCode, resultCode, data)。
//  如果违反了上面两种情况，那么onActivityResult只能够传递到activity中的，无法传递到Fragment中的。
//  没有违反上面两种情况的前提下，可以直接在Fragment中使用startActivityForResult和onActivityResult，和在activity中使用的一样。
//  二：使用aop 在Activity的onActivityResult() 执行之后，通过回调接口 获取返回结果
//      使用方式同 activity 跳转到指定 activity  带回调结果的跳转

    /**
     * 从fragment 跳转到指定的 activity; 带回调结果的跳转
     * @param fragment
     * @param actClass
     * @param bundle
     * @param requestCode
     */
    public static void jump(Fragment fragment, Class actClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), actClass);
        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(fragment.getActivity()).toBundle();
            }
        }

        fragment.startActivityForResult(intent, requestCode, options);
    }

    /**
     * 从fragment 跳转到指定 action 的 activity; 带回调结果的跳转
     * @param fragment
     * @param action
     * @param bundle
     * @param requestCode
     */
    public static void jump(Fragment fragment, String action, Bundle bundle, int requestCode) {
        String act = action.startsWith(".") ? AppUtils.getLocalPackageName() + action : action;

        Intent intent = new Intent(act);
        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(fragment.getActivity()).toBundle();
            }
        }

        fragment.startActivityForResult(intent, requestCode, options);
    }

//////////////////////////////////反射跳转 start///////////////////////////////////////////////
    /**
     * 使用反射 跳转到指定 路径的 activity
     * @param act
     * @param bundle
     * @param classPath
     */
    public static void jumpReflex(Activity act, Bundle bundle, String classPath){
        try {
            Class cla = Class.forName(classPath);
            jump(act, cla, bundle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用反射 跳转到指定 路径的 activity
     * @param fragment
     * @param bundle
     * @param classPath
     */
    public static void jumpReflex(Fragment fragment, Bundle bundle, String classPath){
        try {
            Class cla = Class.forName(classPath);
            jump(fragment, cla, bundle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//////////////////////////////////反射跳转 end///////////////////////////////////////////////

    /**
     * 退出当前activity 并带数据回到上一个Activity
     * @param act
     * @param bundle 可空
     */
    public static void jumpResult(Activity act, Bundle bundle){
        Intent intent = new Intent();
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        act.setResult(Activity.RESULT_OK, intent);
        act.finishAfterTransition();
    }

    /**
     * 退出当前activity
     */
    public static void exitActivity(Activity act) {
        act.finishAfterTransition();
    }

    /**
     * 返回桌面
     * @param act
     */
    public static void backDesktop(Activity act){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            act.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出整个应用
     * @param act
     */
    public static void exitApp(Activity act, Class actClass){
        Intent intent = new Intent(act, actClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意
        intent.putExtra("exitApp", true);//添加标记
        act.startActivity(intent);
    }

    /**
     * 重启app
     * @param act
     * @param actClass  启动页activity
     */
    public static void restartApp(Activity act, Class actClass) {
        Intent intent = new Intent(act, actClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        act.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 启动指定 url 的 第三方应用 界面
     * @param act
     * @param url  如："gc://pull.gc.circle/conn/start?type=110"
     * @param bundle 这里Intent当然也可传递参数,但是一般情况下都会放到上面的URL中进行传递
     */
    public static void jumpUrl(Activity act, String url, Bundle bundle){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent();
        intent.setData(uri);

        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(act).toBundle();
            }
        }

        try {
            act.startActivity(intent, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动指定 包名 的 第三方应用
     * @param act
     * @param packageName
     * @param bundle
     */
    public static void jumpPackage(Activity act, String packageName, Bundle bundle) {
        PackageManager packageManager = act.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);

        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (null != bundle) {
                intent.putExtras(bundle);
            }
            act.startActivity(intent);
        } catch (Exception e) {
            T.show(R.string.appNoInstall, -1);
            e.printStackTrace();
        }
    }

    /**
     * 启动指定 包名 的第三方应用 的指定 路径的 activity
     * @param act
     * @param packageName 目标应用 应用id
     * @param path        目标activity路径
     * @param bundle
     */
    public static void jumpPackageAct(Activity act, String packageName, String path, Bundle bundle) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, path);
        intent.setComponent(componentName);
        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(act).toBundle();
            }
        }

        try {
            act.startActivity(intent, options);
        } catch (Exception e) {
            T.show(R.string.appNoInstall, -1);
            e.printStackTrace();
        }
    }

    /**
     * 启动指定 包名 的第三方应用 的指定 路径的 activity, 带回调结果的跳转
     * @param act
     * @param packageName
     * @param path
     * @param bundle
     * @param requestCode
     */
    public static void jumpPackageAct(Activity act, String packageName, String path, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, path);
        intent.setComponent(componentName);
        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(act).toBundle();
            }
        }

        try {
            act.startActivityForResult(intent, requestCode, options);
        } catch (Exception e) {
            T.show(R.string.appNoInstall, -1);
            e.printStackTrace();
        }
    }

    /**
     * 启动指定 包名 的第三方应用 的指定 路径的 activity, 带回调结果的跳转
     * @param fragment
     * @param packageName
     * @param path
     * @param bundle
     * @param requestCode
     */
    public static void jumpPackageAct(Fragment fragment, String packageName, String path, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, path);
        intent.setComponent(componentName);
        Bundle options = null;
        if (null != bundle) {
            intent.putExtras(bundle);
            String transition =  bundle.getString("transition", "");
            if (!TextUtils.isEmpty(transition)){
                options = ActivityOptions.makeSceneTransitionAnimation(fragment.getActivity()).toBundle();
            }
        }

        try {
            fragment.startActivityForResult(intent, requestCode, options);
        } catch (Exception e) {
            T.show(R.string.appNoInstall, -1);
            e.printStackTrace();
        }
    }

    /**
     * 跳转到 本应用的 指定action 的设置界面
     * @param act
     * @param action  如： Settings.ACTION_APP_NOTIFICATION_SETTINGS （通知设置）
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void jumpDetailsSetting(Activity act, String action){
        ApplicationInfo appInfo = act.getApplicationInfo();

        Intent localIntent = new Intent(action);
        //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
        localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, act.getPackageName());
        localIntent.putExtra(Settings.EXTRA_CHANNEL_ID, appInfo.uid);
        act.startActivity(localIntent);
    }

    /**
     * 跳转到浏览器 打开指定 URL链接
     * @param act
     * @param url
     */
    public static void jump(Activity act, String url){
        jumpWeb(act, url, null);
    }

    /**
     * 跳转到浏览器 打开指定 URL链接
     * @param act
     * @param url
     * @param componentName 【可以跳转到 指定的浏览器打开网页，前提是 已经安装了指定浏览器】
     */
    public static void jumpWeb(Activity act, String url, ComponentName componentName){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if(null != componentName){
            if(AppUtils.isPackageExist(act, componentName.getPackageName())){
                intent.setComponent(componentName);
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            act.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拨打电话
     * 直接拨打电话 需要申请权限：<uses-permission android:name="android.permission.CALL_PHONE" />
     * 手动点击拨打 不需要权限
     * @param action  [Intent.ACTION_DIAL(手动点击拨打), Intent.ACTION_CALL(直接拨打电话)]
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context ctx, String action, String phoneNum) {
//        if (Validator.isMobile(phoneNum) || Validator.isPhone(phoneNum)){
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        ctx.startActivity(intent);
    }

    /**
     * 系统 分享
     * @param act
     * @param action		Intent.EXTRA_TEXT 文本分享；Intent.EXTRA_STREAM，单文件分享；Intent.ACTION_SEND_MULTIPLE多图分享
     * @param title			分享 标题
     * @param shareData		分享 内容
     * @expand 注册广播 在 onReceive 回调中 通过 ComponentName extra = intent.getParcelableExtra(Intent.EXTRA_CHOSEN_COMPONENT);可以得知 用户分享到哪里去
     */
    public static void jumpShare(Activity act, String action, String title, String... shareData){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        if (action.equals(Intent.EXTRA_TEXT) && shareData.length > 0){
            intent.putExtra(Intent.EXTRA_TEXT, shareData[0]);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/*");
        } else if (action.equals(Intent.EXTRA_STREAM) && shareData.length > 0) {
            if(!FileUtils.fileIsExist(shareData[0])) return;

            intent.putExtra(Intent.EXTRA_STREAM, UriUtils.fileToUri(new File(shareData[0])));
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("*/*");
        } else if (action.equals(Intent.ACTION_SEND_MULTIPLE) && shareData.length > 0) {
            ArrayList<Uri> imageUris = new ArrayList<>();
            for(String data : shareData){
                if(FileUtils.fileIsExist(data)){
                    imageUris.add(UriUtils.fileToUri(new File(data)));
                }
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
        } else {
            return;
        }

        PendingIntent pi = PendingIntent.getBroadcast(ConfigUtils.getAppCtx(), 10000, intent, PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            act.startActivity(Intent.createChooser(intent, title, pi.getIntentSender()));
        } else {
            act.startActivity(Intent.createChooser(intent, title));
        }
    }

    /**
     * 调用系统安装器安装apk(适配 Android 7.0 在应用间共享文件)
     *
     * @param context 上下文
     * @param file apk文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, AppUtils.getFileProviderName(), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 卸载软件
     *
     * @param context
     * @param packageName
     */
    public static void uninstallApk(Context context, String packageName) {
        if (AppUtils.isPackageExist(context, packageName)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            context.startActivity(uninstallIntent);
        }
    }


    /**
     * 国内手机厂商白名单跳转工具类
     * @return
     */
    public static void getSettingIntent(Context context){
        ComponentName componentName = null;

        String brand = Build.BRAND;

        switch (brand.toLowerCase()){
            case "samsung":
                componentName = new ComponentName("com.samsung.android.sm",
                        "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
                break;
            case "huawei":
                componentName = new ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                break;
            case "xiaomi":
                componentName = new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity");
                break;
            case "vivo":
                componentName = new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
                break;
            case "oppo":
                componentName = new ComponentName("com.coloros.oppoguardelf",
                        "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
                break;
            case "360":
                componentName = new ComponentName("com.yulong.android.coolsafe",
                        "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");
                break;
            case "meizu":
                componentName = new ComponentName("com.meizu.safe",
                        "com.meizu.safe.permission.SmartBGActivity");
                break;
            case "oneplus":
                componentName = new ComponentName("com.oneplus.security",
                        "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
                break;
            default:
                break;
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(componentName!=null){
            intent.setComponent(componentName);
        }else{
            intent.setAction(Settings.ACTION_SETTINGS);
        }

        try {
            context.startActivity(intent);
        }catch (Exception e){
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    /**
     * 是否在白名单内
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isSystemWhiteList(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        String packageName = context.getPackageName();
        boolean isWhite = pm.isIgnoringBatteryOptimizations(packageName);
        return isWhite;
    }


    /**
     * 适配 8.0以上系统  不再允许后台service直接通过startService方式去启动，
     *
     * manifest 添加 权限：<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
     * 在 service 的 onStartCommand 中调用 startForeground() 启动前台服务
     * @param act
     * @param actClass
     * @param bundle
     */
    public static void jumpService(@NonNull Activity act, @NonNull Class actClass, @Nullable Bundle bundle) {
        Intent intent = new Intent(act, actClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!AppUtils.isBackground(act)){
                act.startForegroundService(intent);
            }
        } else {
            act.startService(intent);
        }
    }

}
