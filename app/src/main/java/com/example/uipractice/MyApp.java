package com.example.uipractice;

import android.app.Application;
import android.util.Log;

import com.base.framwork.BuildConfig;
import com.base.framwork.app.AppActivityLifecycleCallback;
import com.base.framwork.app.CrashHandler;
import com.base.framwork.image.ImageLoader;
import com.example.uipractice.plugin.HookHelper;

import timber.log.Timber;

/**
 * @date 2019-11-19
 * @Author luffy
 * @description
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 自定义crash处理
        CrashHandler.getInstance().init(this, BuildConfig.DEBUG,false,0,SplashActivity.class);
        // ActivityLifecycleCallbacks 注册
        registerActivityLifecycleCallbacks(new AppActivityLifecycleCallback());

        ImageLoader.globeErrorId = R.mipmap.app_logo;
        ImageLoader.globeProgressId = R.mipmap.app_logo;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        try{
//            HookHelper.hookInstrumentation(this);
            HookHelper.hookAMS();
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("MyApp",e.getMessage());
        }

    }

}
