package com.fy.baselibrary.application.ioc;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.statuslayout.OnStatusAdapter;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import retrofit2.Converter;


/**
 * 应用框架基础配置工具类 （应用启动时候初始化）
 * Created by fangs on 2018/7/13.
 */
public class ConfigUtils {

    public volatile static ConfigUtils instance;
    private ConfigBuilder builder;

    private ConfigUtils() {
    }

    public static synchronized ConfigUtils getInstance() {
        if (null == instance) {
            synchronized (ConfigUtils.class) {
                if (null == instance) {
                    instance = new ConfigUtils();
                }
            }
        }

        return instance;
    }

    private void setBuilder(ConfigBuilder builder) {
        this.builder = builder;
    }

    public static Context getAppCtx() {
        return ConfigUtils.getInstance().builder.context;
    }

    public static void setAppCtx(Context context) {
        ConfigUtils.getInstance().builder.setContext(context);
    }

    public static String getFilePath() {
        return ConfigUtils.getInstance().builder.filePath;
    }

    public static int getType() {
        return ConfigUtils.getInstance().builder.type;
    }

    public static boolean isDEBUG() {
        return ConfigUtils.getInstance().builder.DEBUG;
    }

    public static boolean isFontDefault() {
        return ConfigUtils.getInstance().builder.isFontDefault;
    }

    public static boolean isEnableCacheInterceptor() {
        return ConfigUtils.getInstance().builder.isEnableCacheInterceptor;
    }

    public static String getBaseUrl() {
        return ConfigUtils.getInstance().builder.BASE_URL;
    }
    public static long getTimeout() {
        return ConfigUtils.getInstance().builder.timeout;
    }

    public static String getAddCookieKey(){return ConfigUtils.getInstance().builder.addCookieKey;}
    public static String getCookieDataKey(){return ConfigUtils.getInstance().builder.cookieData;}

    public static String getTokenKey(){return ConfigUtils.getInstance().builder.token;}

    public static List<Interceptor> getImgInterceptor(){return ConfigUtils.getInstance().builder.imgInterceptors;}

    public static List<Interceptor> getInterceptor(){return ConfigUtils.getInstance().builder.interceptors;}

    public static List<Interceptor> getNetInterceptor(){return ConfigUtils.getInstance().builder.netInterceptors;}

    public static List<Converter.Factory> getConverterFactory(){return ConfigUtils.getInstance().builder.converterFactories;}

    public static OnStatusAdapter getOnStatusAdapter(){return ConfigUtils.getInstance().builder.statusAdapter;}

    public static String getCer() {
        return ConfigUtils.getInstance().builder.cer;
    }

    public static List<String> getCerFileName() {
        return ConfigUtils.getInstance().builder.cerFileNames;
    }

    public static int getTitleColor(){
        return ConfigUtils.getInstance().builder.titleColor;
    }

    public static int getBgColor(){
        return ConfigUtils.getInstance().builder.bgColor;
    }

    public static boolean isTitleCenter(){
        return ConfigUtils.getInstance().builder.isTitleCenter;
    }

    public static int getBackImg(){
        return ConfigUtils.getInstance().builder.backImg;
    }


    public static class ConfigBuilder {
        /** 是否  DEBUG 环境*/
        boolean DEBUG;
        /** 是否  跟随系统字体大小 默认跟随*/
        boolean isFontDefault = true;

        /** 应用 文件根目录 名称（文件夹） */
        String filePath = "";
        int type = 0;

        /** 标题栏背景色 */
        int bgColor;
        /** 标题是否居中 */
        boolean isTitleCenter;
        /** 标题字体颜色 */
        int titleColor;
        /** 标题栏 返回按钮 图片 */
        int backImg;

        /** 网络请求 服务器地址 url */
        String BASE_URL = "";
        /** 默认的超时时间 单位毫秒 */
        public long timeout = 60 * 1000;
        /** https 公钥证书字符串 */
        String cer = "";
        /** https 公钥证书 文件名字符串【带后缀名】集合（放在 assets 目录下） */
        List<String> cerFileNames = new ArrayList<>();

        /** 是否  启用缓存拦截器 */
        boolean isEnableCacheInterceptor;
        /** 向网络请求 添加 cookie 数据 的 key */
        String addCookieKey = "cookie";
        /** 从网络请求 响应数据中拿到返回的 cookie数据 的 key */
        String cookieData = "set-cookie";

        /** token key */
        String token = "X-Access-Token";
        /** 添加 自定义应用拦截器；如：token 拦截器 */
        List<Interceptor> interceptors  = new ArrayList<>();
        /** 添加 自定义网络拦截器； */
        List<Interceptor> netInterceptors  = new ArrayList<>();
        /** 添加 自定义转换器； */
        List<Converter.Factory> converterFactories = new ArrayList<>();

        List<Interceptor> imgInterceptors  = new ArrayList<>();

        /** 多状态布局 适配器 */
        OnStatusAdapter statusAdapter;

        Context context;

        public ConfigBuilder setDEBUG(boolean DEBUG) {
            this.DEBUG = DEBUG;
            return this;
        }

        public ConfigBuilder setFontDefault(boolean fontDefault) {
            isFontDefault = fontDefault;
            return this;
        }

        public ConfigBuilder setEnableCacheInterceptor(boolean enableCacheInterceptor) {
            isEnableCacheInterceptor = enableCacheInterceptor;
            return this;
        }

        public ConfigBuilder setBASE_URL(String BASE_URL) {
            this.BASE_URL = BASE_URL;
            return this;
        }

        public ConfigBuilder addCerFileName(@NonNull String cerFileName) {
            this.cerFileNames.add(cerFileName);
            return this;
        }

        public ConfigBuilder setCer(String cer) {
            this.cer = cer;
            return this;
        }

        public ConfigBuilder setBgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public ConfigBuilder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public ConfigBuilder setTitleCenter(boolean titleCenter) {
            isTitleCenter = titleCenter;
            return this;
        }

        public ConfigBuilder setBackImg(int backImg) {
            this.backImg = backImg;
            return this;
        }

        public ConfigBuilder setBaseFile(String filePath, int type) {
            this.filePath = filePath == null ? "" : filePath;
            this.type = type;
            return this;
        }

        public ConfigBuilder setCookie(String addCookieKey, String cookieData) {
            this.addCookieKey = TextUtils.isEmpty(addCookieKey) ? "" : addCookieKey;
            this.cookieData = TextUtils.isEmpty(cookieData) ? "" : cookieData;
            return this;
        }

        public ConfigBuilder setToken(String token) {
            this.token = token == null ? "" : token;
            return this;
        }

        public ConfigBuilder setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public ConfigBuilder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public ConfigBuilder addImgInterceptor(Interceptor interceptor) {
            imgInterceptors.add(interceptor);
            return this;
        }

        public ConfigBuilder addNetInterceptor(Interceptor interceptor) {
            netInterceptors.add(interceptor);
            return this;
        }

        public ConfigBuilder addConverterFactory(Converter.Factory converter) {
            converterFactories.add(converter);
            return this;
        }

        public ConfigBuilder setStatusAdapter(OnStatusAdapter statusAdapter) {
            this.statusAdapter = statusAdapter;
            return this;
        }

        public ConfigBuilder setContext(Context context){
            this.context = context;
            return this;
        }

        public void create(Context context){
            this.context = context;
            ConfigUtils.getInstance().setBuilder(this);
        }
    }
}
