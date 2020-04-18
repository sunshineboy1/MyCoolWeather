package com.pfj.coolweather.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    //网络请求
    public static void sendOkHttpRequest(String urlStr, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5,TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(urlStr).build();
        client.newCall(request).enqueue(callback);
    }
}
