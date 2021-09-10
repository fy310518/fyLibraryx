@file:Suppress("FINAL_UPPER_BOUND")

package com.fy.baselibrary.application.mvvm

import android.app.Activity
import android.os.Bundle
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
     * 1、设置 activity 布局 ID，并返回 ViewDataBinding
     * binding = DataBindingUtil.setContentView(this, getContentViewId());
     * 给binding加上感知生命周期，AppCompatActivity就是lifeOwner
     * binding.setLifecycleOwner(this);
     *
     * 2、设置 ViewModel
     * new ViewModelProvider(activity).get(modelClass)
     *
     * 注：在 activity 声明 binding: VDB 和 viewModel: VM 变量
     */
    fun getView(): VDB

    /**
     * 初始化
     */
    fun initData(activity: Activity, savedInstanceState: Bundle?)

}