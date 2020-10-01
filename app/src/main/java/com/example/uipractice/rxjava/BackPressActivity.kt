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
import kotlinx.android.synthetic.main.activity_back_press.*
import timber.log.Timber

class BackPressActivity : AppCompatActivity() {

    @SuppressLint("CheckResult", "AutoDispose")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_press)
        btnTest1.setOnClickListener {
            test1()
        }

    }

    fun test1() {
        val fromIterable = Observable.fromIterable(mutableListOf(1, 2,3))
        fromIterable.subscribe {
            Timber.d(it.toString());
        }
    }

}