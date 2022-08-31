package com.fy.baselibrary.utils.config;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;

/**
 * description app 灰色模式 工具类
 * Created by fangs on 2022/8/31 17:48.
 */
public class GrayManager {
    private static Paint paint = null;
    private static ColorMatrix colorMatrix = null;

    static {
        paint = new Paint();
        colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f);  // 设置灰度效果；0为灰色，1为彩色。

        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }

    /**
     * 设置activity的 decorview 将整个页面改变色彩，也可以单独针对某个view做色彩改变
     * @param view
     */
    public static void setColorThemeMode(View view) {
        view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

}
