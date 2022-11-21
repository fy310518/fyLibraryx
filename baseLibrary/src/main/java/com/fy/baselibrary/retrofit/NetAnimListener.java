package com.fy.baselibrary.retrofit;

/**
 * description 定义网络请求 等待动画 接口
 * Created by fangs on 2022/11/21 15:47.
 */
public interface NetAnimListener {

    /**
     * 定义 启动动画
     * 设置 currentType
     */
    void start(int currentType);

    /**
     * 停止动画
     */
    void stop();
}
