package com.pfj.coolweather.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    public int cityId;
    public String cityName;
    public String cityCode;
    public int provinveId;
}
