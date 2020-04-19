package com.pfj.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("tmp")
    public String tem;
    public Cond cond;
    public class Cond{
        public String txt;
    }
}
