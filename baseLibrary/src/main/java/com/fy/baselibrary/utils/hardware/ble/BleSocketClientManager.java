//package com.fy.baselibrary.utils.hardware.ble;
//
//import android.bluetooth.BluetoothDevice;
//
//import com.fy.baselibrary.retrofit.ServerException;
//import com.fy.baselibrary.retrofit.observer.RequestBaseObserver;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableSource;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * description TODO
// * Created by fangs on 2023/5/22 16:26.
// */
//public class BleSocketClientManager extends BleSocketManager {
//
//    private volatile static BleSocketClientManager manager;
//    protected String remoteAddress;
//
//
//    private BleSocketClientManager() {
//        super();
//    }
//
//    @Override
//    public BleSocketClientManager setBleSocketListener(BleSocketListener bleSocketListener) {
//        this.bleSocketListener = bleSocketListener;
//        return this;
//    }
//
//    @Override
//    public void close() {
//        super.close();
//        manager = null;
//    }
//
//    public static BleSocketClientManager getInstance() {
//        if (manager == null) {
//            synchronized (BleSocketClientManager.class) {
//                if (manager == null) {
//                    manager = new BleSocketClientManager();
//                }
//            }
//        }
//        return manager;
//    }
//
//    public BleSocketClientManager setRemoteAddress(String remoteAddress) {
//        this.remoteAddress = remoteAddress;
//        return this;
//    }
//
//    public void initClient() {
//        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(remoteAddress);
//        try {
//            mSocket = remoteDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUIDString));
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }
//
//        bleConn();
//    }
//
//    // 连接 BluetoothSocket
//    public void bleConn(){
//        Observable.just("")
//                .flatMap((Function<String, ObservableSource<String>>) data -> {
//                    try {
//                        if (!mSocket.isConnected()) {
//                            mSocket.connect();  // 会阻塞线程
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        mSocket.close();
//                        mSocket = null;
//                    }
//
//                    //等待一定时间
//                    return Observable.timer(3, TimeUnit.SECONDS)
//                            .flatMap((Function<Long, ObservableSource<String>>) aLong -> {
//                                if(null == mSocket){
//                                    return Observable.error(new ServerException("conn fail", -13));
//                                }
//
//                                in = mSocket.getInputStream();
//                                out = new PrintWriter(mSocket.getOutputStream());
//
//                                return Observable.just("conn success");
//                            });
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new RequestBaseObserver<String>() {
//                    @Override
//                    protected void onSuccess(String msg) {
//                        if(null != bleSocketListener) bleSocketListener.onConnSuccess();
//
//                        receiveData();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if(e instanceof ServerException){
//                            ServerException exception = (ServerException) e;
//                            if(exception.code == -13){
//                                if(null != bleSocketListener) {
//                                    bleSocketListener.onFailure(exception);
//                                    bleSocketListener.onClosed();
//                                }
//                            }
//
//                        } else {
//                            super.onError(e);
//                        }
//
//                        close();
//                    }
//                });
//    }
//
//}
