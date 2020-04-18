package com.pfj.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class DailyForecast {

    public String date;
    public Cond cond;
    public class Cond{
        @SerializedName("txt_d")
        public String info;
    }
    public Tmp tmp;
    public class Tmp{
        public String max;
        public String min;
    }
}
