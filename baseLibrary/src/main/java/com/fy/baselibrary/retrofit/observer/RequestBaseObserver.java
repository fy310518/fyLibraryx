package com.fy.baselibrary.retrofit.observer;

import androidx.annotation.StringRes;

import com.fy.baselibrary.R;
import com.fy.baselibrary.base.dialog.CommonDialog;
import com.fy.baselibrary.retrofit.ServerException;
import com.fy.baselibrary.statuslayout.OnSetStatusView;
import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.net.NetUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.notify.T;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.net.ssl.SSLException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 自定义 基础功能 Observer
 * Created by fangs on 2017/8/28.
 */
public abstract class RequestBaseObserver<V> implements Observer<V> {

    private Disposable disposed;
    private IProgressDialog progressDialog;
    private CommonDialog dialog;

    /** 传递进来的 环境（AppCompatActivity or v4.app.Fragment） */
    protected Object context;

    public RequestBaseObserver() {}

    /**
     * 观察者：带加载对话框的 构造方法
     * @param pDialog
     */
    public RequestBaseObserver(IProgressDialog pDialog) {
        if (null != pDialog) {
            this.context = pDialog.obj;
            init(pDialog);
        }
    }

    public RequestBaseObserver(Object context) {
        this.context = context;
    }

    private void init(IProgressDialog pDialog) {
        this.progressDialog = pDialog;

        dialog = progressDialog.getDialog();
        if (null != dialog) {
            dialog.setDialogList(() -> {
                if (null != disposed && !disposed.isDisposed()) disposed.dispose();
            });
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        L.e("net", "onSubscribe()");

        this.disposed = d;
        if (null != progressDialog && null != disposed && !disposed.isDisposed()) {
            progressDialog.show();
        }
    }

    @Override
    public void onNext(V t) {
        L.e("net", "onNext()");
        onSuccess(t);
        updateLayout(Constant.LAYOUT_CONTENT_ID);
    }

    @Override
    public void onError(Throwable e) {
        L.e("net", "onError()" + e.getLocalizedMessage());
        dismissProgress();
        onFail();

        if (NetUtils.getNetworkType().equals(NetUtils.NetworkType.NETWORK_NO)){
            actionResponseError(R.string.networkUnavailable);
            updateLayout(Constant.LAYOUT_NETWORK_ERROR_ID);
        } else if (!NetUtils.isConnected()) {
            actionResponseError(R.string.networkUnavailable);
            updateLayout(Constant.LAYOUT_NETWORK_ERROR_ID);

        } else if (e instanceof SocketTimeoutException) {
            actionResponseError(R.string.responseTimeout);
            updateLayout(Constant.LAYOUT_NETWORK_ERROR_ID);
        } else if (e instanceof ConnectException) {
            actionResponseError(R.string.connectionException);
            updateLayout(Constant.LAYOUT_NETWORK_ERROR_ID);
        } else if (e instanceof ConnectTimeoutException) {
            actionResponseError(R.string.netConnTimeout);
            updateLayout(Constant.LAYOUT_NETWORK_ERROR_ID);
        } else if (e instanceof UnknownHostException) {
            actionResponseError(R.string.DomainError);
            updateLayout(Constant.LAYOUT_NETWORK_ERROR_ID);
        } else if (e instanceof SSLException) {
            actionResponseError(R.string.certificateValidationFailed);
            updateLayout(Constant.LAYOUT_ERROR_ID);
        } else if (e instanceof ClassCastException) {
            actionResponseError(R.string.typeConversionError);
            updateLayout(Constant.LAYOUT_ERROR_ID);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSyntaxException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            actionResponseError(R.string.dataParsingError);
        } else if (e instanceof ServerException){
            actionResponseError(e.getMessage());
            updateLayout(((ServerException) e).code);
        } else {
            actionResponseError(R.string.requestFail);
        }
    }

    @Override
    public void onComplete() {
        L.e("net", "onComplete()");
        dismissProgress();
    }

    /**
     * 显示提示信息
     * @param msg 内容
     */
    protected void actionResponseError(String msg) {
        T.show(msg, -1);
    }
    protected void actionResponseError(@StringRes int msg) {
        T.show(msg, -1);
    }

    /**
     * 取消进度框
     */
    protected void dismissProgress() {
        if (null != progressDialog) {
            progressDialog.close();
        }
    }


    /**
     * 请求成功 回调
     *
     * @param t 请求返回的数据
     */
    protected abstract void onSuccess(V t);

    /**
     * 请求失败
     */
    protected void onFail(){}

    /**
     * 更新activity 界面（多状态视图）
     * 可根据flag 判断请求失败
     * @param flag 请求状态flag
     */
    protected void updateLayout(int flag){
        if (context instanceof OnSetStatusView) {
            ((OnSetStatusView)context).showHideViewFlag(flag);
        }
    }

}
