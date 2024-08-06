package com.fy.baselibrary.utils.media;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.AppUtils;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.os.OSUtils;

import java.io.File;

/**
 * describe：Uri 获取工具类
 * Created by fangs on 2020/1/13 0013 上午 10:34.
 */
public class UriUtils {

    private UriUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取 指定 文件Uri
     * @param file
     */
    public static Uri fileToUri(File file){
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(ConfigUtils.getAppCtx(), AppUtils.getFileProviderName(), file);
        }

        return uri;
    }

    /**
     * 获取资源 Uri
     * @param resId
     */
    public static Uri getResUri(int resId){
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + AppUtils.getLocalPackageName() + "/" + resId);
    }

    /**
     * android 向 sdcard 创建文件或文件夹 兼容方案，返回文件 uri
     *
     * @param contentValues Android 10以后使用 ContentValues 在sdcard 实现 文件相关操作
     *                      ContentValues contentValues = new ContentValues();
     *                      //    设置存储路径 , files 数据表中的对应 relative_path 字段在 MediaStore 中以常量形式定义
     *                      contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/" + ConfigUtils.getFilePath());
     *                      //     设置文件名称 带后缀【如 xxx.png】
     *                      contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
     *                      //     设置文件标题, 一般是删除后缀, 可以不设置
     *                      contentValues.put(MediaStore.Downloads.TITLE, "image");
     *                      //    设置 MIME_TYPE
     *                      contentValues.put(MediaStore.Downloads.MIME_TYPE, "image/jpg");
     *
     * @param uriType       【Audio，Images，Video，Downloads】
     *                      MediaStore.Audio.Media.EXTERNAL_CONTENT_URI：存储在手机外部存储器上的音频文件Uri路径。
     *                      MediaStore.Audio.Media.INTERNAL_CONTENT_URI：存储在手机内部存储器上的音频文件Uri路径。
     *                      MediaStore.Images.Media.EXTERNAL_CONTENT_URI：存储在手机外部存储器上的图片文件Uri路径。
     *                      MediaStore.Images.Media.INTERNAL_CONTENT_URI：存储在手机内部存储器上的图片文件Uri路径。
     *                      MediaStore.Video.Media.EXTERNAL_CONTENT_URI：存储在手机外部存储器上的视频文件Uri路径。
     *                      MediaStore.Video.Media.INTERNAL_CONTENT_URI：存储在手机内部存储器上的视频文件Uri路径。
     *                      MediaStore.Downloads.EXTERNAL_CONTENT_URI： 是Android10版本新增API，用于创建、访问非媒体文件。
     *                      MediaStore.Downloads.INTERNAL_CONTENT_URI： 是Android10版本新增API，用于创建、访问非媒体文件。
     */
    public static Uri createFileUri(ContentValues contentValues, String uriType) {
        // 文件路径
        String path = contentValues.getAsString(MediaStore.Downloads.RELATIVE_PATH);
        if (TextUtils.isEmpty(path)) return null;

        if (OSUtils.isAndroid10()) {
            Uri insert = ConfigUtils.getAppCtx()
                    .getContentResolver()
                    .insert(getUriType(uriType), contentValues);

            if (insert != null) {
                L.e("SuperFileUtils", "文件创建从成功");
            }

            return insert;
        } else {
            String superFolder = "";
            if (FileUtils.isSDCardEnable()) {
                superFolder = FileUtils.getSDCardPath() + path;
            } else {
                superFolder = FileUtils.getFilesDir() + path;
            }

            File file = FileUtils.folderIsExists(superFolder);

            String fileName = contentValues.getAsString(MediaStore.Downloads.DISPLAY_NAME);
            if (!TextUtils.isEmpty(fileName)) {
                file = FileUtils.fileIsExists(superFolder + "/" + fileName);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return Uri.fromFile(file);
            } else {
                return FileProvider.getUriForFile(ConfigUtils.getAppCtx(), AppUtils.getFileProviderName(), file);
            }
        }
    }

//    @SuppressLint("Range")
//    public static Uri queryFileUri(String uriType, ContentValues contentValues) {
//        Uri uri = null;
//        if (OSUtils.isAndroid10()) {
//            Cursor cursor = ConfigUtils.getAppCtx()
//                    .getContentResolver()
//                    .query(getUriType(uriType),
//                            null,
//                            MediaStore.Downloads.RELATIVE_PATH + "=? AND " + MediaStore.Downloads.DISPLAY_NAME + "=?",
//                            new String[]{contentValues.getAsString(MediaStore.Downloads.RELATIVE_PATH), contentValues.getAsString(MediaStore.Downloads.DISPLAY_NAME)},
//                            null);
//
//            while(cursor.moveToNext()) {
//                uri = Uri.parse(cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID)));
//                break;
//            }
//
//            cursor.close();
//
//            return uri;
//        }
//
//        return null;
//    }

    public static boolean updateFileUri(Uri uri, ContentValues contentValues) {
        int count = 0;
        try {
            count = ConfigUtils.getAppCtx()
                    .getContentResolver()
                    .update(uri, contentValues, null, null);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        if (count > 0) {
            L.e("SuperFileUtils", "更新成功");
        }

        return count > 0;
    }

    public static void deleteFileUri(String uriType, String where, String[] selectionArgs) {
        int count = ConfigUtils.getAppCtx()
                .getContentResolver()
                .delete(getUriType(uriType), where, selectionArgs);

        if (count > 0) {
            L.e("SuperFileUtils", "删除成功");
        }
    }

    public static Uri getUriType(String uriType) {
        Uri insertUri = null;
        if (OSUtils.isAndroid10()) {
            if (FileUtils.isSDCardEnable()) {
                if (uriType.equals("Audio")) {
                    insertUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else if (uriType.equals("Images")) {
                    insertUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if (uriType.equals("Video")) {
                    insertUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else {
                    insertUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                }
            } else {
                if (uriType.equals("Audio")) {
                    insertUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                } else if (uriType.equals("Images")) {
                    insertUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
                } else if (uriType.equals("Video")) {
                    insertUri = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                } else {
                    insertUri = MediaStore.Downloads.INTERNAL_CONTENT_URI;
                }
            }
        }

        return insertUri;
    }

    /**
     * 适配api19以上,根据uri获取文件的绝对路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    contentUri = MediaStore.Files.getContentUri("external");
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }


        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.MediaColumns.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


}
