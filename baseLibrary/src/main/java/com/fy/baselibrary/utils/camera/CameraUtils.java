package com.fy.baselibrary.utils.camera;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.widget.Toast;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.utils.notify.T;

/**
 * DESCRIPTION：相机 相关工具类
 * Created by fangs on 2019/7/4 16:05.
 */
public class CameraUtils {

    /**
     * 打开手电筒
     */
    public static void openFlashLight(Camera camera) {
        if (null == camera) {
            return;
        }

        PackageManager pm = ConfigUtils.getAppCtx().getPackageManager();
        //判断系统是否 支持 指定的 功能（如：相机，蓝牙，闪光灯等 硬件功能）
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            T.show(R.string.systemNoTorch, -1);
        } else {
            Camera.Parameters parameter = camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
            camera.startPreview();
        }

    }

    /**
     * 关闭手电筒
     */
    public static void closeFlashLight(Camera camera) {
        if (null == camera) {
            return;
        }

        PackageManager pm = ConfigUtils.getAppCtx().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            T.show(R.string.systemNoTorch, -1);
        } else {
            Camera.Parameters parameter = camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameter);
            camera.startPreview();
        }
    }


}
