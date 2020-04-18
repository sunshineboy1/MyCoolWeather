package com.pfj.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;//ok表示成功
    public Basic basic;
    public AQI aqi;
    @SerializedName("daily_forecast")
    public List<DailyForecast> forecastList;
    public Now now;
    public Suggestion suggestion;

}
