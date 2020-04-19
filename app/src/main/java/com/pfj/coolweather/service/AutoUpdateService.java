package com.pfj.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import com.pfj.coolweather.gson.Weather;
import com.pfj.coolweather.util.HttpUtil;
import com.pfj.coolweather.util.SpUtil;
import com.pfj.coolweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather2Sp();//更新天气信息到Sp
        updateBingPic2Sp();//更新图片link到Sp
        //设置闹铃，8小时执行一次onStartCommand方法
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 1000 * 60 * 60 * 8;//8小时
        long triggertime = SystemClock.elapsedRealtime() + time;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggertime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather2Sp() {
        final String wearherStr = SpUtil.getString("weather");//服务器返回数据
        if (wearherStr!=null){
            Weather weather = Utility.handleWeatherInfo(wearherStr);//服务器返回数据转成对象
            String weatherId = weather.basic.id;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=902cab799a27450b96378a7a76abec99";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String result = response.body().string();
                    Weather weather = Utility.handleWeatherInfo(result);
                    if (weather!=null && weather.status.equals("ok")) {
                        SpUtil.putString("weather", result);
                    }
                }
            });
        }

    }

    private void updateBingPic2Sp() {
        String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(picUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String picLink = response.body().string();
                if (!TextUtils.isEmpty(picLink)){
                    SpUtil.putString("bing_pic",picLink);
                }
            }
        });


    }
}
