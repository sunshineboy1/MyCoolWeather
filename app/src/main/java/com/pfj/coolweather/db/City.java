package com.pfj.coolweather.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    //城市标识：在数据库表示是哪个城市
    public int id;
    //城市名字
    public String cityName;
    //根据code找到下一级
    public int cityCode;
    //上一级所属省
    public int provinceId;
}
