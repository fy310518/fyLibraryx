//package com.fy.baselibrary.utils.hardware.ble;
//
//import android.annotation.SuppressLint;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import android.text.TextUtils;
//
//import androidx.annotation.NonNull;
//
//import com.fy.baselibrary.retrofit.ServerException;
//import com.fy.baselibrary.retrofit.observer.RequestBaseObserver;
//import com.fy.baselibrary.utils.TransfmtUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Set;
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
// * Created by fangs on 2023/5/22 16:27.
// */
//public abstract class BleSocketManager {
//
//    public static final String UUIDString = "00001101-0000-1000-8000-00805F9B34FB";
//    public static final String receiveData = "receive_data";
//
//    protected BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//    protected BleSocketListener bleSocketListener;
//
//    protected BluetoothSocket mSocket;
//    protected InputStream in;
//    protected PrintWriter out;
//
//    protected BluetoothServerSocket mServerSocket;
//
//    protected BleSocketManager() {
//    }
//
//    public abstract BleSocketManager setBleSocketListener(BleSocketListener bleSocketListener);
//
//    byte[] buffer = new byte[1024];
//    int byteRead = -1;
//    // 接收数据
//    public void receiveData(){
//        Observable.interval(0, 300, TimeUnit.MILLISECONDS)
//                .flatMap((Function<Long, ObservableSource<String>>) aLong -> {
//                    try {
//                        if (mSocket.isConnected() && null != in) {
//                            byteRead = in.read(buffer);
//                            if (byteRead > 0 && byteRead <= buffer.length) {
//                                return Observable.just(new String(buffer, 0, byteRead, "UTF-8"));
//                            } else {
//                                return Observable.just("");
//                            }
//                        } else {
//                            return Observable.error(new ServerException("conn closed", -14));
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        return Observable.error(new ServerException("conn closed", -14));
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new RequestBaseObserver<String>() {
//                    @Override
//                    protected void onSuccess(String data) {
//                        if(!TextUtils.isEmpty(data)){
//                            if(null != bleSocketListener) bleSocketListener.onMessage(data);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if(e instanceof ServerException){
//                            ServerException exception = (ServerException) e;
//                            if(exception.code == -14){
//                                if(null != bleSocketListener) bleSocketListener.onClosed();
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
//    // 发送数据
//    public void sendMsg(@NonNull final byte[] content){
//        Observable.just(content)
//                .flatMap((Function<byte[], ObservableSource<String>>) bytes -> Observable.just(TransfmtUtils.ByteArrayToHexString(bytes)))
//                .subscribe(new RequestBaseObserver<String>(){
//                    @Override
//                    protected void onSuccess(String data) {
//                        sendMsg(data);
//                    }
//                });
//    }
//
//    // 发送数据
//    public void sendMsg(@NonNull final String content) {
//        if(TextUtils.isEmpty(content)) return;
//
//        Observable.just(content)
//                .flatMap((Function<String, ObservableSource<String>>) data -> {
//                    try {
//                        if (mSocket.isConnected() && null != out) {
//                            out.print(data);
//                            out.flush();
//
//                            return Observable.just("sendMsg success");
//                        } else {
//                            return Observable.error(new ServerException("conn closed", -14));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        return Observable.error(new ServerException("conn closed", -14));
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .subscribe(new RequestBaseObserver<String>() {
//                    @Override
//                    protected void onSuccess(String t) {
//                    }
//                    @Override
//                    public void onError(Throwable e) {
//                        if(e instanceof ServerException){
//                            ServerException exception = (ServerException) e;
//                            if(exception.code == -14){
//                                close();
//                                if(null != bleSocketListener) bleSocketListener.onClosed();
//                            }
//
//                        } else {
//                            super.onError(e);
//                        }
//                    }
//                });
//    }
//
//    /**
//     * 关闭
//     */
//    @SuppressLint("MissingPermission")
//    public void close() {
//        try {
//            if (null != mSocket) mSocket.close();
//        } catch (IOException e) {
//        }
//        try {
//            if (null != mServerSocket) mServerSocket.close();
//        } catch (IOException e) {
//        }
//
//
//        try {
//            if (in != null) in.close();
//        } catch (IOException e) {
//        }
//        try {
//            if (out != null) out.close();
//        } catch (Exception e) {
//        }
//
//        if (mBluetoothAdapter.isDiscovering()){
//            mBluetoothAdapter.cancelDiscovery();
//        }
//
//        in = null;
//        out = null;
//
//        mSocket = null;
//        mServerSocket = null;
//        mBluetoothAdapter = null;
//    }
//
//
//
//
//}
