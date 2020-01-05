package com.base.framwork.p;

/**
 * @date 2019-12-11
 * @Author luffy
 * @description
 */
public interface ILifecycle{

    void onCreate();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
