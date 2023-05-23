package com.fy.baselibrary.utils.hardware.ble;

import androidx.annotation.Nullable;

import com.fy.baselibrary.retrofit.ServerException;

/**
 * description 蓝牙 回调接口
 * Created by fangs on 2023/5/22 16:42.
 */
public class BleSocketListener {

    /**
     */
    public void onOpen() {
    }

    /**
     * 接收消息
     * @param text
     */
    public void onMessage(String text) {
    }

    /**
     * 接收消息
     * @param bytes
     */
    public void onMessage(Byte[] bytes) {
    }

    /**
     * 连接已关闭
     */
    public void onClosed(){}

    /**
     * 失败
     */
    public void onFailure(@Nullable ServerException exception){}

    /**
     * 连接成功
     */
    public void onConnSuccess(){}
}
