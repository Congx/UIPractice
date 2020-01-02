package com.example.uipractice;

import android.app.Application;

/**
 * @date 2019-11-19
 * @Author luffy
 * @description
 */
public class MyApp extends Application {

    private static MyApp application;

    @Override
    public void onCreate() {
        application = this;
        super.onCreate();
        CrashHandler.getInstance().init(this,BuildConfig.DEBUG);
    }

    public static Application getApplication() {
        return application;
    }

}
