package com.fy.baselibrary.utils.drawable.combinebitmap;

import android.content.Context;
import android.graphics.Bitmap;

import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.FileUtils;
import com.fy.baselibrary.utils.drawable.BitmapUtils;
import com.fy.baselibrary.utils.imgload.ImgLoadUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class CombineHelper {

    public static CombineHelper init() {
        return SingletonHolder.instance;
    }


    private CombineHelper() {}

    private static class SingletonHolder {
        private static final CombineHelper instance = new CombineHelper();
    }

    /**
     * 通过url加载
     * @param combineBitmapBuilder
     */
    private String loadByUrls(final CombineBitmapBuilder combineBitmapBuilder) throws ExecutionException, InterruptedException, IOException {
        Context context = ConfigUtils.getAppCtx();

        File file;
        FileInputStream fileInputStream;
        Bitmap[] compressedBitmaps = new Bitmap[combineBitmapBuilder.count];
        //循环 combineBitmapBuilder.urls 获取 bitmap 数组，调用 setBitmap(combineBitmapBuilder, bitmaps);
        for (int i = 0; i < combineBitmapBuilder.urls.size(); i++) {
            if (FileUtils.fileIsExist(combineBitmapBuilder.urls.get(i))) file = new File(combineBitmapBuilder.urls.get(i));
            else file = ImgLoadUtils.getImgCachePath(context, combineBitmapBuilder.urls.get(i), null);

            fileInputStream = new FileInputStream(file);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            compressedBitmaps[i] = CompressHelper.getInstance().compressDescriptor(fileDescriptor, combineBitmapBuilder.subSize, combineBitmapBuilder.subSize);

            fileInputStream.close();
        }

        setBitmap(combineBitmapBuilder, compressedBitmaps);

        return combineBitmapBuilder.filePath;
    }

    /**
     * 通过图片的资源id、bitmap加载
     * @param combineBitmapBuilder
     */
    private String loadByResBitmaps(CombineBitmapBuilder combineBitmapBuilder) {
        Context context = ConfigUtils.getAppCtx();
        int subSize = combineBitmapBuilder.subSize;
        Bitmap[] compressedBitmaps = new Bitmap[combineBitmapBuilder.count];

        for (int i = 0; i < combineBitmapBuilder.count; i++) {
            if (combineBitmapBuilder.resourceIds != null) {
                compressedBitmaps[i] = CompressHelper.getInstance().compressResource(context.getResources(), combineBitmapBuilder.resourceIds[i], subSize, subSize);
            } else if (combineBitmapBuilder.bitmaps != null) {
                compressedBitmaps[i] = CompressHelper.getInstance().compressResource(combineBitmapBuilder.bitmaps[i], subSize, subSize);
            }
        }
        setBitmap(combineBitmapBuilder, compressedBitmaps);

        return combineBitmapBuilder.filePath;
    }

    public String load(CombineBitmapBuilder combineBitmapBuilder) {
        if (combineBitmapBuilder.urls != null) {
            try {
                loadByUrls(combineBitmapBuilder);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadByResBitmaps(combineBitmapBuilder);
        }

        return combineBitmapBuilder.filePath;
    }

    private void setBitmap(final CombineBitmapBuilder b, Bitmap[] bitmaps) {
        Bitmap result = b.layoutManager.combineBitmap(b.size, b.subSize, b.gap, b.gapColor, bitmaps);
        BitmapUtils.saveBitmap(result, b.filePath);
    }

}
