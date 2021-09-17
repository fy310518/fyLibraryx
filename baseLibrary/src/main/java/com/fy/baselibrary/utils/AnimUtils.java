package com.fy.baselibrary.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.application.mvvm.BaseViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * UI OR 动画 工具类
 * Created by fangs on 2017/10/30.
 */
public class AnimUtils {
    public static final String TAG = "CircleMenu";
    public static final int radius1 = 500;

    /**
     * 省略号动画
     * @return ValueAnimator: 为了在activity onDestroy 时候 调用 valueAnimator.cancel(); 避免内存泄漏
     */
    public static ValueAnimator setTxtEllipsisAnim(TextView txt, @StringRes int id) {
        String[] scoreText = {"     ", ".    ", ". .  ", ". . ."};
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 4).setDuration(1500);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int i = (int) animation.getAnimatedValue();
                txt.setText(ResUtils.getReplaceStr(id, scoreText[i % scoreText.length]));
            }
        });
        valueAnimator.start();

        return valueAnimator;
    }

    /**
     * 箭头的动画
     *
     * @param iv_arrow 箭头View
     * @param isExpand 当前状态是否 收起
     */
    public static void doArrowAnim(View iv_arrow, boolean isExpand) {
        if (isExpand) {
            // 当前是收起，箭头由上变为下
            ObjectAnimator.ofFloat(iv_arrow, "rotation", -180, 0).start();
        } else {
            // 当前是展开，箭头由下变为上
            ObjectAnimator.ofFloat(iv_arrow, "rotation", 0, 180).start();
        }
    }

    /**
     * 关闭扇形菜单
     *
     * @param buttonList 控件列表
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static void closeSectorMenu(List<RadioButton> buttonList) {
        for (int i = 0; i < buttonList.size(); i++) {
            PointF point = new PointF();
            int avgAngle = (180 / (buttonList.size() + 1));
            int angle = avgAngle * (i + 1);
            Log.d(TAG, "angle=" + angle);
            point.x = (float) Math.cos(angle * (Math.PI / 180)) * radius1;
            point.y = (float) -Math.sin(angle * (Math.PI / 180)) * radius1;
            Log.d(TAG, point.toString());

            ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(buttonList.get(i), "translationX", point.x, 0);
            ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(buttonList.get(i), "translationY", point.y, 0);
            ObjectAnimator objectAnimatorA = ObjectAnimator.ofFloat(buttonList.get(i), "alpha", 1, 0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(500);
            animatorSet.play(objectAnimatorX).with(objectAnimatorY).with(objectAnimatorA);
            animatorSet.start();
        }
    }

    /**
     * 显示半圆弧 菜单
     *
     * @param buttonList 控件列表
     */
    public static void showSemicircleMenu(List<RadioButton> buttonList) {
        /***第一步，遍历所要展示的菜单ImageView*/
        for (int i = 0; i < buttonList.size(); i++) {
            PointF point = new PointF();
            /***第二步，根据菜单个数计算每个菜单之间的间隔角度*/
            int avgAngle = (180 / (buttonList.size() + 1));
            /**第三步，根据间隔角度计算出每个菜单相对于水平线起始位置的真实角度**/
            int angle = avgAngle * (i + 1);
            Log.d(TAG, "angle=" + angle);
            /**
             * 圆点坐标：(x0,y0)
             * 半径：r
             * 角度：a0
             * 则圆上任一点为：（x1,y1）
             * x1   =   x0   +   r   *   cos(ao   *   3.14   /180   )
             * y1   =   y0   +   r   *   sin(ao   *   3.14   /180   )
             */
            /**第四步，根据每个菜单真实角度计算其坐标值**/
            point.x = (float) Math.cos(angle * (Math.PI / 180)) * radius1;
            point.y = (float) -Math.sin(angle * (Math.PI / 180)) * radius1;
            Log.d(TAG, point.toString());

            /**第五步，根据坐标执行位移动画**/
            /**
             * 第一个参数代表要操作的对象
             * 第二个参数代表要操作的对象的属性
             * 第三个参数代表要操作的对象的属性的起始值
             * 第四个参数代表要操作的对象的属性的终止值
             */
            ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(buttonList.get(i), "translationX", 0, point.x);
            ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(buttonList.get(i), "translationY", 0, point.y);
            ObjectAnimator objectAnimatorA = ObjectAnimator.ofFloat(buttonList.get(i), "alpha", 0, 1);
            /**动画集合，用来编排动画**/
            AnimatorSet animatorSet = new AnimatorSet();
            /**设置动画时长**/
            animatorSet.setDuration(500);
            /**设置同时播放x方向的位移动画和y方向的位移动画**/
            animatorSet.play(objectAnimatorX).with(objectAnimatorY).with(objectAnimatorA);
            /**开始播放动画**/
            animatorSet.start();
        }
    }

    /**
     * 根据 activity OR fragment 对象，获取对应的 ViewModel
     * @param obj
     * @param <BVM>
     * @return ViewModel
     */
    public static <BVM extends BaseViewModel> BVM createViewModel(Object obj) {
        Class modelClass;

        Type[] types = obj.getClass().getGenericInterfaces();
        if (types.length > 0 && types[0] instanceof ParameterizedType){
            modelClass = (Class) ((ParameterizedType) types[0]).getActualTypeArguments()[0];
        } else {
            //如果没有指定泛型参数，则默认使用BaseViewModel
            modelClass = BaseViewModel.class;
        }

        if (obj instanceof FragmentActivity){
            return (BVM) new ViewModelProvider((FragmentActivity) obj).get(modelClass);
        } else if(obj instanceof Fragment){
            return (BVM) new ViewModelProvider(((Fragment)obj).getActivity()).get(modelClass);
        } else {
            return null;
        }
    }

    /**
     * activity 初始化 toolbar
     * @param activity
     */
    public static View initHead(Activity activity) {
        View titleBar = LayoutInflater.from(activity).inflate(R.layout.activity_head, null);
        //这里全局给Activity设置toolbar和title mate
        Toolbar toolbar = titleBar.findViewById(R.id.toolbar);

        if (ConfigUtils.isTitleCenter()) {
            toolbar.setTitle("");
            TextView toolbarTitle = titleBar.findViewById(R.id.toolbarTitle);
            toolbarTitle.setText(activity.getTitle());
            toolbarTitle.setTextColor(ResUtils.getColor(ConfigUtils.getTitleColor()));
            toolbarTitle.setVisibility(View.VISIBLE);
        } else {
            toolbar.setTitle(activity.getTitle());
        }

        if (activity instanceof AppCompatActivity) {
            AppCompatActivity act = (AppCompatActivity) activity;
            //设置导航图标要在setSupportActionBar方法之后
            act.setSupportActionBar(toolbar);
            if (ConfigUtils.isTitleCenter())
                act.getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏 toolbar 自带的标题view

            //在Toolbar左边显示一个返回按钮
            act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //替换toolbar 自带的返回按钮
            if (ConfigUtils.getBackImg() > 0) toolbar.setNavigationIcon(ConfigUtils.getBackImg());
            //设置返回按钮监听事件
            toolbar.setNavigationOnClickListener(v -> JumpUtils.exitActivity(act));
            if (ConfigUtils.getBgColor() > 0)
                toolbar.setBackgroundColor(ResUtils.getColor(ConfigUtils.getBgColor()));
        }

        return titleBar;
    }

    /**
     * fragment 初始化 toolbar
     * @param fragment
     * @param toolbar
     * @param title
     * @param listener
     */
    public static void setToolbar(Fragment fragment, Toolbar toolbar, String title, View.OnClickListener listener){
        ((AppCompatActivity) fragment.getActivity()).setSupportActionBar(toolbar);

        if (ConfigUtils.isTitleCenter()) {
            toolbar.setTitle("");
            ((AppCompatActivity) fragment.getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏 toolbar 自带的标题view
            TextView toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
            toolbarTitle.setText(title);
            toolbarTitle.setTextColor(ResUtils.getColor(ConfigUtils.getTitleColor()));
            toolbarTitle.setVisibility(View.VISIBLE);
        } else {
            toolbar.setTitle(title);
        }

        if (ConfigUtils.getBgColor() > 0)
            toolbar.setBackgroundColor(ResUtils.getColor(ConfigUtils.getBgColor()));

        if (null != listener){
            //在Toolbar左边显示一个返回按钮
            ((AppCompatActivity) fragment.getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //替换toolbar 自带的返回按钮
            if (ConfigUtils.getBackImg() > 0) toolbar.setNavigationIcon(ConfigUtils.getBackImg());

            toolbar.setNavigationOnClickListener(listener);
        }

        fragment.setHasOptionsMenu(true);//允许fragment 显示 menu
    }

}
