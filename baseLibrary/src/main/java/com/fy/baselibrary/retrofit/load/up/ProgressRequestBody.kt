package com.fy.baselibrary.retrofit.load.up

import com.fy.baselibrary.utils.notify.L
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.internal.closeQuietly
import okio.BufferedSink
import okio.Source
import okio.source
import java.io.File
import java.nio.Buffer

class ProgressRequestBody(
    private val file: File,
    private val contentType: String = "",
    private val channel: Channel<Float>
): RequestBody() {


    override fun contentLength() = file.length()

    override fun contentType() = contentType.toMediaTypeOrNull()


    //todo 加这个参数 是因为不同的手机系统，这个回调方法执行次数不同原因未知（手里有的一个华为手机、华为平板执行3次，三星执行两次）
    private var sum = 0
    val SEGMENT_SIZE = 2048L
    override fun writeTo(sink: BufferedSink) {
        sum++
        var ispercent = sink is Buffer //如果传入的 sink 为 Buffer 类型，则直接写入，不进行百分比统计
        L.e("进度--", "$ispercent")

        var source: Source? = null
        try {
            source = file.source()
            val fileLength = file.length()
            var total = 0L
            var lastTotal = 0L // 避免 每次 都发送进度，读大于 1M 就发送一次进度

            var read = source.read(sink.buffer, SEGMENT_SIZE)
            while (read != -1L) {
                total += read
                lastTotal += read

                sink.flush()

                if (!ispercent && sum >= 1) {
                    if(lastTotal >= 1024 * 1024 || total >= fileLength){
                        GlobalScope.launch {
    //                        L.e("request", "进度--> ${total} $fileLength ${Thread.currentThread().name}")
                            channel.send((total * 100 / fileLength).toFloat())
                        }

                        lastTotal = 0L
                    }
                }

                read = source.read(sink.buffer, SEGMENT_SIZE)
            }
        } catch (e: Exception) {
        } finally {
            source?.closeQuietly()
        }
    }

}