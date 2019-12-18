package com.example.uipractice;

import android.app.Application;

/**
 * @date 2019-11-19
 * @Author luffy
 * @description
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this,BuildConfig.DEBUG);
    }
}
