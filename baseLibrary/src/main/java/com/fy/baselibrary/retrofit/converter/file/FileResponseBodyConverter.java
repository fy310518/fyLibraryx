package com.fy.baselibrary.retrofit.converter.file;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.retrofit.load.LoadOnSubscribe;
import com.fy.baselibrary.retrofit.load.down.FileResponseBody;
import com.fy.baselibrary.utils.AppUtils;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.cache.SpfAgent;
import com.fy.baselibrary.utils.media.UriUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * describe: 文件下载 转换器
 * Created by fangs on 2019/8/28 22:03.
 */
public class FileResponseBodyConverter implements Converter<ResponseBody, File> {

    private static final long CALL_BACK_LENGTH = 1024 * 1024;
    public static final ArrayMap<String, LoadOnSubscribe> LISTENER_MAP = new ArrayMap<>();
    public static final ArrayMap<String, String> targetPath_MAP = new ArrayMap<>();
    public static final ArrayMap<String, String> reName_MAP = new ArrayMap<>();

    //添加 进度发射器
    public static void addListener(String url, String targetFilePath, String reName, LoadOnSubscribe loadOnSubscribe) {
        if (null != loadOnSubscribe) {
            LISTENER_MAP.put(url, loadOnSubscribe);
        }
        if (!TextUtils.isEmpty(targetFilePath)){
            targetPath_MAP.put(url, targetFilePath);
        }
        if (!TextUtils.isEmpty(reName)){
            reName_MAP.put(url, reName);
        }
    }

    //取消注册下载监听
    public static void removeListener(String url) {
        if (!TextUtils.isEmpty(url)) {
            LISTENER_MAP.remove(url);
            targetPath_MAP.remove(url);
            reName_MAP.remove(url);
        }
    }

    @Override
    public File convert(ResponseBody responseBody) throws IOException {
        String requestUrl = null;
        try {
            //使用反射获得我们自定义的response
            Class aClass = responseBody.getClass();
            Field field = aClass.getDeclaredField("delegate");
            field.setAccessible(true);
            ResponseBody body = (ResponseBody) field.get(responseBody);
            if (body instanceof FileResponseBody) {
                FileResponseBody fileResponseBody = (FileResponseBody) body;
                requestUrl = fileResponseBody.getRequestUrl();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        String filePath = targetPath_MAP.get(requestUrl);
        if(TextUtils.isEmpty(filePath)){
            filePath = FileUtils.folderIsExists(FileUtils.DOWN, 0).getPath();
        }

        return saveFile(LISTENER_MAP.get(requestUrl), responseBody, requestUrl, filePath);
    }


    /**
     * 根据ResponseBody 写文件
     *
     * @param responseBody
     * @param url
     * @param filePath     文件保存路径
     * @return
     */
    public static File saveFile(LoadOnSubscribe loadOnSubscribe, final ResponseBody responseBody, String url, final String filePath) {
        File tempFile = FileUtils.getTempFile(url, filePath);

        Uri uri = null;
        if(!filePath.contains(AppUtils.getLocalPackageName())) { // 下载到 sd卡 Environment.DIRECTORY_DOWNLOADS 目录
//            UriUtils.deleteFileUri("Downloads", Environment.DIRECTORY_DOWNLOADS + "/" + ConfigUtils.getFilePath(), reName_MAP.get(url));

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + ConfigUtils.getFilePath());
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, tempFile.getName());
            uri = UriUtils.createFileUri(contentValues, "Downloads");
        } else {
            tempFile = FileUtils.fileIsExists(tempFile.getPath());
        }


        File file = null;
        try {
            file = writeFileToDisk(loadOnSubscribe, responseBody, tempFile.getAbsolutePath(), uri);

            int FileDownStatus = SpfAgent.init("").getInt(file.getName() + Constant.FileDownStatus);

            boolean renameSuccess;
            if (FileDownStatus == 4) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + ConfigUtils.getFilePath());

                if(reName_MAP.containsKey(url)){
                    if(null != uri){
                        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, reName_MAP.containsKey(url));
                        renameSuccess = UriUtils.updateFileUri(uri, contentValues);
                    } else {
                        renameSuccess = tempFile.renameTo(new File(tempFile.getParent(), reName_MAP.get(url)));
                    }
                } else {
                    if(null != uri){
                        String fileName = FileUtils.getFileName(url);
                        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                        renameSuccess = UriUtils.updateFileUri(uri, contentValues);
                    } else {
                        renameSuccess = FileUtils.reNameFile(url, tempFile.getPath());
                    }
                }

                if(renameSuccess){
                    return FileUtils.getFile(url, filePath);
                } else {
                    return tempFile;
                }
            } else if (FileDownStatus == 3) {//取消下载则 删除下载内容
                FileUtils.deleteFileSafely(tempFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * 单线程 断点下载
     *
     * @param loadOnSubscribe
     * @param responseBody
     * @param filePath
     * @return
     * @throws IOException
     */
    @SuppressLint("DefaultLocale")
    public static File writeFileToDisk(LoadOnSubscribe loadOnSubscribe, ResponseBody responseBody, String filePath, Uri uri) {
        long totalByte = responseBody.contentLength();
        L.e("fy_file_FileDownInterceptor", "文件下载 写数据" + "---" + Thread.currentThread().getName());

        File file = new File(filePath);
        if (null != loadOnSubscribe) {
            loadOnSubscribe.setmSumLength(file.length() + totalByte);
            loadOnSubscribe.onRead(file.length());
        }


        SpfAgent.init("").saveInt(file.getName() + Constant.FileDownStatus, 1).commit(false);//正在下载
        byte[] buffer = new byte[1024 * 4];

        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        OutputStream out = null;

        try {
            is = responseBody.byteStream();

            if(null != uri){
                ContentResolver resolver = ConfigUtils.getAppCtx().getContentResolver();
                out = resolver.openOutputStream(uri);

            } else {
                long tempFileLen = file.length();
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.seek(tempFileLen);
            }

            long downloadByte = 0;
            while (true) {
                int len = is.read(buffer);
                if (len == -1) {//下载完成
                    if (null != loadOnSubscribe) loadOnSubscribe.clean();

                    SpfAgent.init("").saveInt(file.getName() + Constant.FileDownStatus, 4).commit(false);//下载完成
                    break;
                }

                int FileDownStatus = SpfAgent.init("").getInt(file.getName() + Constant.FileDownStatus);
                if (FileDownStatus == 2 || FileDownStatus == 3) break;//暂停或者取消 停止下载

                if(null != randomAccessFile){
                    randomAccessFile.write(buffer, 0, len);
                } else {
                    out.write(buffer, 0, len);
                }

                downloadByte += len;

                if (null != loadOnSubscribe && downloadByte >= CALL_BACK_LENGTH) {//避免每写4096字节，就回调一次，那未免太奢侈了，所以设定一个常量每1mb回调一次
                    loadOnSubscribe.onRead(len);
                    downloadByte = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileUtils.closeIO(out, is, responseBody);

        return file;
    }

}
