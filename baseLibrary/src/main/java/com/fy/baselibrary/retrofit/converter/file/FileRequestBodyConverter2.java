package com.fy.baselibrary.retrofit.converter.file;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.fy.baselibrary.retrofit.load.up.ProgressRequestBody;
import com.fy.baselibrary.utils.security.EncodeUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

import kotlinx.coroutines.channels.Channel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 文件上传 转换器
 * Created by fangs on 2018/11/12.
 */
public class FileRequestBodyConverter2 extends FileRequestBodyConverter {

    //进度发射器

    public FileRequestBodyConverter2() {
    }

    @Override
    public RequestBody convert(ArrayMap<String, Object> params) throws IOException {
        String fileKey = (String) params.get("uploadFile");

        if (TextUtils.isEmpty(fileKey)) fileKey = "file";

        if (params.containsKey("filePathList")) {
            return filesToMultipartBody((List<String>) params.get("filePathList"), fileKey, params);
        } else if (params.containsKey("files")) {
            return filesToMultipartBody((List<File>) params.get("files"), fileKey, params);
        } else {
            return null;
        }
    }

    /**
     * 用于把 File集合 或者 File路径集合 转化成 MultipartBody
     * @param <T> 泛型（File 或者 String）
     * @param files File列表或者 File 路径列表
     * @param fileKey 文件上传 表单提交，文件key (默认为 "file"，一般根据后台提供)
     * @return MultipartBody（retrofit 多文件文件上传）
     */
    public synchronized <T> MultipartBody filesToMultipartBody(List<T> files, String fileKey, ArrayMap<String, Object> params) {
        boolean isFileKeyAES = (boolean) params.get("isFileKeyAES");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        //进度发射器
        Channel<Float> channel = (Channel<Float>) params.get("ProgressChannel");

        //解析 文本参数 装载到 MultipartBody 中
        for (String key : params.keySet()) {
            if (!TextUtils.isEmpty(key)
                    && !key.equals("ProgressChannel")
                    && !key.equals("uploadFile")
                    && !key.equals("isFileKeyAES")
                    && !key.equals("filePathList")
                    && !key.equals("files")){
                builder.addFormDataPart(key, (String) params.get(key));
            }
        }

        File file;
//        此种方式只是为了应对 特定服务器上传图片
        for (int i = 0; i < files.size(); i++) {
            T t = files.get(i);
            if (t instanceof File) file = (File) t;
            else if (t instanceof String) file = new File((String) t);//访问手机端的文件资源，保证手机端sdcdrd中必须有这个文件
            else break;

            String contentType = URLConnection.guessContentTypeFromName(file.getName());
            if(TextUtils.isEmpty(contentType)){
                contentType = "multipart/form-data";
            }

            ProgressRequestBody requestBody = new ProgressRequestBody(file, contentType, channel);
            if (files.size() > 1){
                builder.addFormDataPart(isFileKeyAES ? fileKey + (i + 1) : fileKey, file.getName(), requestBody);
            } else {
                String name = fileKey.equals("fileName") ? fileKey + 1 : fileKey;
                builder.addFormDataPart(name, EncodeUtils.urlEncode(file.getName()), requestBody);
            }
        }

        return builder.build();
    }

}
