package com.pfj.coolweather.application;

import android.content.Context;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
