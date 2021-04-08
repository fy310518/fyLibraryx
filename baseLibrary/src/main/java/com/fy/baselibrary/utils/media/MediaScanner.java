package com.fy.baselibrary.utils.media;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;

/**
 * 通知 系统媒体库更新数据
 * Created by fangs on 2018/1/15.
 */
public class MediaScanner {

    private MediaScannerConnection mConn = null;
    private ScannerClient mClient = null;
    private File mFile = null;
    private String mMimeType = null;
    private OnMediaScannerCompleted scannerCompleted = null;

    public MediaScanner(Context context) {
        if (mClient == null) {
            mClient = new ScannerClient();
        }
        if (mConn == null) {
            mConn = new MediaScannerConnection(context, mClient);
        }
    }

    public void scanFile(File file, String mimeType, OnMediaScannerCompleted scannerCompleted) {
        mFile = file;
        mMimeType = mimeType;
        this.scannerCompleted = scannerCompleted;
        mConn.connect();
    }

    class ScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {
        @Override
        public void onMediaScannerConnected() {
            if (mFile == null) {
                return;
            }
            scan(mFile, mMimeType);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mConn.disconnect();
            if (null != scannerCompleted) {
                RequestUtils.runUiThread(() -> scannerCompleted.onScanCompleted());
            }
            L.e("MediaScanner", "扫描完成：" + path);
        }


        private void scan(File file, String type) {
            if (file.isFile()) {
                mConn.scanFile(file.getAbsolutePath(), null);
                return;
            }
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : file.listFiles()) {
                scan(f, type);
            }
        }
    }

    //定义一个 扫描回调接口
    public interface OnMediaScannerCompleted {
        //此回调已在 UI线程
        void onScanCompleted();
    }
}
