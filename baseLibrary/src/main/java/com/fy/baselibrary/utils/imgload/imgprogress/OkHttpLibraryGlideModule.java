package com.fy.baselibrary.utils.imgload.imgprogress;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.RequestModule;
import com.fy.baselibrary.retrofit.interceptor.FileDownInterceptor;
import com.fy.baselibrary.retrofit.interceptor.RequestHeaderInterceptor;
import com.fy.baselibrary.utils.security.SSLUtil;

import java.io.InputStream;
import java.net.Proxy;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * 1、更改Glide配置，替换 Glide 默认http通讯组件
 * 2、需要下载进度时候 继承 此类 并 添加 {@GlideModule} 注解
 * 3、添加 glide 更多支持类型 继承 此类 并 添加 {@GlideModule} 注解
 */
//@GlideModule
public class OkHttpLibraryGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);

        //原来的是  new OkHttpUrlLoader.Factory()；
        registry.replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(getClient()));
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    }

    //完全禁用清单解析
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }


    public static OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)//错误重连
                .addInterceptor(new RequestHeaderInterceptor())
                .addInterceptor(new FileDownInterceptor())
                .hostnameVerifier((hostname, session) -> {
                    return true;//强行返回true 即验证成功
                })
                .proxy(Proxy.NO_PROXY)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1));

        for(Interceptor interceptor : ConfigUtils.getImgInterceptor()){
            builder.addInterceptor(interceptor);
        }

        if (ConfigUtils.isDEBUG()){//是否使用日志拦截器
            builder.addInterceptor(RequestModule.getResponseIntercept());
        }

        //加载图片 信任所有证书
//        builder.sslSocketFactory(SSLUtil.createSSLSocketFactory());
//        builder.hostnameVerifier(SSLUtil.DO_NOT_VERIFY);
        builder.sslSocketFactory(SSLUtil.getSSLSocketFactory(), new SSLUtil.TrustAllManager());

        return builder.build();
    }

}
