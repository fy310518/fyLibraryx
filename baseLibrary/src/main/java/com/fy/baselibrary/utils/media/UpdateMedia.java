package com.fy.baselibrary.utils.media;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.fy.baselibrary.application.ioc.ConfigUtils;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * 更新媒体库 （4.4之后 使用）
 * Created by fangs on 2018/1/31.
 */
public class UpdateMedia {

    public static final String TAG = "UpdateMedia";

    /**
     * 通知系统媒体库更新 (使用此 广播方式)
     * @param context
     * @param action    扫描的类型 文件或目录（文件：Intent.ACTION_MEDIA_SCANNER_SCAN_FILE）
     * @param file      要扫描的文件
     */
    public static void scanFolder(Context context, String action, File file) {
        Intent mediaScanIntent = new Intent(action);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 通知系统媒体库更新
     * @param context
     * @param filePath      要扫描的文件或目录
     */
    public static void scanMedia(Context context, String filePath, MediaScanner.OnMediaScannerCompleted scannerCompleted){
        File file = new File(filePath);
        if (!file.exists()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            insertImageFileIntoMediaStore(file);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            new MediaScanner(context).scanFile(file, null, scannerCompleted);
        } else {
            String action = Intent.ACTION_MEDIA_MOUNTED;//扫描的类型 文件或目录
            scanFolder(context, action, file);
        }
    }

    /**
     * AndroidQ以上保存图片到公共目录
     * @param file 图片
     */
    private static Uri insertImageFileIntoMediaStore (File file) {
        String imageName = file.getName();

        ContentResolver resolver = ConfigUtils.getAppCtx().getContentResolver();

        //设置文件参数到ContentValues中
        ContentValues values = new ContentValues();
        //设置文件名
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        //设置文件描述，这里以文件名代替
        values.put(MediaStore.Images.Media.DESCRIPTION, imageName);

        //设置文件类型为image/*
        String mimeType = getMimeType(imageName);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);

        //注意：MediaStore.Images.Media.RELATIVE_PATH 需要targetSdkVersion=29,
        //故该方法只可在Android10的手机上执行；Environment.DIRECTORY_DCIM  DIRECTORY_PICTURES、DIRECTORY_MOVIES、DIRECTORY_MUSIC等，
        // 分别表示相册、图片、电影、音乐等目录
        String RELATIVE_PATH = mimeType.startsWith("image") ? Environment.DIRECTORY_DCIM : mimeType.startsWith("audio") ? Environment.DIRECTORY_MUSIC : Environment.DIRECTORY_MOVIES;
        values.put(MediaStore.Images.Media.RELATIVE_PATH, RELATIVE_PATH);
        //EXTERNAL_CONTENT_URI代表外部存储器
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //insertUri表示文件保存的uri路径
        return resolver.insert(external, values);
    }

    /**
     *根据文件名 获取 mimeType
     * @param fileName
     */
    public static String getMimeType(String fileName){
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(fileName);
    }

}
