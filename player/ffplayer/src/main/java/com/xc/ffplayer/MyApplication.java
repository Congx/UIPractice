package com.xc.ffplayer;

import android.app.Application;

public class MyApplication extends Application {

    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
