package com.fy.baselibrary.utils.imgload.imgprogress;

import android.content.Context;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.RequestModule;
import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.retrofit.interceptor.FileDownInterceptor;
import com.fy.baselibrary.retrofit.interceptor.RequestHeaderInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cache.CacheNetworkInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cache.IsUseCacheInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cookie.AddCookiesInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cookie.CacheCookiesInterceptor;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.security.SSLUtil;

import java.io.InputStream;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * 更改Glide配置，替换Glide 默认http通讯组件
 */
@GlideModule
public class OkHttpLibraryGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);

        //添加拦截器到Glide
        OkHttpClient.Builder builder = getClient();

        //加载图片 信任所有证书
        builder.sslSocketFactory(SSLUtil.createSSLSocketFactory());
        builder.hostnameVerifier(SSLUtil.DO_NOT_VERIFY);
        OkHttpClient okHttpClient = builder.build();

        //原来的是  new OkHttpUrlLoader.Factory()；
        registry.replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(okHttpClient));
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    }

    //完全禁用清单解析
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }


    protected static OkHttpClient.Builder getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(Constant.DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .readTimeout(Constant.DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .writeTimeout(Constant.DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)//错误重连
                .addInterceptor(new RequestHeaderInterceptor())
                .addInterceptor(new FileDownInterceptor())
                .hostnameVerifier((hostname, session) -> {
                    return true;//强行返回true 即验证成功
                })
                .proxy(Proxy.NO_PROXY)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1));

        if (ConfigUtils.isDEBUG()){//是否使用日志拦截器
            builder.addInterceptor(RequestModule.getResponseIntercept());
        }

        List<String> cerFileNames = ConfigUtils.getCerFileName();
        if (!cerFileNames.isEmpty()){
            Object[] sslData = SSLUtil.getSSLSocketFactory(cerFileNames.toArray(new String[]{}));
            if (null != sslData) builder.sslSocketFactory((SSLSocketFactory)sslData[0], (X509TrustManager)sslData[1]);
        } else {
            builder.sslSocketFactory(SSLUtil.createSSLSocketFactory());
            builder.hostnameVerifier(SSLUtil.DO_NOT_VERIFY);
        }

        return builder;
    }

}
