package com.example.uipractice.rxjava

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.base.rxjavalib.RxUtils
import com.example.uipractice.R
import com.example.uipractice.api.ApiRepository
import com.example.uipractice.api.AppApi
import com.example.uipractice.bean.PublishBean
import com.example.uipractice.net.RetrofitServer
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable

class BackPressActivity : AppCompatActivity() {

    @SuppressLint("CheckResult", "AutoDispose")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_press)
        val disposable = Observable.just("str")
            .doOnDispose { Log.e("AutoDisposeActivity", "dispose") }
            .`as`(RxUtils.bindLifecycle(this))
            .subscribe { str -> Log.e("AutoDisposeActivity", str) }

    }

}