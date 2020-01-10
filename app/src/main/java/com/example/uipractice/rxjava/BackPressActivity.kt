package com.example.uipractice.rxjava

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.base.rxjavalib.RxUtils
import com.example.uipractice.R
import com.example.uipractice.api.ApiRepository

class BackPressActivity : AppCompatActivity() {

    @SuppressLint("CheckResult", "AutoDispose")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_press)
//        val disposable = Observable.just("str")
//            .doOnDispose { Log.e("AutoDisposeActivity", "dispose") }
//            .`as`(RxUtils.bindLifecycle(this))
//            .subscribe { str -> Log.e("AutoDisposeActivity", str) }

        ApiRepository.getPublishList().`as`(RxUtils.bindLifecycle(this))
            .subscribe({ list->
                Log.e("BackPressActivity",list.size.toString())
            },{ e->
                e.printStackTrace()
            })

    }

}