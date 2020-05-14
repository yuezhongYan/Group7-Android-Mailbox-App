package com.njit.android.emailmobileterminal.utils;

import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import com.njit.android.emailmobileterminal.MyApplication;

public class ToastHelper {
    public static void showResID(@StringRes int id){
        Toast.makeText(MyApplication.getInstance(),MyApplication.getInstance().getString(id),Toast.LENGTH_LONG).show();
    }
    public static void show(String info){
        Toast.makeText(MyApplication.getInstance(),info,Toast.LENGTH_LONG).show();
    }
}
