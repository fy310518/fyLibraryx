package com.fy.baselibrary.retrofit.load

import android.util.ArrayMap
import com.fy.baselibrary.retrofit.load.down.DownLoadFileType
import com.fy.baselibrary.retrofit.load.up.UpLoadFileType
import com.fy.baselibrary.retrofit.test.BeanModule
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiService {

    //普通get 请求
    @GET
    suspend fun getCompose(@Url apiUrl: String, @QueryMap params: ArrayMap<String, Any>): BeanModule<Any>

    //普通post 请求【请求参数 json格式 】
    @POST
    suspend fun postCompose(@Url apiUrl: String, @Body params: ArrayMap<String, Any>): BeanModule<Any>

    //普通post 请求【表单提交】
    @FormUrlEncoded
    @POST
    suspend fun postFormCompose(@Url apiUrl: String, @FieldMap params: ArrayMap<String, Any>): BeanModule<Any>


    /**
     * 断点下载
     * @param downParam 下载参数，传下载区间使用 "bytes=" + startPos + "-"
     *                  【IF-RANGE 如果服务器不支持分段下载，则直接下载整个文件】
     * @param url
     * @return
     */
    @DownLoadFileType
    @Streaming
    @GET
    @Headers(value = ["NoReplaceIp:---", "CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"])
    suspend fun download(@Header("IF-RANGE") downParam: String, @Url url: String): ResponseBody

    /**
     * 通用 图文上传 (支持多图片) （参数注解：@Body；参数类型：MultipartBody）
     * params.put("uploadFile", "fileName");
     * params.put("filePathList", files);
     *
     * 注意：其它 文本参数 value 必须是 字符串类型（如下 token 参数）
     * params.put("token", "123");
     */
    @UpLoadFileType
    @Headers(value = ["CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"])
    @POST
    suspend fun uploadFile(@Url apiUrl: String,
                           @Body params: ArrayMap<String, Any>): Any

    /**
     * 多图片上传 方式二（@Multipart：方法注解；@Part：参数注解；参数类型；MultipartBody.Part）
     * @param apiUrl
     * @param txtParams  文本参数，可多个 （转换方式：MultipartBody.Part.createFormData("key", "参数");）
     * @param files  文件
     * @return
     */
//    @Multipart
//    @Headers(value = ["CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"])
//    @POST
//    suspend fun uploadFile(@Url apiUrl: String,
//                           @Part txtParams: ArrayList<MultipartBody.Part>,
//                           @Part files : MultipartBody.Part): Any
    @Multipart
    @Headers(value = ["CONNECT_TIMEOUT:60000", "READ_TIMEOUT:60000", "WRITE_TIMEOUT:60000"])
    @POST
    suspend fun uploadFile(@Url apiUrl: String,
                             @Part txtParams: ArrayList<MultipartBody.Part>?,
                             @Part files : ArrayList<MultipartBody.Part>): Any
}