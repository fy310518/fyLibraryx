package com.fy.baselibrary.retrofit.test

import com.fy.baselibrary.retrofit.BaseBean
import com.google.gson.annotations.SerializedName

data class BeanModule<T>(
    @SerializedName("message", alternate = ["errorMsg"])
    val message: String,
    @SerializedName("code", alternate = ["errorCode"])
    val code: Int,

    @SerializedName("result", alternate = ["data"])
    val result: T
) : BaseBean<T> {

    override fun getResultCode() = code

    override fun getResultMsg() = message

    override fun getResultData() = result

    override fun isSuccess() = code == 0

}
