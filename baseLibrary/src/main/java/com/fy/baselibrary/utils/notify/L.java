package com.fy.baselibrary.utils.notify;

import android.util.Log;

import com.fy.baselibrary.application.ioc.ConfigUtils;

/**
 * Log统一管理类
 * Created by fangs on 2017/3/1.
 */
public class L {

    private L() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = ConfigUtils.isDEBUG();// 是否需要打印bug
    private static final String TAG = "Log";

    private static int MAX_LENGTH = 4000;

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug) {
            String[] info = getAutoJumpLogInfos();
            int strLength = msg.length();
            int start = 0;
            int end = MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.i(TAG, info[1] + info[2] + " ---> " + msg.substring(start, end));
                    start = end;
                    end = end + MAX_LENGTH;
                } else {
                    Log.i(TAG, info[1] + info[2] + " ---> " + msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            String[] info = getAutoJumpLogInfos();
            int strLength = msg.length();
            int start = 0;
            int end = MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.d(TAG, info[1] + info[2] + " ---> " + msg.substring(start, end));
                    start = end;
                    end = end + MAX_LENGTH;
                } else {
                    Log.d(TAG, info[1] + info[2] + " ---> " + msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            String[] info = getAutoJumpLogInfos();
            int strLength = msg.length();
            int start = 0;
            int end = MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.e(TAG, info[1] + info[2] + " ---> " + msg.substring(start, end));
                    start = end;
                    end = end + MAX_LENGTH;
                } else {
                    Log.e(TAG, info[1] + info[2] + " ---> " + msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void v(String msg) {
        if (isDebug) {
            String[] info = getAutoJumpLogInfos();
            int strLength = msg.length();
            int start = 0;
            int end = MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.v(TAG, info[1] + info[2] + " ---> " + msg.substring(start, end));
                    start = end;
                    end = end + MAX_LENGTH;
                } else {
                    Log.v(TAG, info[1] + info[2] + " ---> " + msg.substring(start, strLength));
                    break;
                }
            }
        }
    }


    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug) {
            String[] info = getAutoJumpLogInfos();
            int strLength = msg.length();
            int start = 0;
            int end = MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.i(tag, info[1] + info[2] + " ---> " + msg.substring(start, end));
                    start = end;
                    end = end + MAX_LENGTH;
                } else {
                    Log.i(tag, info[1] + info[2] + " ---> " + msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            String[] info = getAutoJumpLogInfos();
            int strLength = msg.length();
            int start = 0;
            int end = MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.d(tag, info[1] + info[2] + " ---> " + msg.substring(start, end));
                    start = end;
                    end = end + MAX_LENGTH;
                } else {
                    Log.d(tag, info[1] + info[2] + " ---> " + msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            String[] info = getAutoJumpLogInfos();
            int strLength = msg.length();
            int start = 0;
            int end = MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
//                    Log.e(tag, info[1] + info[2] + " ---> " + msg.substring(start, end));
                    Log.e(tag, msg.substring(start, end));
                    start = end;
                    end = end + MAX_LENGTH;
                } else {
                    Log.e(tag, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            String[] info = getAutoJumpLogInfos();
            int strLength = msg.length();
            int start = 0;
            int end = MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.v(tag, info[1] + info[2] + " ---> " + msg.substring(start, end));
                    start = end;
                    end = end + MAX_LENGTH;
                } else {
                    Log.v(tag, info[1] + info[2] + " ---> " + msg.substring(start, strLength));
                    break;
                }
            }
        }
    }


    /**
     * 获取打印信息所在方法名，行号等信息
     */
    private static String[] getAutoJumpLogInfos() {
        String[] infos = new String[]{"", "", ""};
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        infos[0] = elements[4].getClassName().substring(elements[4].getClassName().lastIndexOf(".") + 1);
        infos[1] = elements[4].getMethodName();
        infos[2] = "(" + elements[4].getFileName() + ":" + elements[4].getLineNumber() + ")";
        return infos;
    }

}
