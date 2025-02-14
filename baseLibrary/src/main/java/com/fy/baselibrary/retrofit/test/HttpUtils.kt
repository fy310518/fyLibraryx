package com.fy.baselibrary.retrofit.test

import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.ArrayMap
import com.fy.baselibrary.application.ioc.ConfigUtils
import com.fy.baselibrary.retrofit.RequestUtils
import com.fy.baselibrary.retrofit.ServerException
import com.fy.baselibrary.retrofit.load.ApiService
import com.fy.baselibrary.retrofit.observer.IProgressDialog
import com.fy.baselibrary.retrofit.observer.TransmissionState
import com.fy.baselibrary.utils.AppUtils
import com.fy.baselibrary.utils.Constant
import com.fy.baselibrary.utils.FileUtils
import com.fy.baselibrary.utils.GsonUtils
import com.fy.baselibrary.utils.cache.SpfAgent
import com.fy.baselibrary.utils.media.UriUtils
import com.fy.baselibrary.utils.notify.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.RandomAccessFile

class HttpUtils {

    val CALL_BACK_LENGTH: Long = (1024 * 1024).toLong()

    companion object {
        val instance by lazy { HttpUtils() }
    }

    /**
     * 获取get 请求 被观察者，最终返回数据是 一个对象
     *
     * @param clazz  请求完成后 返回数据对象
     * @param apiUrl 请求Url
     * @param params 请求参数
     */
    fun <T> httpGet(clazz: Class<T>, apiUrl: String, progressDialog: IProgressDialog?,
                    params: ArrayMap<String, Any> = ArrayMap<String, Any>()): Flow<T> {
        return flow {
            L.e("request", "请求执行--> ${Thread.currentThread().name}")

            val result = RequestUtils.create(ApiService::class.java)
                .getCompose(apiUrl, params)

            if (result.isSuccess()) {
                val data = run {
                    val jsonData = GsonUtils.toJson(result.getResultData())
                    GsonUtils.fromJson(jsonData, clazz)
                }

                emit(data)
            } else {
                throw ServerException(result.getResultMsg(), result.getResultCode())
            }
        }.flowNext(progressDialog)
    }


    fun <T> postCompose(clazz: Class<T>, apiUrl: String, progressDialog: IProgressDialog?,
                    params: ArrayMap<String, Any> = ArrayMap<String, Any>()): Flow<T> {
        return flow {
            L.e("request", "请求执行--> ${Thread.currentThread().name}")

            val result = RequestUtils.create(ApiService::class.java)
                .postCompose(apiUrl, params)

            if (result.isSuccess()) {
                val data = run {
                    val jsonData = GsonUtils.toJson(result.getResultData())
                    GsonUtils.fromJson(jsonData, clazz)
                }
                emit(data)
            } else {
                throw Exception(result.getResultMsg())
            }
        }.flowNext(progressDialog)
    }

    fun <T> postForm(clazz: Class<T>, apiUrl: String, progressDialog: IProgressDialog?,
                        params: ArrayMap<String, Any> = ArrayMap<String, Any>()): Flow<T> {
        return flow {
            L.e("request", "请求执行--> ${Thread.currentThread().name}")

            val result = RequestUtils.create(ApiService::class.java)
                .postFormCompose(apiUrl, params)

            if (result.isSuccess()) {
                val data = run {
                    val jsonData = GsonUtils.toJson(result.getResultData())
                    GsonUtils.fromJson(jsonData, clazz)
                }
                emit(data)
            } else {
                throw Exception(result.getResultMsg())
            }
        }.flowNext(progressDialog)
    }

    fun <T> Flow<T>.flowNext(progressDialog: IProgressDialog?): Flow<T> {
        return flowOn(Dispatchers.IO)
            .onStart {
                L.e("request", "请求开始--> ${Thread.currentThread().name}")
                progressDialog?.show()
            }
            .onCompletion { cause ->
                L.e("request", "请求结束--> ${Thread.currentThread().name}")
                progressDialog?.close()
            }
            .catch { ex ->
                ex.printStackTrace()
            }

//            .collect {
//                return it
//            }
    }

