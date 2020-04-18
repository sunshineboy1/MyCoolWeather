package com.pfj.coolweather.util;

import android.widget.TabHost;
import android.widget.Toast;

import com.pfj.coolweather.application.MyApplication;

public class ToastUtil {

    public static void shortToast(String msg){
        Toast.makeText(MyApplication.getContext(),msg, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(String msg){
        Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
