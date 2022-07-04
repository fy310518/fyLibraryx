package com.fy.baselibrary.application.mvvm;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.databinding.ViewDataBinding;

/**
 * description：mvvm 架构 之 activity 实现接口 统一规范；
 * 项目自己创建的 activity 建议实现 此 接口
 * Created by fangs on 2022/7/4 16:58.
 */
public interface IBaseMVVM<VM extends BaseViewModel, VDB extends ViewDataBinding> {

    /**
     * 设置 activity 布局 ID
     */
    @LayoutRes int setContentLayout();

    /**
     * 初始化
     * 注：在 activity 声明 binding: VDB 和 viewModel: VM 变量，并用 viewModel dataBinding 赋值
     */
    void initData(VM viewModel, VDB dataBinding, Bundle savedInstanceState);

}
