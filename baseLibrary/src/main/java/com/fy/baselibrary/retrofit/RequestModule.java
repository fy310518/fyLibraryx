package com.fy.baselibrary.retrofit;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.converter.file.FileConverterFactory;
import com.fy.baselibrary.retrofit.interceptor.FileDownInterceptor;
import com.fy.baselibrary.retrofit.interceptor.RequestHeaderInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cache.CacheNetworkInterceptor;
import com.fy.baselibrary.retrofit.interceptor.cache.IsUseCacheInterceptor;
import com.fy.baselibrary.retrofit.load.TimeoutInterceptor;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.security.SSLUtil;
import com.google.gson.GsonBuilder;

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
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 提供依赖对象的实例
 * Created by fangs on 2017/5/15.
 */
//@Module
public final class RequestModule {

//    @Singleton
//    @Provides
    protected static Retrofit getService(OkHttpClient.Builder okBuilder) {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(FileConverterFactory.create())
                .addConverterFactory(getGsonConvertFactory())
                .baseUrl(ConfigUtils.getBaseUrl())
                .client(okBuilder.build());

        List<Converter.Factory> converterFactors = ConfigUtils.getConverterFactory();
        for (Converter.Factory converter : converterFactors) {
            retrofitBuilder.addConverterFactory(converter);
        }

        return retrofitBuilder.build();
    }

//    @Singleton
//    @Provides
    protected static GsonConverterFactory getGsonConvertFactory() {
        return GsonConverterFactory.create(new GsonBuilder()
                .setLenient()// json宽松
                .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
                .serializeNulls() //智能null
                .setPrettyPrinting()// 调教格式
                .disableHtmlEscaping() //默认是GSON把HTML 转义的
                .create());
//        return DES3GsonConverterFactory.create();//使用 自定义 GsonConverter
    }

//    @Singleton
//    @Provides
    protected static OkHttpClient.Builder getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(ConfigUtils.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(ConfigUtils.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(ConfigUtils.getTimeout(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)//错误重连
                .addInterceptor(new RequestHeaderInterceptor())
                .addInterceptor(new TimeoutInterceptor())
                .addInterceptor(new FileDownInterceptor())
//                .addInterceptor(new CacheCookiesInterceptor())
//                .addNetworkInterceptor(new AddCookiesInterceptor())
                .hostnameVerifier((hostname, session) -> {
                    return true;//强行返回true 即验证成功
                })
                .proxy(Proxy.NO_PROXY)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1));

        if (ConfigUtils.isEnableCacheInterceptor()) {//是否 添加缓存拦截器
            builder.addInterceptor(new IsUseCacheInterceptor())
                    .addNetworkInterceptor(new CacheNetworkInterceptor())
                    .cache(new Cache(FileUtils.folderIsExists(FileUtils.cache, ConfigUtils.getType()), 1024 * 1024 * 30L));
        }

        List<Interceptor> interceptors = ConfigUtils.getInterceptor();
        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }

        List<Interceptor> netInterceptors = ConfigUtils.getNetInterceptor();
        for (Interceptor interceptor : netInterceptors) {
            builder.addNetworkInterceptor(interceptor);
        }

        if (ConfigUtils.isDEBUG()){//是否使用日志拦截器
            builder.addInterceptor(getResponseIntercept());
        }

        List<String> cerFileNames = ConfigUtils.getCerFileName();
        if (!cerFileNames.isEmpty()){
            Object[] sslData = SSLUtil.getSSLSocketFactory(cerFileNames.toArray(new String[]{}));
            if (null != sslData) builder.sslSocketFactory((SSLSocketFactory)sslData[0], (X509TrustManager)sslData[1]);
        } else {
//            builder.sslSocketFactory(SSLUtil.createSSLSocketFactory());
//            builder.hostnameVerifier(SSLUtil.DO_NOT_VERIFY);
            builder.sslSocketFactory(SSLUtil.getSSLSocketFactory(), new SSLUtil.TrustAllManager());
        }

        return builder;
    }

//    @Singleton
//    @Provides
    public static HttpLoggingInterceptor getResponseIntercept() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                L.e("net 请求or响应", message);
//                FileUtils.fileToInputContent("log", "日志.txt", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return loggingInterceptor;
    }

}