    fun <R> uploadFile(apiUrl: String, files: ArrayList<R>, progressDialog: IProgressDialog?,
                   params: ArrayMap<String, Any> = ArrayMap<String, Any>(),
                       progressCallback: ((Float) -> Unit)? = null): Flow<Any> {

        val channel = Channel<Float>()
        GlobalScope.launch {
            for(proress in channel){
                L.e("request", "进度--> ${proress} ${Thread.currentThread().name}")
                progressCallback?.invoke(proress)
            }
        }
        return flow {
            L.e("request", "请求执行--> ${Thread.currentThread().name}")

            val item = files[0]
            if (item is String) params["filePathList"] = files
            else if (item is File) params["files"] = files
            else throw Exception("param exception")

            params["ProgressChannel"] = channel
            params["uploadFile"] = "files"
            params["isFileKeyAES"] = false
            RequestUtils.create(ApiService::class.java)
                .uploadFile(apiUrl, params)

            for(proress in channel){
                L.e("request", "进度--> ${proress} ${Thread.currentThread().name}")
                emit(proress)
            }

            emit("data")
        }
            .flowOn(Dispatchers.IO)
            .onStart {
                L.e("request", "请求开始--> ${Thread.currentThread().name}")
                progressDialog?.show()
            }
            .onCompletion { cause ->
                L.e("request", "请求结束--> ${Thread.currentThread().name}")
                progressDialog?.close()
                channel?.close()
            }
            .catch { ex ->
                ex.printStackTrace()
                channel?.close()
            }
    }


    /**
     * 下载文件
     */
     fun downLoadFile(downUrl: String,
                      targetPath: String,
                      reNameFile: String,
                      progressDialog: IProgressDialog?,
                      isReturnProcess: Boolean = false): Flow<TransmissionState> {

         return flow {
             var filePath: String = targetPath
             filePath = if (!TextUtils.isEmpty(filePath)) {
                 if (filePath.contains(AppUtils.getLocalPackageName())) { // 下载到 指定的私有目录
                     FileUtils.folderIsExists(filePath).path
                 } else {
                     FileUtils.getSDCardDirectoryTpye(filePath) + ConfigUtils.getFilePath()
                 }
             } else {
                 FileUtils.folderIsExists(FileUtils.DOWN, 0).path
             }

             val tempFile = FileUtils.getTempFile(downUrl, filePath)

             var targetFile = FileUtils.getFile(downUrl, filePath)
             if (!TextUtils.isEmpty(reNameFile)) { // 找最终下载完成的 文件
                 targetFile = File(filePath, reNameFile)
             }
             val downParam = if (targetFile.exists()) {
                 targetFile.path
             } else {
                 "bytes=" + tempFile.length() + "-"
             }

             val responseBody = RequestUtils.create(ApiService::class.java)
                 .download(downParam, downUrl)

             val file = saveFile(responseBody, downUrl, filePath){
                 if (isReturnProcess) {
                     emit(TransmissionState.InProgress(it))
                 }
             }

             emit(TransmissionState.Success(file))
         }.flowOn(Dispatchers.IO)
             .onStart {
                 L.e("request", "请求开始--> ${Thread.currentThread().name}")
                 progressDialog?.show()
             }
             .onCompletion { cause ->
                 L.e("request", "请求结束--> ${Thread.currentThread().name}")
                 progressDialog?.close()
             }
             .catch { ex ->
                 ex.printStackTrace()
             }
    }

