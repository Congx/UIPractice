package com.example.uipractice.rxjava;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.base.rxjavalib.RxUtils;
import com.example.uipractice.R;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class AutoDisposeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_dispose);
        Disposable disposable = Observable.just("str")
                .doOnDispose(()->{Log.e("AutoDisposeActivity","dispose");})
                .as(RxUtils.bindLifecycle(this))
                .subscribe(str -> {
                    Log.e("AutoDisposeActivity",str);
                });
    }
}