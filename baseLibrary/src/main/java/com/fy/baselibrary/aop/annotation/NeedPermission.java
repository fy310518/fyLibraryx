package com.fy.baselibrary.aop.annotation;

import android.view.Gravity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义一个注解 NeedPermission，用来注解方法，
 * 以便在编译期被编译器检测到需要做切面的方法
 * 注意：建议用在 activity，fragment
 * Created by fangs on 2018/8/27 15:32.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedPermission {

    /**
     * 请求权限列表
     */
    String[] value() default {};

    /**
     * 第一次拒绝必要权限的提示信息
     */
    String firstRefuseMsg() default "";

    /**
     * 永久拒绝必要权限的提示信息
     */
    String alwaysRefuseMsg() default "";

    /**
     * 危险权限请求失败，显示弹窗位置
     */
    int gravity() default Gravity.BOTTOM;

    /**
     * 特殊权限被拒绝后，弹窗 是否显示 取消按钮
     */
    boolean isShowCancelBtn() default true;


    /**
     * 存在被拒绝的权限时，是否继续执行
     * @return 默认 false，不继续执行
     */
    boolean isRun() default false;
}
