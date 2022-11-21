package com.fy.baselibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.fy.baselibrary.R;


/**
 * 圆形加载进度
 * Created by huangyi on 2017/6/25.
 */
public class RoundProgressBar extends View {

    /** 自定义变量 */
    private int mTextSize;
    private int mTextColor;
    private int mCircleWidth;
    private int mBgColor;
    private int mCurrentColor;
    private int mLoadSpeed;
    private float mCurrentProgress;

    private String mContent = "0%";
    private Rect mBounds;
    private Paint mPaint;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundProgressBar, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = array.getIndex(i);
            if (index == R.styleable.RoundProgressBar_textSizeRound) {
                // 默认设置为16sp，TypeValue也可以把sp转化为px
                mTextSize = array.getDimensionPixelSize(index, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
            } else if (index == R.styleable.RoundProgressBar_textColorRound) {

                // 默认设置为黑色
                mTextColor = array.getColor(index, Color.BLACK);
            } else if (index == R.styleable.RoundProgressBar_bgColorRound) {
                mBgColor = array.getColor(index, Color.BLACK);
            } else if (index == R.styleable.RoundProgressBar_circleWidthRound) {
                mCircleWidth = array.getDimensionPixelSize(index, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()
                ));
            } else if (index == R.styleable.RoundProgressBar_currentColorRound) {
                mCurrentColor = array.getColor(index, Color.BLACK);
            } else if (index == R.styleable.RoundProgressBar_loadSpeedRound) {
                mLoadSpeed = array.getInt(index, 10);
            }
        }
        array.recycle();
        init();
    }

    private void init() {
        mBounds = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (mCurrentProgress < 360) {
//                    mCurrentProgress = mCurrentProgress + 1;
//                    mContent = Math.round((mCurrentProgress / 360) * 100) + "%";
//                    postInvalidate();
//                    try {
//                        Thread.sleep(mLoadSpeed);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 设置画笔的属性
         */
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);

        /**
         * 绘制圆环背景
         */
        int xPoint = getWidth() / 2;//获取圆心x的坐标
        int radius = xPoint - mCircleWidth;//获取圆心的半径
        canvas.drawCircle(xPoint, xPoint, radius, mPaint);

        /**
         * 绘制圆环
         */
        mPaint.setColor(mCurrentColor);
        RectF oval = new RectF(xPoint - radius, xPoint - radius, radius + xPoint, radius + xPoint);
        canvas.drawArc(oval, -90, mCurrentProgress, false, mPaint);

        /**
         * 绘制当前进度文本
         */
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mContent, 0, mContent.length(), mBounds);
        canvas.drawText(mContent, xPoint - mBounds.width() / 2, xPoint + mBounds.height() / 2, mPaint);
    }



    public void setProgress(int progress){
        if (progress < 100){
            mCurrentProgress = (float) (progress * 3.6);
            mContent = progress + "%";

            invalidate();
        }
    }

}
