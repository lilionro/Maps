package com.zero.lionro.maps.app;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by king on 2017/8/17.
 */

public class MyLeanCloudApp extends Application {

    private static Context context;
    private AppCompatActivity activity;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        activity = new AppCompatActivity();
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(getApplicationContext(), "UBXiXvjxUN1uBRgLVaG2zdJd-gzGzoHsz", "LVy4VcC0yzMY6XFLmqGi16lz");
        AVOSCloud.setDebugLogEnabled(true);
    }
    public static Context getContext(){
        return context;
    }

}

