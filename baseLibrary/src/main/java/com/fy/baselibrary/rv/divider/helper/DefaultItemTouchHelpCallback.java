package com.fy.baselibrary.rv.divider.helper;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fy.baselibrary.R;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.media.PlayUtils;

/**
 * description 定义 recycleView Item 1、侧滑删除；2、拖动交换 帮助类
 * Created by fangs on 2021/3/25 14:27.
 */
public class DefaultItemTouchHelpCallback extends ItemTouchHelper.Callback {

    /** Item操作的回调 */
    private OnItemTouchCallbackListener onItemTouchCallbackListener;
    /** 是否可以拖拽 */
    private boolean isCanDrag = false;
    /** 是否可以被滑动 */
    private boolean isCanSwipe = false;

    public DefaultItemTouchHelpCallback(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置Item操作的回调，去更新UI和数据源
     *
     * @param onItemTouchCallbackListener
     */
    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置是否可以被拖拽
     * @param canDrag 是true，否false
     */
    public void setDragEnable(boolean canDrag) {
        isCanDrag = canDrag;
    }

    /**
     * 设置是否可以被滑动
     * @param canSwipe 是true，否false
     */
    public void setSwipeEnable(boolean canSwipe) {
        isCanSwipe = canSwipe;
    }

    /**
     * 当Item被长按的时候是否可以被拖拽
     * 开启长按拖拽功能，默认为true
     * @return 如果设置为 false，手动开启，调用startDrag()
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }

    /**
     * Item是否可以被滑动(H：左右滑动，V：上下滑动)
     * 开始滑动功能，默认为true
     * @return 如果设置为 false，手动开启，调用startSwipe()
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return isCanSwipe;
    }

    @Override//当用户拖拽或者滑动Item的时候需要我们告诉系统滑动或者拖拽的方向
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // flag如果值是0，相当于这个功能被关闭
        int dragFlag = 0;
        int swipeFlag = 0;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeFlag = 0;
            return makeMovementFlags(dragFlag, swipeFlag);
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();

            // 为了方便理解，相当于分为横着的ListView和竖着的ListView
            if (orientation == LinearLayoutManager.HORIZONTAL) {// 如果是横向的布局
                swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (orientation == LinearLayoutManager.VERTICAL) {// 如果是竖向的布局，相当于ListView
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFlag, swipeFlag);
        }
        return 0;
    }

    @Override//当Item被拖拽的时候被回调
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder srcViewHolder, RecyclerView.ViewHolder target) {
        //得到当拖拽的viewHolder的Position
        int fromPosition = srcViewHolder.getAdapterPosition();
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getAdapterPosition();

        if (onItemTouchCallbackListener != null) {
            return onItemTouchCallbackListener.onMove(srcViewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return false;
    }

    @Override //侧滑删除可以使用；
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (onItemTouchCallbackListener != null) {
            onItemTouchCallbackListener.onSwiped(viewHolder.getAdapterPosition());
        }
    }

    /**
     * 长按选中Item的时候开始调用
     * 长按高亮
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(ResUtils.getColor(R.color.lblue));
            //获取系统震动服务 震动70毫秒
            PlayUtils.vibrator(false, new long[]{500, 200, 500, 200});
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 手指松开的时候还原高亮
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(0);

        recyclerView.getAdapter().notifyDataSetChanged();  //完成拖动后刷新适配器，这样拖动后删除就不会错乱
    }
}
