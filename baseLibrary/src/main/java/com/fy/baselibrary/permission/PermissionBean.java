package com.fy.baselibrary.permission;

import android.view.Gravity;

/**
 * description 对标 NeedPermission 注解
 * Created by fangs on 2024/8/30 17:24.
 */
public class PermissionBean {
    /**
     * 请求权限列表
     */
    String[] value = new String[]{};

    /**
     * 第一次拒绝必要权限的提示信息
     */
    String firstRefuseMsg = "";

    /**
     * 永久拒绝必要权限的提示信息
     */
    String alwaysRefuseMsg = "";

    /**
     * 存在被拒绝的权限时，是否继续执行
     * @return 默认 false，不继续执行
     */
    boolean isRun = false;

    /**
     * 危险权限请求失败，显示弹窗位置
     */
    int gravity = Gravity.BOTTOM;

    /**
     * 特殊权限被拒绝后，弹窗 是否显示 取消按钮
     */
    boolean isShowCancelBtn = true;


    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public String getFirstRefuseMsg() {
        return firstRefuseMsg == null ? "" : firstRefuseMsg;
    }

    public void setFirstRefuseMsg(String firstRefuseMsg) {
        this.firstRefuseMsg = firstRefuseMsg;
    }

    public String getAlwaysRefuseMsg() {
        return alwaysRefuseMsg == null ? "" : alwaysRefuseMsg;
    }

    public void setAlwaysRefuseMsg(String alwaysRefuseMsg) {
        this.alwaysRefuseMsg = alwaysRefuseMsg;
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public boolean isShowCancelBtn() {
        return isShowCancelBtn;
    }

    public void setShowCancelBtn(boolean showCancelBtn) {
        isShowCancelBtn = showCancelBtn;
    }
}
