package com.fy.baselibrary.retrofit;

/**
 * 网络请求 返回数据 格式化接口
 * Created by fangs on 2018/7/23 16:25.
 */
public interface BaseBean<T> {

    /**
     * data : {}
     * errorCode : 0
     * errorMsg :
     */

    /**
     * 获取请求返回状态码
     * @return 状态码
     */
    int getResultCode();

    /**
     * 获取请求返回信息
     * @return 消息
     */
    String getResultMsg();

    /**
     * 获取请求返回对象
     * @return 泛型
     */
    T getResultData();

    /**
     * 判断请求是否成功
     * @return true/false
     */
    boolean isSuccess();
}
