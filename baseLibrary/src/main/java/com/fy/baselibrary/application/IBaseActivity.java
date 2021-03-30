package com.fy.baselibrary.application;

import android.app.Activity;
import android.os.Bundle;

import androidx.viewbinding.ViewBinding;

/**
 * activity 实现接口 统一规范
 * 项目自己新建的 activity 建议实现 此 接口
 * Created by fangs on 2018/3/13.
 */
public interface IBaseActivity {

    /**
     * 是否显示 标题栏
     */
    boolean isShowHeadView();

    /**
     * Activity 界面布局文件 ViewBinding
     * XXXBinding.inflate(LayoutInflater.from(this));
     * OR
     * XXXBinding.inflate(getLayoutInflater());
     */
    ViewBinding getView();

    /**
     * 初始化
     */
    void initData(Activity activity, Bundle savedInstanceState);

}
