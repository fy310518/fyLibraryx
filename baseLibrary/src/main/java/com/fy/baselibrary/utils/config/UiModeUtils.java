package com.fy.baselibrary.utils.config;

import android.app.UiModeManager;
import android.content.Context;

import com.fy.baselibrary.application.ioc.ConfigUtils;

/**
 * description TODO
 * Created by fangs on 2024/4/15 11:15.
 */
public class UiModeUtils {

    /**
     * 判断当前是否深色模式
     *
     * @return 深色模式返回 true，否则返回false
     */
    public static boolean isDarkThemeOn() {
        UiModeManager uiModeManager = (UiModeManager) ConfigUtils.getAppCtx().getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null) {
            int currentNightMode = uiModeManager.getNightMode();
            return currentNightMode == UiModeManager.MODE_NIGHT_YES; // 深色模式
        }
        return false;
    }
}
