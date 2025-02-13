package com.fy.baselibrary.utils.imgload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fy.baselibrary.retrofit.RequestUtils;
import com.fy.baselibrary.retrofit.interceptor.FileDownInterceptor;
import com.fy.baselibrary.utils.imgload.imgprogress.ImgLoadCallBack;
import com.fy.baselibrary.utils.imgload.imgprogress.ProgressListener;
import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.util.concurrent.ExecutionException;


/**
 * 图片加载工具类(目前使用 Glide)
 * Created by fangs on 2017/5/5.
 */
public class ImgLoadUtils {

    private ImgLoadUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 预加载 （把指定URL地址的图片 的原始尺寸保存到缓存中）
     * @param model
     */
    public static void preloadImg(Context context, Object model) {
        Glide.with(context)
                .load(model)
                .preload();
    }

    /**
     * 获取 glide 缓存在磁盘的图片文件
     * @param context
     * @param model
     */
    public static File getImgCachePath(Context context, Object model, RequestOptions options) throws ExecutionException, InterruptedException {
        if(null == options){
            options = new RequestOptions()
//                    .onlyRetrieveFromCache(true) // 只从缓存中检索数据，而不去网络或其他地方获取新的数据
            ;
        }

        FutureTarget<File> target = Glide.with(context)
                .asFile()
                .load(model)
                .apply(options)
                .submit();//必须要用在子线程当中

        return target.get();
    }

    public static Bitmap getImgCacheBitmap(Context context, Object model, RequestOptions options) throws ExecutionException, InterruptedException {
        if(null == options){
            options = new RequestOptions()
//                    .onlyRetrieveFromCache(true) // 只从缓存中检索数据，而不去网络或其他地方获取新的数据
            ;
        }
        return Glide.with(context)
                .asBitmap()
                .load(model)
                .apply(options)
                .submit() //必须要用在子线程当中
                .get();
    }

//    android:scaleType="centerCrop"
//    .skipMemoryCache(true) //设置 跳过内存缓存
//    .circleCrop() 圆形图片
//    .transform(new CenterCrop(), new RoundedCorners(15)) 圆角图片
//    .transform(new BlurTransformation(25));图片模糊
    public static RequestOptions getDefaultOption(@NonNull int errorId){
        return new RequestOptions()
                .fallback(errorId)
                .error(errorId)
                .placeholder(errorId)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    public static RequestOptions getDefaultOption(@NonNull int errorId, @NonNull int placeholderId){
        return new RequestOptions()
                .fallback(errorId)
                .error(errorId)
                .placeholder(placeholderId)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    /**
     * 根据 RequestOptions 加载定 model 的图片
     * @param model
     * @param imageView
     * @param options
     */
    public static void loadImage(@NonNull Object model, @NonNull ImageView imageView, @NonNull RequestOptions options) {
        Glide.with(imageView.getContext())
                .load(model)
                .apply(options)
                .listener(new RequestListener<Drawable>(){
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        Observable.just("")
//                                .flatMap((Function<String, ObservableSource<String>>) s -> {
//                                    File file = ImgLoadUtils.getImgCachePath(ConfigUtils.getAppCtx(), model, new RequestOptions().onlyRetrieveFromCache(true));
//                                    if(null != file) {
//                                        L.e("-----file", file.getPath());
//                                        FileUtils.deleteFileSafely(file);
//                                    }
//
//                                    return Observable.just("");
//                                })
//                                .subscribeOn(Schedulers.io())
//                                .subscribe(new RequestBaseObserver<String>(){
//                                    @Override
//                                    protected void onSuccess(String t) {
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                    }
//                                });
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }

    /**
     * 加载指定URL的图片
     * @param model
     * @param imageView
     */
    public static void loadImage(@NonNull Object model, @NonNull int errorId, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(model)
                .apply(getDefaultOption(errorId))
                .listener(new RequestListener<Drawable>(){
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        Observable.just("")
//                                .flatMap((Function<String, ObservableSource<String>>) s -> {
//                                    File file = ImgLoadUtils.getImgCachePath(ConfigUtils.getAppCtx(), model, new RequestOptions().onlyRetrieveFromCache(true));
//                                    if(null != file) {
//                                        L.e("-----file", file.getPath());
//                                        FileUtils.deleteFileSafely(file);
//                                    }
//
//                                    return Observable.just("");
//                                })
//                                .subscribeOn(Schedulers.io())
//                                .subscribe(new RequestBaseObserver<String>(){
//                                    @Override
//                                    protected void onSuccess(String t) {
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                    }
//                                });
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }

    /**
     * 加载网络图片 带进度回调监听
     */
    public static void loadImgProgress(@NonNull Object url, @NonNull RequestOptions options,
                                       @NonNull ImageView imageView, ImgLoadCallBack<Drawable> callBack) {

        FileDownInterceptor.addListener(url.toString(), new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                L.e("glide", progress + "%");
                if (null != callBack) {
                    RequestUtils.runUiThread(new RequestUtils.OnRunUiThreadListener() {
                        @Override
                        public void onRun() {
                            callBack.onProgress(progress);
                        }
                    });
                }
            }
        });

        Glide.with(imageView.getContext())
                .asDrawable()
                .load(url)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        FileDownInterceptor.removeListener(url.toString());
                        if (null != callBack)
                            callBack.onLoadFailed(e, model, target, isFirstResource);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        FileDownInterceptor.removeListener(url.toString());
                        if (null != callBack)
                            callBack.onResourceReady(resource, model, target, dataSource, isFirstResource);
                        return false;
                    }
                })
                .into(imageView);
    }

}
