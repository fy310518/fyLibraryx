package com.fy.baselibrary.utils.hardware.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.text.TextUtils;

import com.fy.baselibrary.eventbus.EventBean;
import com.fy.baselibrary.eventbus.RxBus;
import com.fy.baselibrary.retrofit.ServerException;
import com.fy.baselibrary.retrofit.observer.RequestBaseObserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * description BluetoothSocket 管理类
 * Created by fangs on 2023/4/26 13:54.
 */
public class BluetoothSocketManager {

    private volatile static BluetoothSocketManager manager;

    private BluetoothSocketManager() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothSocketManager getInstance() {
        if (manager == null) {
            synchronized (BluetoothSocketManager.class) {
                if (manager == null) {
                    manager = new BluetoothSocketManager();
                }
            }
        }
        return manager;
    }

    public static final String UUIDString = "00001101-0000-1000-8000-00805F9B34FB";
    public static final String receiveData = "receive_data";
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothSocket mSocket;
    private InputStream in;
    private PrintWriter out;
    private Object lock = new Object();

    public void initClient(String remoteAddress) {
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(remoteAddress);
        try {
            mSocket = remoteDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUIDString));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private BluetoothServerSocket mServerSocket;
    public void initServer() {
        try {
            mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("server_name", UUID.fromString(UUIDString));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void beginListen() {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .flatMap((Function<Long, ObservableSource<String>>) aLong -> {
                    try {
                        if(null != mServerSocket){
                            if (null == mSocket) {
                                mSocket = mServerSocket.accept(); // 会阻塞线程
                            } else {
                                return Observable.error(new ServerException("success", 200));
                            }
                        } else {
                            if (!mSocket.isConnected()) {
                                mSocket.connect();  // 会阻塞线程
                            } else {
                                return Observable.error(new ServerException("success", 200));
                            }
                        }

                        in = mSocket.getInputStream();
                        out = new PrintWriter(mSocket.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return Observable.just("success");

                }).subscribeOn(Schedulers.io())
                .subscribe(new RequestBaseObserver<String>() {
                    @Override
                    protected void onSuccess(String t) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });


        Observable.interval(1, 1, TimeUnit.SECONDS)
                .flatMap((Function<Long, ObservableSource<String>>) aLong -> {
                    try {
                        if (mSocket.isConnected()) {
                            byte[] bt = new byte[50];
                            in.read(bt);
                            String content = new String (bt, "UTF-8" );
                            if (!TextUtils.isEmpty(content)) {
                                RxBus.getInstance().send(new EventBean<>(BluetoothSocketManager.receiveData, content));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Observable.just("success");
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new RequestBaseObserver<String>() {
                    @Override
                    protected void onSuccess(String t) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    public void sendMsg(final String content) {
        Observable.just(content)
                .flatMap((Function<String, ObservableSource<String>>) data -> {
                    try {
                        out.print(data);
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return Observable.just("success");
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new RequestBaseObserver<String>() {
                    @Override
                    protected void onSuccess(String t) {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    public void close(){
        Observable.just("")
                .flatMap((Function<String, ObservableSource<String>>) s -> {
                    try {
                        mSocket.close();
                        if(null != mServerSocket){
                            mServerSocket.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Observable.just("success");
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new RequestBaseObserver<String>() {
                    @Override
                    protected void onSuccess(String t) {
                        mSocket = null;
                        mServerSocket = null;
                        mBluetoothAdapter = null;
                        manager = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    //获取已绑定设备列表
    @SuppressLint("MissingPermission")
    public ArrayList<BluetoothDevice> getBondedDevices() {
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        return new ArrayList<>(devices);
    }

    //搜索周围蓝牙设备，并通过广播返回
    @SuppressLint("MissingPermission")
    public void startDiscovery() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable(); // 打开蓝牙功能
            return;
        }
        mBluetoothAdapter.startDiscovery();
    }
}
