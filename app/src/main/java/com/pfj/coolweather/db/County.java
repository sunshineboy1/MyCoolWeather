package com.pfj.coolweather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    public int countyId;
    public String countyName;
    public String weatherCode;
    public int cityId;
}
