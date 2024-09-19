package com.fy.baselibrary.rv;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

/**
 * description TODO
 * Created by fangs on 2024/9/19 12:29.
 */
public class CmRecyclerView extends RecyclerView {

    private static final float FLING_SCALE_DOWN_FACTOR = 0.5f; // 减速因子
    private int FLING_MAX_VELOCITY = 3000; // 最大顺时滑动速度
    private static boolean mEnableLimitVelocity = true; // 最大顺时滑动速度

    public CmRecyclerView(Context context) {
        super(context);
    }

    public CmRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CmRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean fling(int velocityX, int velocityY) {
        if (mEnableLimitVelocity) {
            velocityX = solveVelocity(velocityX);
            velocityY = solveVelocity(velocityY);
        }
        return super.fling(velocityX, velocityY);
    }

    private int solveVelocity(int velocity) {
        if (velocity > 0) {
            return Math.min(velocity, FLING_MAX_VELOCITY);
        } else {
            return Math.max(velocity, -FLING_MAX_VELOCITY);
        }
    }

    public void setFlingMaxVelocity(int maxVelocity){
        this.FLING_MAX_VELOCITY = maxVelocity;
    }

}
