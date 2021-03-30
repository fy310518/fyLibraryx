package com.fy.baselibrary.rv.divider.helper;

/**
 * description 定义 recycleView Item 1、侧滑删除；2、拖动交换 接口
 * Created by fangs on 2021/3/25 14:23.
 */
public interface OnItemTouchCallbackListener {

    /**
     * 当某个Item被滑动删除的时候
     * @param adapterPosition item的position
     */
    void onSwiped(int adapterPosition);

    /**
     * 当两个Item位置互换的时候被回调
     * @param srcPosition    拖拽的item的position
     * @param targetPosition 目的地的Item的position
     * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
     */
    boolean onMove(int srcPosition, int targetPosition);

}
