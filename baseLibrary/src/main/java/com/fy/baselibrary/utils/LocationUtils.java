package com.fy.baselibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * description 原生API获取地理位置和经纬度，判断所在国家 工具类
 * Created by fangs on 2022/10/26 17:44.
 */
public class LocationUtils {

    public interface OnLocationListener{
        public void getCountry(String country);
    }

    @SuppressLint("MissingPermission")
    public static void getLocation(OnLocationListener onLocationListener) {
        LocationManager locationManager = (LocationManager) ConfigUtils.getAppCtx().getSystemService(Context.LOCATION_SERVICE);

        if (null == locationManager) return;

        LocationListener location = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) { //GPS信息发送改变时回调

                locationManager.removeUpdates(this);

                Address address = getAddress(location.getLatitude(), location.getLongitude());

                if (null != onLocationListener && null != address) {
                    onLocationListener.getCountry(address.getLocale().getCountry());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {//GPS状态发送改变时回调

            }

            @Override
            public void onProviderEnabled(String provider) { //定位提供者启动时回调

            }

            @Override
            public void onProviderDisabled(String provider) { //定位提供者关闭时回调

            }
        };

        //指定GPS定位提供者
        //指定数据更新的间隔时间
        //位置间隔的距离为1m
        //监听GPS信息是否改变
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, location);
    }

    public static Address getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(ConfigUtils.getAppCtx(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            L.e("LocationUtils", addresses.toString());
//            Address[addressLines=[0:"广东省东莞市健升大厦"],feature=健升大厦,admin=广东省,sub-admin=null,locality=东莞市,thoroughfare=null,postalCode=null,countryCode=CN,countryName=中国,hasLatitude=true,
//                    latitude=23.025354,hasLongitude=true,longitude=113.748738,phone=null,url=null,extras=Bundle[mParcelledData.dataSize=92]]
            if (addresses.size() > 0) {
                return addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
