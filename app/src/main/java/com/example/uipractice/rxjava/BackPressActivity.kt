package com.example.uipractice.rxjava

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.base.rxjavalib.RxUtils
import com.example.uipractice.R
import com.example.uipractice.api.ApiRepository
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable

@SuppressLint("AutoDispose")
class BackPressActivity : AppCompatActivity() {

    @SuppressLint("CheckResult", "AutoDispose")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_press)
        val disposable = Observable.just("str")
            .doOnDispose { Log.e("AutoDisposeActivity", "dispose") }
            .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe { str -> Log.e("AutoDisposeActivity", str) }

        toList()
    }

    fun toList() {
        var list = arrayListOf("a","b","c")
        Observable.fromArray(list).flatMap {
            return@flatMap Observable.just(it.get(1))
        }

        Observable.just("list","array").flatMap {
            return@flatMap Observable.just(it.get(1))
        }.toList().subscribe{it-> Log.e("x",it.toString())}

        Observable.fromIterable(list).flatMap {
            return@flatMap Observable.just(it.get(1))
        }


    }

}