package com.fy.baselibrary.retrofit.load.up;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description 定义一个方法注解 UpLoadFileType2，
 * 用于 多文件上传 使用单文件上传接口，上传文件时候 retrofit 匹配对应的 converter
 * Created by fangs on 2021/5/17 11:15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UpLoadFileType2 {

}
