package com.fy.baselibrary.retrofit.load;

import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * description 自定义 超时拦截器, 处理 某些请求 单独设置 请求、响应、连接 超时【单位 秒】
 * 例子：
 * @Headers({"CONNECT_TIMEOUT:60", "READ_TIMEOUT:60", "WRITE_TIMEOUT:60"})
 * @POST
 * Observable<ResponseBody> preLogin(@Url String apiUrl, @FieldMap ArrayMap<String, Object> params);
 *
 * Created by fangs on 2023/1/30 17:40.
 */
public class TimeoutInterceptor implements Interceptor {

    public static final String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
    public static final String READ_TIMEOUT = "READ_TIMEOUT";
    public static final String WRITE_TIMEOUT = "WRITE_TIMEOUT";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        int connectTimeout = chain.connectTimeoutMillis();
        int readTimeout = chain.readTimeoutMillis();
        int writeTimeout = chain.writeTimeoutMillis();

        String connectNew = request.header(CONNECT_TIMEOUT);
        String readNew = request.header(READ_TIMEOUT);
        String writeNew = request.header(WRITE_TIMEOUT);

        if (!TextUtils.isEmpty(connectNew)) {
            connectTimeout = Integer.valueOf(connectNew);
        }
        if (!TextUtils.isEmpty(readNew)) {
            readTimeout = Integer.valueOf(readNew);
        }
        if (!TextUtils.isEmpty(writeNew)) {
            writeTimeout = Integer.valueOf(writeNew);
        }

        return chain
                .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .proceed(request);
    }

}