    /**
     * 根据ResponseBody 写文件
     * @param responseBody
     * @param url
     * @param filePath     文件保存路径
     * @return
     */
    private inline fun saveFile(responseBody: ResponseBody, url: String, filePath: String, progressListener: (Float) -> Unit): File {
        var tempFile = FileUtils.getTempFile(url, filePath)

        val reName_MAP = ArrayMap<String, String>()


        var uri: Uri? = null
        if (!filePath.contains(AppUtils.getLocalPackageName())) { // 下载到 sd卡 Environment.DIRECTORY_DOWNLOADS 目录
            UriUtils.deleteFileUri("Downloads",
                MediaStore.Downloads.RELATIVE_PATH + "=? AND " + MediaStore.Downloads.DISPLAY_NAME + " =?",
                arrayOf(Environment.DIRECTORY_DOWNLOADS + "/" + ConfigUtils.getFilePath(), tempFile.name)
            )

            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.Downloads.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS + "/" + ConfigUtils.getFilePath()
            )
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, tempFile.name)
            uri = UriUtils.createFileUri(contentValues, "Downloads")
        } else {
            tempFile = FileUtils.fileIsExists(tempFile.path)
        }


        var file: File = writeFileToDisk(responseBody, url, tempFile.absolutePath, uri, progressListener)
        try {
            val FileDownStatus = SpfAgent.init("").getInt(url + Constant.FileDownStatus)

            val renameSuccess: Boolean
            if (FileDownStatus == 4) {
                val contentValues = ContentValues()
                contentValues.put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/" + ConfigUtils.getFilePath()
                )

                val fileName = FileUtils.getFileName(url)
                var resultFile = File(filePath, fileName)
                if (reName_MAP.containsKey(url)) {
                    if (null != uri) {
                        contentValues.put(
                            MediaStore.Downloads.DISPLAY_NAME,
                            reName_MAP[url]
                        )
                        contentValues.put(
                            MediaStore.Downloads.DATA,
                            filePath + File.separator + reName_MAP[url]
                        )
                        renameSuccess = UriUtils.updateFileUri(uri, contentValues)
                        resultFile = File(filePath, reName_MAP[url])
                    } else {
                        resultFile = File(tempFile.parent, reName_MAP[url])
                        renameSuccess = tempFile.renameTo(resultFile)
                    }
                } else {
                    if (null != uri) {
                        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        contentValues.put(
                            MediaStore.Downloads.DATA,
                            filePath + File.separator + fileName
                        )
                        renameSuccess = UriUtils.updateFileUri(uri, contentValues)
                    } else {
                        renameSuccess = FileUtils.reNameFile(url, tempFile.path)
                    }
                }

                return if (renameSuccess) {
                    resultFile
                } else {
                    tempFile
                }
            } else if (FileDownStatus == 3) { //取消下载则 删除下载内容
                FileUtils.deleteFileSafely(tempFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    /**
     * 单线程 断点下载
     *
     * @param responseBody
     * @param filePath
     * @return
     * @throws IOException
     */
    private inline fun writeFileToDisk(responseBody: ResponseBody, url: String, filePath: String?, uri: Uri?, progressListener: (Float) -> Unit): File {
        val file = File(filePath)
        val totalByte = responseBody.contentLength()
        val fileTotalByte = file.length() + totalByte

        SpfAgent.init("").saveInt(url + Constant.FileDownStatus, 1).commit(false) //正在下载
        val buffer = ByteArray(1024 * 4)

        var `is`: InputStream? = null
        var randomAccessFile: RandomAccessFile? = null
        var out: OutputStream? = null

        try {
            `is` = responseBody.byteStream()

            if (null != uri) {
                val resolver = ConfigUtils.getAppCtx().contentResolver
                out = resolver.openOutputStream(uri)
            } else {
                val tempFileLen = file.length()
                randomAccessFile = RandomAccessFile(file, "rwd")
                randomAccessFile.seek(tempFileLen)
            }

            var downloadByte: Long = 0
            var lastTotal = 0L

            while (true) {
                val len = `is`.read(buffer)
                if (len == -1) { //下载完成
                    SpfAgent.init("").saveInt(url + Constant.FileDownStatus, 4).commit(false) //下载完成
                    break
                }

                val FileDownStatus = SpfAgent.init("").getInt(url + Constant.FileDownStatus)
                if (FileDownStatus == 2 || FileDownStatus == 3) break //暂停或者取消 停止下载


                if (null != randomAccessFile) {
                    randomAccessFile.write(buffer, 0, len)
                } else {
                    out?.write(buffer, 0, len)
                }

                downloadByte += len.toLong()
                lastTotal += len.toLong()

                if (lastTotal >= CALL_BACK_LENGTH || downloadByte >= fileTotalByte) { //避免每写4096字节，就回调一次，那未免太奢侈了，所以设定一个常量每1mb回调一次
                    progressListener((downloadByte * 100 / fileTotalByte).toFloat())
                    lastTotal = 0
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            FileUtils.closeIO(out, `is`, responseBody)
        }

        return file
    }


}