package com.fy.baselibrary.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.recyclerview.widget.RecyclerView;

import com.fy.baselibrary.utils.ResUtils;

/**
 * 通用 ViewHolder
 * Created by 下载 on 2017/7/31.
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    public View mConvertView;
    private Context mContext;

    public ViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    public static ViewHolder createViewHolder(Context context, View itemView) {
        return new ViewHolder(context, itemView);
    }

    public static ViewHolder createViewHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(context, itemView);
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <V extends View> V getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (V) view;
    }

//////////////////////以下为 常用控件设置文本 图片 点击事件 方法 可根据需要添加///////////////////////
    /**
     * 设置指定 viewID 的TextView 的文本
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置指定 viewID 的TextView 的文本
     * @param viewId
     * @param strId
     */
    public ViewHolder setText(int viewId, int strId) {
        TextView tv = getView(viewId);
        tv.setText(strId);
        return this;
    }

    /**
     * 设置指定 viewID 的TextView 的 字体颜色
     * @param viewId
     * @param colorId
     */
    public ViewHolder setTextColor(int viewId, @ColorRes int colorId) {
        TextView tv = getView(viewId);
        tv.setTextColor(ResUtils.getColor(colorId));
        return this;
    }

    /**
     * 设置指定 viewID 的ImageView 的图片（资源图片）
     * @param viewId
     * @param resId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    /**
     * 设置指定 viewID 的View 的 点击事件
     * @param viewId
     * @param listener
     * @return
     */
    public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        if (null != listener){
            View view = getView(viewId);
            view.setOnClickListener(listener);
        }
        return this;
    }

    public ViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        if (null != listener){
            View view = getView(viewId);
            view.setOnLongClickListener(listener);
        }
        return this;
    }

    /**
     * 设置指定 viewID 的View 的可见性
     * @param viewId
     * @param isVisibility
     * @return
     */
    public ViewHolder setVisibility(int viewId, boolean isVisibility) {
        View view = getView(viewId);
        view.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置背景
     * @param viewId
     * @param drawable
     */
    public ViewHolder setBackground(int viewId, Drawable drawable) {
        View view = getView(viewId);
        view.setBackground(drawable);
        return this;
    }

    public ViewHolder setBackground(int viewId, @ColorRes int color) {
        View view = getView(viewId);
        view.setBackgroundColor(ResUtils.getColor(color));
        return this;
    }

}
