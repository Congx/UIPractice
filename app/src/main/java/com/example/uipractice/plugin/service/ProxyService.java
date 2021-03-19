package com.example.uipractice.plugin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ProxyService extends Service {

    private final String TAG = "ProxyService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        return Service.START_STICKY;
    }
}
