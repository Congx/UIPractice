package com.example.uipractice;

import android.app.Application;

import com.base.custom.callback.CustomCallback;
import com.base.custom.callback.EmptyCallback;
import com.base.custom.callback.ErrorCallback;
import com.base.custom.callback.LoadingCallback;
import com.base.custom.callback.TimeoutCallback;
import com.base.framwork.BuildConfig;
import com.base.framwork.app.AppActivityLifecycleCallback;
import com.base.framwork.app.CrashHandler;
import com.base.framwork.ui.statusview.core.LoadSir;

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
        // 自定义crash处理
        CrashHandler.getInstance().init(this, BuildConfig.DEBUG,true,0,SplashActivity.class);
        // ActivityLifecycleCallbacks 注册
        registerActivityLifecycleCallbacks(new AppActivityLifecycleCallback());

        LoadSir.newBuilder()
                .addCallback(new ErrorCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .addCallback(new TimeoutCallback())
                .addCallback(new CustomCallback())
                .setDefaultCallback(LoadingCallback.class)
                .commit();
    }

    public static Application getApplication() {
        return application;
    }

}
