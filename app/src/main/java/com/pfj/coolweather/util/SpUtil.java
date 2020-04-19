package com.pfj.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.pfj.coolweather.application.MyApplication;

public class SpUtil {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    public static SharedPreferences getSp() {
        if (sp == null) {
            sp = MyApplication.getContext().getSharedPreferences("sp_file", Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = getSp().edit();
        }
        return editor;
    }

    public static void putString(String key,String value) {
        editor = getEditor();
        editor.putString(key,value);
        editor.commit();
    }

    public static String getString(String key) {
        String value = getSp().getString(key,null);
        return value;
    }

    public static int getInt(String key) {
        int value = getSp().getInt(key,0);
        return value;
    }

    public static void putInt(String key,int value) {
        editor = getEditor();
        editor.putInt(key,value);
        editor.commit();
    }

}
