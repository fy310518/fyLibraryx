package com.fy.baselibrary.utils.drawable.combinebitmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Region;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;

import com.fy.baselibrary.R;
import com.fy.baselibrary.utils.drawable.combinebitmap.layout.DingLayoutManager;
import com.fy.baselibrary.utils.drawable.combinebitmap.layout.ILayoutManager;
import com.fy.baselibrary.utils.drawable.combinebitmap.layout.WechatLayoutManager;
import com.fy.baselibrary.utils.drawable.combinebitmap.listener.OnSubItemClickListener;
import com.fy.baselibrary.utils.drawable.combinebitmap.region.DingRegionManager;
import com.fy.baselibrary.utils.drawable.combinebitmap.region.IRegionManager;
import com.fy.baselibrary.utils.drawable.combinebitmap.region.WechatRegionManager;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.DensityUtils;
import com.fy.baselibrary.utils.FileUtils;

import java.util.List;

/**
 * 组合位图
 */
public class CombineBitmapBuilder {

    public String filePath;
    public ImageView imageView;
    public int size = 360; // 最终生成bitmap的尺寸
    public int gap = 2; // 每个小bitmap之间的距离
    public @ColorInt int gapColor = Color.parseColor("#E8E8E8"); // 间距的颜色
    public int count; // 要加载的资源数量
    public int subSize; // 单个bitmap的尺寸

    public ILayoutManager layoutManager = new WechatLayoutManager(); // bitmap的组合样式

    public Region[] regions;
    public OnSubItemClickListener subItemClickListener; // 单个bitmap点击事件回调

    public Bitmap[] bitmaps;
    public int[] resourceIds;
    public List<String> urls;


    public CombineBitmapBuilder(String fileName) {
        String filePath = FileUtils.folderIsExists(FileUtils.headImg, ConfigUtils.getType()).getPath();
        this.filePath = FileUtils.getTempFile(fileName, filePath).getPath();
    }

    public CombineBitmapBuilder setImageView(ImageView imageView) {
        this.imageView = imageView;
        return this;
    }

    public CombineBitmapBuilder setSize(int size) {
        this.size = DensityUtils.dp2px(size);
        return this;
    }

    public CombineBitmapBuilder setGap(int gap) {
        this.gap = DensityUtils.dp2px(gap);
        return this;
    }

    public CombineBitmapBuilder setGapColor(@ColorInt int gapColor) {
        this.gapColor = gapColor;
        return this;
    }

    public CombineBitmapBuilder setLayoutManager(ILayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        return this;
    }

    public CombineBitmapBuilder setOnSubItemClickListener(OnSubItemClickListener subItemClickListener) {
        this.subItemClickListener = subItemClickListener;
        return this;
    }

    public CombineBitmapBuilder setBitmaps(Bitmap... bitmaps) {
        this.bitmaps = bitmaps;
        this.count = bitmaps.length;
        return this;
    }

    public CombineBitmapBuilder setUrls(List<String> urls) {
        this.urls = urls;
        this.count = urls.size();
        return this;
    }

    public CombineBitmapBuilder setResourceIds(int... resourceIds) {
        this.resourceIds = resourceIds;
        this.count = resourceIds.length;
        return this;
    }

    public String build() {
        if (FileUtils.fileIsExist(filePath)) { //文件存在
            return filePath;
        } else {
            subSize = getSubSize(size, gap, layoutManager, count);
            initRegions();

            return CombineHelper.init().load(this);
        }
    }

    /**
     * 根据最终生成bitmap的尺寸，计算单个bitmap尺寸
     *
     * @param size
     * @param gap
     * @param layoutManager
     * @param count
     * @return
     */
    private int getSubSize(int size, int gap, ILayoutManager layoutManager, int count) {
        int subSize = 0;
        if (layoutManager instanceof DingLayoutManager) {
            subSize = size;
        } else if (layoutManager instanceof WechatLayoutManager) {
            if (count < 2) {
                subSize = size;
            } else if (count < 5) {
                subSize = (size - 3 * gap) / 2;
            } else if (count < 10) {
                subSize = (size - 4 * gap) / 3;
            }
        } else {
            throw new IllegalArgumentException("Must use DingLayoutManager or WechatRegionManager!");
        }
        return subSize;
    }

    /**
     * 初始化RegionManager
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initRegions() {
        if (null == imageView) return;

        IRegionManager regionManager;

        if (layoutManager instanceof DingLayoutManager) {
            regionManager = new DingRegionManager();
        } else if (layoutManager instanceof WechatLayoutManager) {
            regionManager = new WechatRegionManager();
        } else {
            throw new IllegalArgumentException("Must use DingLayoutManager or WechatRegionManager!");
        }

        regions = regionManager.calculateRegion(size, subSize, gap, count);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            int initIndex = -1;
            int currentIndex = -1;
            Point point = new Point();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                point.set((int) event.getX(), (int) event.getY());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initIndex = getRegionIndex(point.x, point.y);
                        currentIndex = initIndex;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        currentIndex = getRegionIndex(point.x, point.y);
                        break;
                    case MotionEvent.ACTION_UP:
                        currentIndex = getRegionIndex(point.x, point.y);
                        if (subItemClickListener != null && currentIndex != -1 && currentIndex == initIndex) {
                            subItemClickListener.onSubItemClick(currentIndex);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        initIndex = currentIndex = -1;
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 根据触摸点计算对应的Region索引
     *
     * @param x
     * @param y
     * @return
     */
    private int getRegionIndex(int x, int y) {
        for (int i = 0; i < regions.length; i++) {
            if (regions[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

}
