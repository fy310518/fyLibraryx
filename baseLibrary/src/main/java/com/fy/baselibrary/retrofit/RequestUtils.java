package com.fy.baselibrary.retrofit;

import android.os.Handler;
import android.os.Looper;

import com.fy.baselibrary.utils.Constant;
import com.fy.baselibrary.utils.cache.SpfAgent;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * 网络请求入口
 * Created by fangs on 2018/3/13.
 */
public final class RequestUtils {

    public volatile static RequestUtils instance;

//    @Inject
    protected Retrofit netRetrofit;

//    @Inject
    protected OkHttpClient.Builder okBuilder;//使 上层依赖 可以获得唯一的 OkHttpClient；


    private RequestUtils() {
//        DaggerRequestComponent.builder().build().inJect(this);

        okBuilder = RequestModule.getClient();
        netRetrofit = RequestModule.getService(okBuilder);
    }

    public static synchronized RequestUtils getInstance() {
        if (null == instance) {
            synchronized (RequestUtils.class) {
                if (null == instance) {
                    instance = new RequestUtils();
                }
            }
        }

        return instance;
    }

    public static OkHttpClient.Builder getOkBuilder() {
        return getInstance().okBuilder;
    }

    /**
     * 得到 RxJava + Retrofit 被观察者 实体类
     *
     * @param clazz 被观察者 类（ApiService.class）
     * @param <T>   被观察者 实体类（ApiService）
     * @return 封装的网络请求api
     */
    public static <T> T create(Class<T> clazz) {
        return getInstance().netRetrofit.create(clazz);
    }







//    //暂停所有下载任务
//    public static void pauseAllDownLoad(){
//        for (String key : FileResponseBodyConverter.LISTENER_MAP.keySet()) {
//            pauseDownLoad(key);
//        }
//    }
//
//    //取消所有下载
//    public static void cancelAllDownLoad() {
//        for (String key : FileResponseBodyConverter.LISTENER_MAP.keySet()) {
//            cancelDownLoad(key);
//        }
//    }

    /**
     * 暂停下载
     * @param url 文件下载地址
     */
    public static void pauseDownLoad(String url){
//        final String filePath = FileUtils.folderIsExists(FileUtils.DOWN, ConfigUtils.getType()).getPath();
//        final File tempFile = FileUtils.getTempFile(url, filePath);

        SpfAgent.init("").saveInt(url + Constant.FileDownStatus, 2).commit(false);//暂停下载
    }

    /**
     * 取消下载
     * @param url 文件下载地址
     */
    public static void cancelDownLoad(String url){
//        final String filePath = FileUtils.folderIsExists(FileUtils.DOWN, ConfigUtils.getType()).getPath();
//        final File tempFile = FileUtils.getTempFile(url, filePath);

        SpfAgent.init("").saveInt(url + Constant.FileDownStatus, 3).commit(false);//取消下载
    }

    /**
     * 文件下载
     */
//    public static Observable<Object> downLoadFile(@NonNull String url){
//        return downLoadFile(url, "", "", false);
//    }

//    /**
//     * 文件下载
//     * @param url
//     * @param targetPath  下载文件到 此目录（1：app 私有目录；2：android 10 SD卡公共 Environment.DIRECTORY_DOWNLOADS）
//     * @param reNameFile
//     */
//    public static Observable<Object> downLoadFile(@NonNull final String url, @NonNull String targetPath, @NonNull String reNameFile, boolean isReturnProcess){
//        if(!url.startsWith("http://") && !url.startsWith("https://")){
//            return Observable.just("");
//        }
//
//        if(SpfAgent.init("").getInt(url + Constant.FileDownStatus) == 1) {
//            return Observable.error(new ServerException("Downloading", -120));
//        }
//
//        ArrayMap<String, String> data = new ArrayMap<>();
//        data.put("requestUrl", url);
//        if(!TextUtils.isEmpty(targetPath)){
//            data.put("targetFilePath", targetPath);
//        }
//        if(!TextUtils.isEmpty(reNameFile)){
//            data.put("reNameFile", reNameFile);
//        }
//
//        return Observable.just(data).flatMap(new Function<ArrayMap<String, String>, ObservableSource<Object>>() {
//            @Override
//            public ObservableSource<Object> apply(@NonNull ArrayMap<String, String> arrayMap) throws Exception {
//                String downUrl = arrayMap.get("requestUrl");
//                String reNameFile = "";
//                if(arrayMap.containsKey("reNameFile")){
//                    reNameFile = arrayMap.get("reNameFile");
//                }
//
//                String filePath = arrayMap.get("targetFilePath");
//                if(!TextUtils.isEmpty(filePath)){
//                    if(filePath.contains(AppUtils.getLocalPackageName())){ // 下载到 指定的私有目录
//                        filePath = FileUtils.folderIsExists(filePath).getPath();
//                    } else {
//                        filePath = FileUtils.getSDCardDirectoryTpye(filePath) + ConfigUtils.getFilePath();
//                    }
//                } else {
//                    filePath = FileUtils.folderIsExists(FileUtils.DOWN, 0).getPath();
//                }
//
//                LoadOnSubscribe loadOnSubscribe = new LoadOnSubscribe();
//                FileResponseBodyConverter.addListener(downUrl, filePath, reNameFile, isReturnProcess ? loadOnSubscribe : null);
//
//                final File tempFile = FileUtils.getTempFile(downUrl, filePath);
//
//                File targetFile = FileUtils.getFile(downUrl, filePath);
//                if(!TextUtils.isEmpty(reNameFile)){ // 找最终下载完成的 文件
//                    targetFile = new File(filePath, reNameFile);
//                }
//
//                String downParam;
//                if (targetFile.exists()) {
//                    downParam = targetFile.getPath();
//                } else {
//                    downParam = "bytes=" + tempFile.length() + "-";
//                }
//
//                if (downParam.startsWith("bytes=")) {
//                    L.e("fy_file_FileDownInterceptor", "文件下载开始---" + Thread.currentThread().getName());
//                    if(isReturnProcess){
//                        return Observable.merge(Observable.create(loadOnSubscribe), RequestUtils.create(LoadService.class).download(downParam, url));
//                    } else {
//                        return RequestUtils.create(LoadService.class)
//                                .download(downParam, url)
//                                .flatMap(new Function<File, ObservableSource<File>>() {
//                                    @Override
//                                    public ObservableSource<File> apply(@NonNull File file) throws Exception {
//                                        L.e("fy_file_FileDownInterceptor", "文件下载完成---" + Thread.currentThread().getName());
//                                        return Observable.just(file);
//                                    }
//                                });
//                    }
//                } else {
//                    SpfAgent.init("").saveInt(url + Constant.FileDownStatus, 4).commit(false);
//                    return Observable.just(new File(downParam));
//                }
//            }
//        });
////                .subscribeOn(Schedulers.io())
////                .subscribe(new FileCallBack(url, pDialog) {
////                    @Override
////                    protected void downSuccess(File file) {
////                        loadListener.onProgress("100");
////                        runUiThread(() -> {
////                            loadListener.onSuccess((File) file);//已在主线程中，可以更新UI
////                        });
////                    }
////
////                    @Override
////                    protected void downProgress(String percent) {
////                        loadListener.onProgress(percent);
////                    }
////                });
//    }

    public interface OnRunUiThreadListener{
        void onRun();
    }

    /**
     * 定义 回调 UI线程
     * @param runUiThreadListener
     */
    public static void runUiThread(OnRunUiThreadListener runUiThreadListener){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            runUiThreadListener.onRun();
        });
    }


}
