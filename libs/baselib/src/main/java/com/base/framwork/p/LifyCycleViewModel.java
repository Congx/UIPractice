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
public class LifyCycleViewModel extends ViewModel implements ILifecycle, LifecycleEventObserver,LifecycleOwner {

    private String TAG = this.getClass().getSimpleName();

    LifecycleRegistry mLifecycleRegistry;

    public LifyCycleViewModel() {
        initLifecycle();
    }

    private void initLifecycle() {
        mLifecycleRegistry = new LifecycleRegistry(this);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
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
        mLifecycleRegistry.handleLifecycleEvent(event);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

//    @Override
//    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {
//
//    }

}
