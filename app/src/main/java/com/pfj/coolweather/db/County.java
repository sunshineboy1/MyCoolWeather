package com.pfj.coolweather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    public int id;
    public String countyName;
    public String weatherId;
    public int cityId;
}
