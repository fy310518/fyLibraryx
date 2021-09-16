@file:Suppress("FINAL_UPPER_BOUND")

package com.fy.baselibrary.application.mvvm

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * description：mvvm 架构 之 activity 实现接口 统一规范；
 * 项目自己创建的 activity 建议实现 此 接口
 * Created by fangs on 2021/9/10 10:04.
 */
interface IBaseActivity<VM : BaseViewModel, VDB : ViewDataBinding> {
    /**
     * 是否显示 标题栏
     */
    fun isShowHeadView(): Boolean


    /**
     * 设置 activity 布局 ID
     */
    @LayoutRes fun setContentLayout(): Int

    /**
     * 初始化
     * 注：在 activity 声明 binding: VDB 和 viewModel: VM 变量，并用 viewModel dataBinding 赋值
     */
    fun initData(viewModel : VM?, dataBinding : VDB?, savedInstanceState: Bundle?)

}