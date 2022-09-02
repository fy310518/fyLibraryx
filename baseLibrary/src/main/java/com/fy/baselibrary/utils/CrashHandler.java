package com.fy.baselibrary.utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * describe: 捕获崩溃异常
 * Created by fangs on 2020/2/27 16:27.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "TEST";
    // CrashHandler 实例
    private static CrashHandler INSTANCE = new CrashHandler();
    // 程序的 Context 对象
    private Context mContext;
    // 系统默认的 UncaughtException 处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    // 用来显示Toast中的信息
    private static String error = "";

    /**
     * 保证只有一个 CrashHandler 实例
     */
    private CrashHandler() {
    }

    /**
     * 获取 CrashHandler 实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        Log.d("TEST", "Crash:init");
    }

    /**
     * 当 UncaughtException 发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
            Log.d("TEST", "defalut");
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            // mDefaultHandler.uncaughtException(thread, ex);
            System.exit(0);
        }
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     * @param ex
     * @return true：如果处理了该异常信息；否则返回 false
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用 Toast 来显示异常信息
        new Thread() {
            @Override
            public void run() {
                // 收集设备参数信息
//                StringBuffer sb1 = AppUtils.collectPhoneInfo(new StringBuffer());
                StringBuffer sb = AppUtils.dumpExceptionToSDCard(ex, new StringBuffer());
//                // 保存日志文件
                FileUtils.fileToInputContent("crash", "crash.txt", sb.toString());
                Looper.prepare();
                Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        return true;
    }

}
