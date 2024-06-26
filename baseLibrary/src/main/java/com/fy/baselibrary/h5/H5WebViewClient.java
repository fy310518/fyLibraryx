package com.fy.baselibrary.h5;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.statuslayout.OnSetStatusView;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.imgload.ImgLoadUtils;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * describe：默认的 WebViewClient
 * Created by fangs on 2020/1/9 0009 下午 14:28.
 */
public abstract class H5WebViewClient extends WebViewClient {

    public static String blank = "about:blank";
    private boolean isUseLocalIntercept;//使用
    private OnSetStatusView onSetStatusView;

    public H5WebViewClient(OnSetStatusView onSetStatusView) {
        this.onSetStatusView = onSetStatusView;
    }

    public H5WebViewClient(OnSetStatusView onSetStatusView, boolean isUseLocalIntercept) {
        this.onSetStatusView = onSetStatusView;
        this.isUseLocalIntercept = isUseLocalIntercept;
    }

    //在开始加载网页时会回调
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        view.getSettings().setBlockNetworkImage(true);
        setTips(Constant.LAYOUT_CONTENT_ID);
    }

    //加载完成的时候会回调
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
//        view.clearHistory();//重写 onPageFinished 清空【当前】页面之前的所有记录

        view.getSettings().setBlockNetworkImage(false);
        if (!view.getSettings().getLoadsImagesAutomatically()) {
            //设置wenView加载图片资源
            view.getSettings().setBlockNetworkImage(false);
            view.getSettings().setLoadsImagesAutomatically(true);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(ResUtils.getStr(R.string.sslAuthenticationFail));
        builder.setPositiveButton(ResUtils.getStr(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed(); // 接受https所有网站的证书
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(ResUtils.getStr(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    //当加载的网页需要重定向的时候就会回调这个函数告知我们应用程序是否需要接管控制网页加载，如果应用程序接管，
    //并且return true意味着主程序接管网页加载，如果返回false让webview自己处理。
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        if (!(url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://"))) {
            Intent intent = new Intent("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            try {
                webView.getContext().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        webView.loadUrl(url);
        return false;
    }

    @Nullable
    @Override//webView 请求 拦截方法【下同】
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @Nullable
    @Override//此 API 21后 过时
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse webResourceResponse;

        if (isUseLocalIntercept) return super.shouldInterceptRequest(view, url);

        if (isImgUrl(url)){//1、如果是图片
            webResourceResponse = getImgWebResResponse(view, url);
            if (null == webResourceResponse) webResourceResponse = super.shouldInterceptRequest(view, url);
            return webResourceResponse;
        } else if (isWebResUrl(url)){
            webResourceResponse = getFileWebResResponse(url);
            if (null == webResourceResponse) webResourceResponse = super.shouldInterceptRequest(view, url);
            return webResourceResponse;
        } else {
            return super.shouldInterceptRequest(view, url);
        }
    }

    //加载错误的时候会回调
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return;
        }

        setTips(Constant.LAYOUT_ERROR_ID);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (request.isForMainFrame()){
            setTips(Constant.LAYOUT_ERROR_ID);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)// 这个方法在6.0才出现
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
//        view.loadUrl(blank);// 避免出现默认的错误界面
        int statusCode = errorResponse.getStatusCode();
        if (request.getUrl().toString().toLowerCase().endsWith("favicon.ico")) return;//说明网页没有配置 网页 图标
        if (404 == statusCode || 500 == statusCode) {
            setTips(Constant.LAYOUT_NETWORK_ERROR_ID);
        }
    }

    protected void setTips(int status){
        if (null != onSetStatusView) {
            onSetStatusView.showHideViewFlag(status);
        }
    }




    //判断是否是 图片
    private boolean isImgUrl(String url){
        if (TextUtils.isEmpty(url)) return false;

        url = url.toLowerCase();
        if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".gif")  || url.endsWith(".svg")) return true;

        return false;
    }

    //判断是否是 js css html 字体 文件
    private boolean isWebResUrl(String url){
        if (TextUtils.isEmpty(url)) return false;

        url = url.toLowerCase();
        if (url.endsWith("js") || url.endsWith(".css") || url.endsWith(".woff")
                || url.contains(".js?") || url.contains(".css?") || url.contains(".woff?")
                || url.endsWith(".html") || url.endsWith(".htm")
                || url.contains(".html?") || url.contains(".htm?")) return true;

        return false;
    }

//    new WebResourceResponse("image/png","UTF-8",new FileInputStream(imgFile)) 第一个参数对应的如下：
//    js:mimeType ="application/x-javascript";
//    css:mimeType ="text/css";
//    html:mimeType ="text/html";
//    jpg/png: mimeType = "image/png";
//    woff: application/octet-stream
    private WebResourceResponse getImgWebResResponse(WebView view, String url){
        WebResourceResponse webResourceResponse = null;
        File imgFile = null;
        try {
            imgFile = ImgLoadUtils.getImgCachePath(view.getContext(), url, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != imgFile) {
                try {
                    L.e("H5 图片地址", imgFile.getPath() + "------");
                    webResourceResponse = new WebResourceResponse("image/png", "UTF-8", new FileInputStream(imgFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return webResourceResponse;
    }

    private WebResourceResponse getFileWebResResponse(String url){
        WebResourceResponse webResourceResponse = null;

        final String filePath = FileUtils.folderIsExists(FileUtils.DOWN, ConfigUtils.getType()).getPath();

        File targetFile = FileUtils.getFile(url, filePath);
        if (targetFile.exists()) {
            try {
                String mimeType = new URL(url).openConnection().getContentType();
                L.e("H5 文件地址", targetFile.getPath() + "------");
                webResourceResponse = new WebResourceResponse(mimeType, "UTF-8", new FileInputStream(targetFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            cacheH5Res(url);
        }

        return webResourceResponse;
    }

    /**
     * 缓存 需要下载的 网页资源【如：js文件，css文件 】
     */
    protected abstract void cacheH5Res(String url);

}
