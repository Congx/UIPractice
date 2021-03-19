package com.example.uipractice.plugin.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.uipractice.plugin.HookHelper;

public class PluginService extends Service {

    private final String TAG = "PluginService";

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

        if (intent == null || intent.hasExtra(HookHelper.TARGET_SERVICE_INTENT)) {
            return Service.START_STICKY;
        }

        String serviceName = intent.getStringExtra(HookHelper.TARGET_SERVICE_INTENT);
        if (serviceName == null )return Service.START_STICKY;



        return Service.START_STICKY;
    }
}
