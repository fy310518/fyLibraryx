//package com.fy.baselibrary.utils.hardware.ble;
//
//import android.annotation.SuppressLint;
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
//import io.reactivex.functions.Predicate;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * description TODO
// * Created by fangs on 2023/5/22 17:39.
// */
//public class BleSocketServerManager extends BleSocketManager {
//
//    public static final int STATE_NONE = 0; // we're doing nothing
//    public static final int STATE_LISTEN = 1; // now listening for incoming
//    // connections
//    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
//    // connection
//    public static final int STATE_CONNECTED = 3; // now connected to a remote
//
//    private int mState; // 当前连接状态
//
//
//    private volatile static BleSocketServerManager manager;
//
//    private BleSocketServerManager() {
//        super();
//    }
//
//    public static BleSocketServerManager getInstance() {
//        if (manager == null) {
//            synchronized (BleSocketServerManager.class) {
//                if (manager == null) {
//                    manager = new BleSocketServerManager();
//                }
//            }
//        }
//        return manager;
//    }
//
//    @SuppressLint("MissingPermission")
//    public void initServer() {
//        try {
//            mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("server_name", UUID.fromString(UUIDString));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        acceptConn();
//    }
//
//
//    public void acceptConn(){
//        Observable.just("")
//                .flatMap((Function<String, ObservableSource<String>>) data -> {
//                    try {
//                        if(null != mServerSocket){
//                            mSocket = mServerSocket.accept(); // 会阻塞线程
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        if(null != mSocket) mSocket.close();
//                        mSocket = null;
//                    }
//
//                    //等待一定时间
//                    return Observable.interval(0, 500, TimeUnit.MILLISECONDS)
//                            .subscribeOn(Schedulers.io())
//                            .takeUntil(new Predicate<Long>(){
//                                @Override
//                                public boolean test(Long aLong) throws Exception {
//                                    return null != mSocket;
//                                }
//                            })
//                            .flatMap((Function<Long, ObservableSource<String>>) aLong -> {
//                                if(null != mSocket){
////                                    mSocket.getRemoteDevice();
//                                    in = mSocket.getInputStream();
//                                    out = new PrintWriter(mSocket.getOutputStream());
//
//                                    return Observable.just("conn success");
//                                }
//
//                                return Observable.error(new ServerException("no conn", -15));
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
//                                close();
//                                if(null != bleSocketListener) bleSocketListener.onFailure(exception);
//                            }
//
//                        } else {
//                            super.onError(e);
//                        }
//                    }
//                });
//    }
//
//    @Override
//    public BleSocketServerManager setBleSocketListener(BleSocketListener bleSocketListener) {
//        this.bleSocketListener = bleSocketListener;
//        return this;
//    }
//
//    @Override
//    public void close() {
//        super.close();
//        manager = null;
//    }
//}
