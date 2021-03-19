package com.example.uipractice;

import android.app.Application;
import android.util.Log;

import com.base.custom.callback.CustomCallback;
import com.base.custom.callback.EmptyCallback;
import com.base.custom.callback.ErrorCallback;
import com.base.custom.callback.LoadingCallback;
import com.base.custom.callback.TimeoutCallback;
import com.base.framwork.BuildConfig;
import com.base.framwork.app.AppActivityLifecycleCallback;
import com.base.framwork.app.CrashHandler;
import com.base.framwork.image.ImageLoader;
import com.base.framwork.ui.statusview.core.LoadSir;
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

        LoadSir.newBuilder()
                .addCallback(new ErrorCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .addCallback(new TimeoutCallback())
                .addCallback(new CustomCallback())
                .setDefaultCallback(LoadingCallback.class)
                .commit();

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
