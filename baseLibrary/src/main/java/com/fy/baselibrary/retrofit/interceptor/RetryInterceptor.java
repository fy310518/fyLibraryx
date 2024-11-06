package com.fy.baselibrary.retrofit.interceptor;

import com.fy.baselibrary.utils.notify.L;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * description 重试拦截器
 * Created by fangs on 2024/11/5 16:38.
 */
public class RetryInterceptor implements Interceptor {

    private int maxRetries = 3;
    private int retryCount = 0;

    public RetryInterceptor() {

    }

    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url();
        Response response = chain.proceed(request);

        while (!response.isSuccessful() && retryCount < maxRetries){
            retryCount++;
            L.e("net 响应",  retryCount + "--重试拦截器--" + url.encodedPath());
            response = chain.proceed(request);
        }
        return response;
    }

}
