package com.fy.baselibrary.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.fy.baselibrary.application.mvvm.BaseViewModel;
import com.fy.baselibrary.application.mvvm.IBaseMVVM;
import com.fy.baselibrary.statuslayout.LoadSirUtils;
import com.fy.baselibrary.statuslayout.OnSetStatusView;
import com.fy.baselibrary.statuslayout.StatusLayoutManager;
import com.fy.baselibrary.utils.AnimUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.notify.L;


/**
 * Fragment 基类
 * Created by fangs on 2017/4/26.
 */
public abstract class BaseFragment<VM extends BaseViewModel, VDB extends ViewDataBinding> extends Fragment implements IBaseMVVM<VM, VDB>, View.OnClickListener, OnSetStatusView {
    public final String TAG = "lifeCycle --> " + getClass().getSimpleName();

    protected AppCompatActivity mContext;
    protected StatusLayoutManager slManager;

    protected VDB vdb;
    protected VM vm;
    protected View mRootView;

    /** 设置懒加载 */
    protected void lazyData() {}

    @Override
    public void onClick(View view) {}

    @Override
    public void onRetry() {}

    @Override
    public View setStatusView(){return mRootView;}

    @Override
    public void showHideViewFlag(int flagId) {
        if (null != slManager) slManager.showHideViewFlag(flagId);
    }

    @Override//Fragment和Activity建立关联的时候调用
    public void onAttach(Context context) {
        super.onAttach(context);
        L.e(TAG, "onAttach()");

        this.mContext = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.e(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mRootView) {
            if (-1 != setContentLayout()){
                vdb = DataBindingUtil.inflate(LayoutInflater.from(getContext()), setContentLayout(), container, false);
                vdb.setLifecycleOwner(getActivity());
                mRootView = vdb.getRoot();

                vm = AnimUtils.createViewModel(this);
            }

            initData(vm, vdb, savedInstanceState);
            if (-1 != setContentLayout()) slManager = LoadSirUtils.initStatusLayout(this);

            isViewCreated = true;
        } else {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        }
        L.e(TAG, "onCreateView()");

        return mRootView;
    }

    @Override//当Activity中的onCreate方法执行完后调用
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.e(TAG, "onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        isActivityShow = true;
        L.e(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.e(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        L.e(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityShow = false;
        L.e(TAG, "onStop()");
    }

    @Override//Fragment中的布局被移除时调用
    public void onDestroyView() {
        super.onDestroyView();
        L.e(TAG, "onDestroyView()");
        if (null != vdb) vdb = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.e(TAG, "onDestroy()");
    }

    @Override//Fragment和Activity解除关联的时候调用
    public void onDetach() {
        super.onDetach();
        L.e(TAG, "onDetach()");
    }

    /**
     * fragment 设置 toolbar
     * @param title
     * tips：重写 onCreateOptionsMenu 方法 可以设置 菜单
     */
    protected void setToolbar(Toolbar toolbar, @StringRes int title, @Nullable View.OnClickListener listener){
        setToolbar(toolbar, ResUtils.getStr(title), listener);
    }

    protected void setToolbar(Toolbar toolbar, String title, @Nullable View.OnClickListener listener){
        AnimUtils.setToolbar(this, toolbar, title, listener);
    }

    /**
     * 当前activity 是否显示
     * 目的：解决activity 跳转到一个已存在的activity 并显示指定位置的fragment，onResume方法重走两次问题
     */
    private boolean isActivityShow;
    //activity内部切换 Fragment 不回调onPause() 和 onResume() 方法解决方案
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isActivityShow) return;
        if (hidden) {// 不在最前端界面显示
            onPause();
        } else {// 重新显示到最前端中
            onResume();
        }
    }


    /** Fragment的View加载完毕的标记 */
    private boolean isViewCreated;
    /** Fragment对用户可见的标记 */
    private boolean isUIVisible;
    //当fragment结合viewpager使用的时候 这个方法会调用
    //这个方法是在oncreateView之前使用 不要使用控件
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        onHiddenChanged(isVisibleToUser);

        if (isVisibleToUser) {
            isUIVisible = true;
            lazyLoad();
        } else {
            isUIVisible = false;
        }

        L.e(TAG, "setUserVisibleHint()");
    }

    // 这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,
    // 必须确保onCreateView加载完毕且页面可见,才加载数据
    private void lazyLoad() {
        if (isViewCreated && isUIVisible) {
            lazyData();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            isUIVisible = false;
        }
    }

}
