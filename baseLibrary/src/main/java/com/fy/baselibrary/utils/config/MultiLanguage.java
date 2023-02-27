package com.fy.baselibrary.utils.config;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.fy.baselibrary.R;
import com.fy.baselibrary.utils.JumpUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.cache.SpfAgent;

import java.util.HashMap;
import java.util.Locale;

/**
 * description 多语言工具类
 * Created by fangs on 2022/7/25 14:27.
 */
public class MultiLanguage {

    // 缓存 的 语言key；根据 这个的值 从 mAllLanguages 获取 Locale 对象
    public static final String SP_LANGUAGE = "SP_LANGUAGE";
    // 系统 语言已改变 key
    public static final String SYS_localeStatus = "SYS_localeStatus";


    public static final String system = "Language0";
    public static final String English = "Language1";
    public static final String Japanese = "Language2";
    public static final String Korean = "Language3";
    public static final String ChineseSimplified = "Language4";
    public static final String ChineseTraditional = "Language5";

    // 支持切换的语言 集合
    public static HashMap<String, Locale> mAllLanguages = new HashMap<String, Locale>() {{
//        put(English, Locale.US);
//        put(Japanese, Locale.JAPAN);
//        put(Korean, Locale.KOREA);
//        put(ChineseSimplified, Locale.CHINA);
//        put(ChineseTraditional, Locale.TAIWAN);
    }};

    /**
     * 初始化 先添加语言
     * @param languageKey  格式 参考 默认添加的
     * @param locale
     */
    public static void addLanguage(String languageKey, Locale locale){
        mAllLanguages.put(languageKey, locale);
    }

    /**
     * 第一次进入app时保存系统选择的语言(为了选择跟随系统语言时使用，如果不保存，切换语言后就拿不到了）
     * 1、Application 的 super.attachBaseContext(saveSystemCurrentLanguage(context)) 调用
     */
    public static Context saveSystemCurrentLanguage(Context context) {
        mAllLanguages.put(MultiLanguage.system, MultiLanguage.getSystemLocal());

        return updateResources(context, getSetLanguageLocale());
    }

    /**
     * 保存系统语言
     * 2、Application 的 super.onConfigurationChanged 后调用
     * 用户在系统设置页面切换语言时保存系统选择语言(为了选择随系统语言时使用，如果不保存，切换语言后就拿不到了）
     * @param newConfig
     */
    public static void saveSystemCurrentLanguage(Configuration newConfig, Application context) {
        if(!mAllLanguages.get(MultiLanguage.system).equals(MultiLanguage.getSystemLocal(newConfig))){
            mAllLanguages.put(MultiLanguage.system, MultiLanguage.getSystemLocal(newConfig));

            updateResources(context, getSetLanguageLocale());
            setApplicationLanguage(context);

            // 标记 系统语言已经改变
            SpfAgent.init().saveBoolean(MultiLanguage.SYS_localeStatus, true)
                    .commit(true);
        }
    }

    /**
     * 2.1 如果系统语言已经改变 重启app 【避免系统配置改变，app 界面文案错乱】
     * ActivityLifecycleCallbacks  onActivityResumed 中调用
     */
    public static void restartApp(Activity act, Class actClass) {
        if (SpfAgent.init().getBoolean(MultiLanguage.SYS_localeStatus)) { // 系统语言已经改变
            SpfAgent.init().remove(MultiLanguage.SYS_localeStatus, true);
            JumpUtils.restartApp(act, actClass);
        }
    }

    /**
     * 设置语言类型
     * 3、Application 的 super.onCreate() 后 和 所有 Activity 的 super.onCreate() 前
     *
     * 3.1 解决加载 webView 导致语言混乱
     *      * 1、Application 的 super.onCreate() 后添加 WebView(applicationContext).destroy()
     *      * 2、在每个包含WebView的Activity中添加一次手动设置语言得逻辑---> MultiLanguage.setApplicationLanguage(applicationContext)
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    /**
     * 保存选择的语言
     * 4、  MultiLanguage.saveSelectLanguage(activity, languageNum)
     *      MultiLanguage.saveSelectLanguage(applicationContext, languageNum)
     *  调用 restartApp() 重启app
     * @param select
     */
    public static void saveSelectLanguage(Context context, int select) {
        SpfAgent.init().saveInt(MultiLanguage.SP_LANGUAGE, select)
                .commit(true);

        MultiLanguage.setApplicationLanguage(context);
    }

    /**
     * 更新语言设置
     * @param context
     * @param locale
     * @return
     */
    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    /**
     * 获取系统语言
     * @param newConfig
     * @return
     */
    public static Locale getSystemLocal(Configuration newConfig) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = newConfig.getLocales().get(0);
        } else {
            locale = newConfig.locale;
        }
        return locale;
    }

    /**
     * 获取系统语言
     * @return
     */
    public static Locale getSystemLocal() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }


    /**
     * 获取选择的语言
     * @return
     */
    private static Locale getSetLanguageLocale() {
        Locale locale = getSetLocale();
        if (null != locale){
            return locale;
        } else {
            return getSystemLocale();
        }
    }

    /**
     * 获取系统的locale
     * @return Locale对象
     */
    public static Locale getSystemLocale() {
        return mAllLanguages.get(MultiLanguage.system);
    }

    /**
     * 获取选择的语言 Locale
     */
    public static Locale getSetLocale() {
        int languageNum = SpfAgent.init().getInt(SP_LANGUAGE, 0);
        return mAllLanguages.get("Language" + languageNum);
    }

    /**
     * 当前选择的语言 String
     */
    public static String getSelectLanguage() {
        String[] languageData = ResUtils.getStrArray(R.array.languageList);
        return languageData[SpfAgent.init().getInt(SP_LANGUAGE, 0)];
    }

}
