package com.pfj.coolweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.pfj.coolweather.db.City;
import com.pfj.coolweather.db.County;
import com.pfj.coolweather.db.Province;
import com.pfj.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    public static boolean handleProvince(String response) {

        if (!response.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Province province = new Province();
                    JSONObject object = jsonArray.getJSONObject(i);
                    province.provinceCode = object.getInt("id");
                    province.provinceName = object.getString("name");
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                LogUtil.e("handleCity occur error");
            }
        }
        return false;
    }

    public static boolean handleCity(String response, int provinceId) {
        if (!response.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    City city = new City();
                    JSONObject object = jsonArray.getJSONObject(i);
                    city.cityCode = object.getInt("id");
                    city.cityName = object.getString("name");
                    city.provinceId = provinceId;//当前城市所在的省
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                LogUtil.e("handleCity occur error");
            }
        }
        return false;
    }

    public static boolean handleCounty(String response, int cityId) {

        if (!response.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    County county = new County();
                    JSONObject object = jsonArray.getJSONObject(i);
                    county.weatherId = object.getString("weather_id");
                    county.countyName = object.getString("name");
                    county.cityId = cityId;
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                LogUtil.e("handleCounty occur error");
            }
        }
        return false;
    }

    public static Weather handleWeatherInfo(String response) {
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject object = new JSONObject(response);
                JSONArray heWeather = object.getJSONArray("HeWeather");
                String array = heWeather.getJSONObject(0).toString();
                return new Gson().fromJson(array, Weather.class);//String->Weather对象
            }

        } catch (Exception e) {
            LogUtil.e("handleWeatherInfo Exception");
        }
        return null;
    }


}
