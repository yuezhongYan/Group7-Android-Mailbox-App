package com.njit.android.emailmobileterminal.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPHelper {
    private static final String spDefKey = "spDefKey";
    public static boolean putString(Context context,String key,String data){
        SharedPreferences.Editor edit = context.getSharedPreferences(spDefKey, Context.MODE_PRIVATE).edit();
        edit.putString(key, data);
        return edit.commit();
    }
    public static String getString(Context context,String key){
        return context.getSharedPreferences(spDefKey, Context.MODE_PRIVATE).getString(key, "");
    }
}
