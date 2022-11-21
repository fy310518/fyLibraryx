package com.fy.baselibrary.retrofit.observer;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fy.baselibrary.base.dialog.CommonDialog;
import com.fy.baselibrary.retrofit.NetAnimListener;

/**
 * 自定义对话框的dialog
 * Created by fangs on 2017/11/7.
 */
public class IProgressDialog {

    protected Context mContext;

    /** 传递进来的 环境（AppCompatActivity or v4.app.Fragment） */
    protected Object obj;
    protected CommonDialog dialog;

    protected NetAnimListener netAnimListener;


    /**
     * 显示对话框
     */
    public void show() {
        if (null != obj){
            if (obj instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) obj;
                mContext = activity;
                if (null != dialog && (null == dialog.getDialog() || !dialog.getDialog().isShowing())) dialog.show(activity.getSupportFragmentManager(), dialog.getClass().getName());
            } else if (obj instanceof Fragment) {
                Fragment fragment = (Fragment) obj;
                mContext = fragment.getContext();
                if (null != dialog && (null == dialog.getDialog() || !dialog.getDialog().isShowing())) dialog.show(fragment.getFragmentManager(), dialog.getClass().getName());
            } else {
                throw new IllegalArgumentException("The Context must be is AppCompatActivity or v4.app.Fragment.");
            }
        }
    }

    /**
     * 关闭对话框
     */
    public void close() {
        if (null != dialog && null != mContext) {
            dialog.dismiss(false);
        }  else if (null != netAnimListener && null != mContext){
            netAnimListener.stop();
        }
    }


    public CommonDialog getDialog() {
        return dialog;
    }

    public Object getObj() {
        return obj;
    }
}
