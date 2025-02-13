package com.fy.baselibrary.h5;

import android.app.Activity;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import com.fy.baselibrary.retrofit.observer.IProgressDialog;
import com.fy.baselibrary.utils.GsonUtils;

/**
 * DESCRIPTION：h5 android 交互
 * Created by fangs on 2019/3/27 17:03.
 */
public class BaseAndroidJSInterface {

    protected Activity context;
    protected Fragment fragment;
    protected WebView view;
    private String host;

    private ArrayMap<String, String> defaultParams = new ArrayMap<>();
    private IProgressDialog progressDialog;

    public BaseAndroidJSInterface(Fragment fragment, WebView view, String host) {
        this.fragment = fragment;
        this.view = view;
        this.host = host;
    }

    public BaseAndroidJSInterface(Activity activity, WebView view, String host) {
        this.context = activity;
        this.view = view;
        this.host = host;
    }

    /**
     * 设置 加载弹窗
     *
     * @param progressDialog
     */
    public void setProgressDialog(IProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    /**
     * 设置 请求参数
     *
     * @param key
     * @param value
     * @return
     */
    public BaseAndroidJSInterface setDefaultParams(String key, String value) {
        defaultParams.put(key, value);
        return this;
    }

    /**
     * 获取 指定 key 的参数
     *
     * @param key
     * @param <T>
     */
    public <T> T getDefaultParams(String key) {
        return (T) defaultParams.get(key);
    }

    //解析 H5RequestBean 获取 请求参数
    private ArrayMap<String, Object> getHttpParams(H5RequestBean request) {
        ArrayMap<String, Object> oneParams = new ArrayMap<>();
        ArrayMap<String, Object> params = request.getParams();

        for (String key : defaultParams.keySet()) {
            oneParams.put(key, defaultParams.get(key));
        }

        for (String key : params.keySet()) {
            //params.get(key) 为空 或者 defaultParams map 中存在这个key 则 不添加
            if (null == params.get(key) || defaultParams.containsKey(key)) continue;

            oneParams.put(key, params.get(key));
        }

        return oneParams;
    }

    //添加请求头
    private ArrayMap<String, Object> getHeaderParams(H5RequestBean request) {
        ArrayMap<String, Object> params = request.getHeader();
        if (null == params) {
            params = new ArrayMap<>();
//            params.put("Content-Type", "multipart/form-data;charse=UTF-8");
//            params.put("Connection", "keep-alive");
//            params.put("Accept", "*/*");
//            params.put("app-type", "Android");
        }

        return params;
    }

    /**
     * 定义本地网络请求方法 供 h5 调用
     *
     * @param requestContent h5 传递的 网络请求 请求头，请求方法（get，post），请求参数，请求 url
     * @return ""
     */
    @JavascriptInterface
    public String httpRequest(String requestContent) {
        return httpRequest("", requestContent);
    }

    /**
     * 定义本地网络请求方法 供 h5 调用
     *
     * @param hostIp         请求的主机地址 可为空，为空则表示 使用构造方法传递的 host
     * @param requestContent h5 传递的 网络请求 请求头，请求方法（get，post），请求参数，请求 url
     * @return
     */
    @JavascriptInterface
    public String httpRequest(String hostIp, String requestContent) {
        H5RequestBean request = null;
        try {
            request = GsonUtils.fromJson(requestContent, H5RequestBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "requestContent 格式错误";//返回 json格式 错误信息
        }

        String method = request.getRequestMethod();
        ArrayMap<String, Object> headers = getHeaderParams(request);
        ArrayMap<String, Object> params = getHttpParams(request);

        String hostAddress = !TextUtils.isEmpty(hostIp) ? hostIp : this.host;

        switch (method.toUpperCase()) {
            case "GET":
//                httpGet(headers, params, hostAddress + request.getUrl(), request.getJsMethod());
                break;
            case "POST":
//                httpPost(headers, params, hostAddress + request.getUrl(), request.getJsMethod());
                break;
            case "POSTJSON":
//                httpPostJson(headers, params, hostAddress + request.getUrl(), request.getJsMethod());
                break;
            case "UPLOAD":
//                httpUpload(headers, params, hostAddress + request.getUrl(), request.getJsMethod(), request.getBase64());
                break;
        }

        return "";
    }



    private OnOwnOptListener listener;

    public void setOnOwnOptListener(OnOwnOptListener listener) {
        this.listener = listener;
    }

    public interface OnOwnOptListener {
        String beforH5(String url, String data, boolean isError);
    }


    @JavascriptInterface
    public void back() {
        Activity act = null == context ? fragment.getActivity() : context;
        assert act != null;
        act.finish();
    }

    @JavascriptInterface
    public void webViewback() {
        if (this.view != null && this.view.canGoBack()) {
            this.view.goBack();
        } else {
            back();
        }
    }

}
