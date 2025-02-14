package com.fy.baselibrary.base.dialog;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.databinding.ViewDataBinding;

import com.fy.baselibrary.application.mvvm.BaseViewModel;
import com.fy.baselibrary.base.ViewHolder;

/**
 * 完善 没有扩展需求情况下 使用父类创建dialog 不够优雅的问题
 */
public class NiceDialog<VM extends BaseViewModel, VDB extends ViewDataBinding> extends CommonDialog<VM, VDB> {
    private static final String LISTENER = "ViewConvertListener";
    private static final String DestroyLISTENER = "ViewDestroyListener";

    private DialogConvertListener dialogConvertListener;
    private DialogDestroyListener dialogDestroyListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            dialogConvertListener = savedInstanceState.getParcelable(LISTENER);
            dialogDestroyListener = savedInstanceState.getParcelable(DestroyLISTENER);
        }
    }


    @Override
    protected int initLayoutId() {
        return layoutId;
    }

    @Override
    public void convertView(CommonDialog dialog) {
        if (null != dialogConvertListener) {
            dialogConvertListener.convertView(ViewHolder.createViewHolder(getContext(), mRootView), dialog);
        }
    }

    @Override
    public void onDestroy() {
        if (null != dialogDestroyListener) {
            dialogDestroyListener.destroyView(this);
        }
        super.onDestroy();
    }

    /**
     * 创建 dialog
     *
     * @return
     */
    public static NiceDialog init() {
        return new NiceDialog();
    }

    public NiceDialog setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public NiceDialog setDialogConvertListener(DialogConvertListener dialogConvertListener) {
        this.dialogConvertListener = dialogConvertListener;
        return this;
    }

    public NiceDialog setDialogDestroyListener(DialogDestroyListener dialogDestroyListener) {
        this.dialogDestroyListener = dialogDestroyListener;
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LISTENER, dialogConvertListener);
        if (null != dialogDestroyListener){
            outState.putParcelable(DestroyLISTENER, dialogDestroyListener);
        }
    }
}
