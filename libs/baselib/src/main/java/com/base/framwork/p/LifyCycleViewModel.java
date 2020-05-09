package com.base.framwork.p;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModel;

/**
 * @date 2019-12-11
 * @Author luffy
 * @description 具有生命周期的ViewModel,仿照fragment/activity的源码改造，
 *              让viewmode像Activity/Fragment一样具有生命周期事件发射能力，
 *              和生命周期管理能力，完全解耦View层和viewModle(Presenter)层
 */
public class LifyCycleViewModel extends ViewModel implements LifecycleEventObserver,LifecycleOwner {

    private String TAG = this.getClass().getSimpleName();

    LifecycleRegistry mLifecycleRegistry;

    @NonNull
    @Override
    public LifecycleRegistry getLifecycle() {
        if (mLifecycleRegistry == null) {
            mLifecycleRegistry = new LifecycleRegistry(this);
        }
        return mLifecycleRegistry;
    }

    /**
     * 是否注册给，lifecyclerOwner，默认为true，不需要监听生命周期，重写返回false
     * @return
     */
    public boolean needObserver() {
        return true;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        // 不需要生命周期 直接返回
        if (!needObserver()) return;
        Log.i(TAG,"lifecycle:" + event.toString());
        switch (event) {
            case ON_CREATE:
                onCreate();
                break;
            case ON_START:
                onStart();
                break;
            case ON_RESUME:
                onResume();
                break;
            case ON_PAUSE:
                onPause();
                break;
            case ON_STOP:
                onStop();
                break;
            case ON_DESTROY:
                onDestroy();
                break;
        }
        // 事件转发，具有LifecycleOwner能力
        getLifecycle().handleLifecycleEvent(event);
    }

    public void onCreate() {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

//    @Override
//    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {
//
//    }

}
