package com.fy.baselibrary.utils.hardware;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.fy.baselibrary.application.ioc.ConfigUtils;

import java.util.ArrayList;
import java.util.Set;

/**
 * 低功耗蓝牙工具类
 * Created by fangs on 2017/4/17.
 */
public class BleUtils {

    private BleUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取本机的 蓝牙adapter
     * @return bleAdapter
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothAdapter getBleAdapter(){
        final BluetoothManager bluetoothManager = (BluetoothManager) ConfigUtils.getAppCtx().getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getAdapter();
    }

    /**
     * 判断是否支持蓝牙4.0
     * @return true/false
     */
    public static boolean isHaveBluetooth(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 判断蓝牙是否打开
     * @return true表示已经开启
     */
    @SuppressLint("MissingPermission")
    public static boolean isBluetoothOpen(Context context) {

        BluetoothAdapter bluetoothAdapter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothAdapter = getBleAdapter();
        }

        return !(bluetoothAdapter == null || !bluetoothAdapter.isEnabled());
    }

    /**
     * 强制开启当前 Android 设备的 Bluetooth
     * @return true：强制打开 Bluetooth　成功　false：强制打开 Bluetooth 失败
     */
    @SuppressLint("MissingPermission")
    public static boolean turnOnBluetooth() {

        BluetoothAdapter bluetoothAdapter = getBleAdapter();

        if (null != bluetoothAdapter) {
            return bluetoothAdapter.enable();
        }

        return false;
    }

    /**
     * 强制关闭当前 Android 设备的 Bluetooth
     * @return  true：强制关闭 Bluetooth　成功　false：强制关闭 Bluetooth 失败
     */
    @SuppressLint("MissingPermission")
    public static boolean turnOffBluetooth() {

        BluetoothAdapter bluetoothAdapter = getBleAdapter();
        if (null != bluetoothAdapter) {
            return bluetoothAdapter.disable();
        }

        return false;
    }

    /**
     * 判断蓝牙是否打开，如果打开就重启蓝牙
     */
    public static void reStartBluetooth() {
        //先关闭手机蓝牙
        turnOffBluetooth();

//        //延迟1S 打开手机蓝牙
//        Observable.timer(1, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(@NonNull Long aLong) throws Exception {
//                        turnOnBluetooth();
//                    }
//                });
    }

    /**
     * 当前设备的 蓝牙 名称和mac地址
     * @return
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getBleAddress(){
        return getBleAdapter().getName() + "\n" + getBleAdapter().getAddress();
    }

    /**
     * 获取已绑定蓝牙设备列表
     */
    @SuppressLint("MissingPermission")
    public static ArrayList<BluetoothDevice> getBondedDevices() {
        Set<BluetoothDevice> devices = getBleAdapter().getBondedDevices();
        return new ArrayList<>(devices);
    }

    //搜索周围蓝牙设备，并通过广播返回
    @SuppressLint("MissingPermission")
    public static void startDiscovery() {
        BluetoothAdapter bluetoothAdapter = getBleAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable(); // 打开蓝牙功能
            return;
        }
        bluetoothAdapter.startDiscovery();

//        bluetoothAdapter.getBluetoothLeScanner();
    }

    /**
     * 注册 扫描 蓝牙设备，接收广播
     * @param broadcastReceiver
     */
    public static void registerScanBleReceiver(BroadcastReceiver broadcastReceiver){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        ConfigUtils.getAppCtx().registerReceiver(broadcastReceiver, filter);
    }
}
