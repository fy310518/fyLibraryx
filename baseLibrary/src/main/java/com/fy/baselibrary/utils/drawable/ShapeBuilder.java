package com.fy.baselibrary.utils.drawable;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.ColorRes;

import com.fy.baselibrary.utils.ResUtils;

/**
 * 封装GradientDrawable替代用shape.xml，减小apk体积
 * Created by fangs on 2018/2/11.
 */
public class ShapeBuilder {
    private GradientDrawable drawable;

    private ShapeBuilder() {
        drawable = new GradientDrawable();
    }

    /**
     * 构建 ShapeBuilder 工具类
     * @return
     */
    public static ShapeBuilder create() {
        return new ShapeBuilder();
    }

    /**
     * 返回构建的drawable
     */
    public GradientDrawable build() {
        return drawable;
    }

    /**
     * 设置shape的type类型
     * @param type RECTANGLE,OVAL,LINE,RING
     */
    public ShapeBuilder Type(int type) {
        drawable.setShape(type);//默认是 矩形值为 【GradientDrawable.RECTANGLE】
        return this;
    }

    /**
     * 设置size
     * @param width  宽
     * @param height 高
     */
    public ShapeBuilder setSize(int width, int height) {
        drawable.setSize(width, height);
        return this;
    }

    /**
     * 实线描边
     * @param px    width,需要px值
     * @param color color值
     */
    public ShapeBuilder stroke(int px, @ColorRes int color) {
        drawable.setStroke(px, ResUtils.getColor(color));
        return this;
    }

    /**
     * 虚线描边
     * @param px        width,需要px值
     * @param color     color值
     * @param dashWidth dashWidth 横线的宽度
     * @param dashGap   dashGap 点与点间的距离
     */
    public ShapeBuilder stroke(int px, @ColorRes int color, int dashWidth, int dashGap) {

        drawable.setStroke(px, ResUtils.getColor(color), dashWidth, dashGap);
        return this;
    }

    /**
     * 背景颜色
     * @param color 颜色
     */
    public ShapeBuilder solid(@ColorRes int color) {
        drawable.setColor(ResUtils.getColor(color));
        return this;
    }

    /**
     * 圆角
     * @param px 四个角保持一致
     */
    public ShapeBuilder radius(float px) {
        drawable.setCornerRadius(px);
        return this;
    }

    /**
     * 圆角
     * @param topleft  左上
     * @param topright 右上
     * @param botleft  左下
     * @param botright 右下
     */
    public ShapeBuilder radius(float topleft, float topright, float botleft, float botright) {
        drawable.setCornerRadii(new float[]{topleft, topleft, topright, topright, botleft, botleft, botright, botright});
        return this;
    }


    /**
     * 设置 渐变角度和颜色 【同下】  注意 最后设置 渐变type，以及其它属性
     * @param angle
     * @param startColor
     * @param endColor
     * @return
     */
    public ShapeBuilder gradient(int angle, @ColorRes int startColor, @ColorRes int endColor) {
        gradient(angle, startColor, -1, endColor);
        return this;
    }

    /**
     * 渐变，设置角度(实质调用的Gradient(GradientDrawable.Orientation orientation, int startColor, int
     * centerColor, int endColor)方法)
     *  注意 最后设置 渐变type，以及其它属性
     * @param angle       角度，需要是45的整数倍
     * @param startColor  开始颜色
     * @param centerColor 中心颜色
     * @param endColor    结束颜色
     */
    public ShapeBuilder gradient(int angle, @ColorRes int startColor, int centerColor, @ColorRes int endColor) {
        angle = angle % 360;
        GradientDrawable.Orientation orientation = null;
        switch (angle) {
            case 0:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case 45:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case 90:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case 135:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case 180:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case 225:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case 270:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case 315:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
        }
        return gradientInit(orientation, startColor, centerColor, endColor);
    }

    /**
     * 重新构造 drawable
     */
    private ShapeBuilder gradientInit(GradientDrawable.Orientation orientation, @ColorRes int startColor, int centerColor, @ColorRes int endColor) {
        int[] colors;
        if (centerColor < 1) {
            colors = new int[]{ResUtils.getColor(startColor), ResUtils.getColor(endColor)};
        } else {
            colors = new int[]{ResUtils.getColor(startColor), ResUtils.getColor(centerColor), ResUtils.getColor(endColor)};
        }

        drawable = new GradientDrawable(orientation, colors);
        return this;
    }

    /**
     * 渐变type
     * @param type linear (default.)-LINEAR_GRADIENT
     *             circular-RADIAL_GRADIENT
     *             sweep-SWEEP_GRADIENT
     */
    public ShapeBuilder gradientType(int type) {
        drawable.setGradientType(type);
        return this;
    }

    /**
     * 这两个属性只有在type不为linear情况下起作用。
     * @param x 相对X的渐变位置
     * @param y 相对Y的渐变位置
     */
    public ShapeBuilder gradientCenter(float x, float y) {
        drawable.setGradientCenter(x, y);
        return this;
    }

    /**
     * 该属性只有在type="radial"有效
     * @param radius 渐变颜色的半径
     */
    public ShapeBuilder gradientRadius(float radius) {
        drawable.setGradientRadius(radius);
        return this;
    }



    public void setBackBg(View v){
        v.setBackground(drawable);
    }

}
