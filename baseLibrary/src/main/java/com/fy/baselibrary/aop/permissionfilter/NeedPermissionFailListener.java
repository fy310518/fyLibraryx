package com.fy.baselibrary.aop.permissionfilter;

/**
 * description TODO
 * Created by fangs on 2024/8/30 14:39.
 */
public interface NeedPermissionFailListener {
    /**
     * 有权限被授予时回调（部分或全部授予）
     * @param isAll       是否全部授予了
     */
    void noFail(boolean isAll);
}
