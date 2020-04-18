package com.pfj.coolweather.util;

import android.util.Log;

public class LogUtil {


    private static final int VERSOVER = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;

    private static final int level = 1;

    public static void v(String TAG,String log){
        if (level <= VERSOVER ){
            Log.v(TAG,log);
        }
    }

    public static void d(String TAG,String log){
        if (level <= DEBUG ){
            Log.d(TAG,log);
        }
    }

    public static void i(String TAG,String log){
        if (level <= INFO ){
            Log.i(TAG,log);
        }
    }

    public static void w(String TAG,String log){
        if (level <= WARN ){
            Log.w(TAG,log);
        }
    }

    public static void e(String TAG,String log){
        if (level <= ERROR ){
            Log.e(TAG,log);
        }
    }

    public static void e(String log){
        if (level <= ERROR ){
            Log.e("pfj===",log);
        }
    }
}
