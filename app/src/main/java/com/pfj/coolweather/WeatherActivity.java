package com.pfj.coolweather;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.pfj.coolweather.gson.DailyForecast;
import com.pfj.coolweather.gson.Weather;
import com.pfj.coolweather.util.HttpUtil;
import com.pfj.coolweather.util.LogUtil;
import com.pfj.coolweather.util.SpUtil;
import com.pfj.coolweather.util.ToastUtil;
import com.pfj.coolweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private TextView titleCounty;
    private TextView titleTime;
    private TextView nowTem;
    private TextView nowInfo;
    private LinearLayout forecastLayout;
    private TextView aqi;
    private TextView pm25;
    private TextView tvComfort;
    private TextView tvCarwash;
    private TextView tvSport;

    private ImageView ivBing;

    private SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示（无状态栏）
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**
         * 5.0以上 沉浸式状态栏
         */
        if (Build.VERSION.SDK_INT >= 21) {
            //使UI显示到状态栏
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个flag contentView才能延伸到状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //状态栏覆盖在contentView上面，设置透明使contentView的背景透出来
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        init();
        String weatherInfo = SpUtil.getString("weather");
        if (weatherInfo != null) {
            Weather weather = Utility.handleWeatherInfo(weatherInfo);
            mWeatherId = weather.basic.id;
            showWeatherInfo(weather);
        } else {
            mWeatherId = getIntent().getStringExtra("weather_id");
            scrollView.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        //必应bG
        String picLink = SpUtil.getString("bing_pic");
        if (picLink != null) {
            Glide.with(this).load(picLink).into(ivBing);
        } else {
            loadBingPic();
        }
        //下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
    }

    //加载必应背景图片
    private void loadBingPic() {
        String bingPicUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(bingPicUrl, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("加载背景图片失败");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                final String result = response.body().string();
                SpUtil.putString("bing_pic", result);//sp存储背景图片链接
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(result).into(ivBing);
                    }
                });
            }
        });
    }

    //请求天气信息
    private void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=902cab799a27450b96378a7a76abec99";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.shortToast("获取天气数据失败！！！");
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                final String result = response.body().string();
                final Weather weather = Utility.handleWeatherInfo(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather.status.equals("ok")) {
                            SpUtil.putString("weather", result);
                            showWeatherInfo(weather);
                            mWeatherId = weather.basic.id;
                        } else {
                            ToastUtil.shortToast("onResponse 获取天气数据失败");
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        ToastUtil.shortToast("已获取最新天气信息");
                    }
                });
            }
        });
    }

    /*显示天气信息*/
    private void showWeatherInfo(Weather weather) {
        titleCounty.setText(weather.basic.city);
        titleTime.setText("更新于：" + weather.basic.update.loc.split(" ")[1]);
        nowTem.setText(weather.now.tem + "℃");
        nowInfo.setText(weather.now.cond.txt);
        forecastLayout.removeAllViews();
        for (DailyForecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forcast_item, forecastLayout, false);
            TextView tvDate = view.findViewById(R.id.tv_date);
            TextView tvInfo = view.findViewById(R.id.tv_info);
            TextView tvMax = view.findViewById(R.id.tv_max);
            TextView tvMin = view.findViewById(R.id.tv_min);
            tvDate.setText(forecast.date);
            tvInfo.setText(forecast.cond.info);
            tvMax.setText("最高" + forecast.tmp.max + "℃");
            tvMin.setText("最低" + forecast.tmp.min + "℃");
            forecastLayout.addView(view);
        }
        aqi.setText(weather.aqi.city.aqi);
        pm25.setText(weather.aqi.city.pm25);
        tvComfort.setText(weather.suggestion.comf.txt);
        tvCarwash.setText(weather.suggestion.cw.txt);
        tvSport.setText(weather.suggestion.sport.txt);
        scrollView.setVisibility(View.VISIBLE);

    }

    private void init() {
        scrollView = findViewById(R.id.weather_info);
        titleCounty = findViewById(R.id.tvCounty);
        titleTime = findViewById(R.id.update_time);
        nowInfo = findViewById(R.id.now_weather);
        nowTem = findViewById(R.id.now_tem);
        forecastLayout = findViewById(R.id.forcast_layout);
        aqi = findViewById(R.id.aqi);
        pm25 = findViewById(R.id.pm25);
        tvComfort = findViewById(R.id.tv_comfort);
        tvCarwash = findViewById(R.id.tv_washcar);
        tvSport = findViewById(R.id.tv_sport);
        ivBing = findViewById(R.id.ivBiYING);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue);
    }
}
