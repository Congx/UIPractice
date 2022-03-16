package com.example.uipractice.rxjava

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.base.rxjavalib.RxUtils.bindLifecycle
import com.base.rxjavalib.bindLifecycle
import com.example.uipractice.R
import com.uber.autodispose.android.autoDispose
import com.uber.autodispose.android.lifecycle.autoDisposable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_auto_dispose.*

class AutoDisposeActivity : AppCompatActivity() {

    var messagesStream = PublishSubject.create<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_dispose)
        val disposable = Observable.just("str")
            .doOnDispose {
                Log.e(
                    "AutoDisposeActivity",
                    "dispose"
                )
            }
            .`as`(
                bindLifecycle(
                    this
                )
            )
            .subscribe { str: String? ->

            }

        btnSend.setOnClickListener {
            messagesStream.onNext("xxxxx")
        }
        messagesStream.startWith(startWith().toObservable())
            .bindLifecycle(this)
            .subscribe {
                Log.e("subscribe",it)
            }
    }

    fun startWith():Completable {
        return Completable.fromAction {
            Log.e("startWith","fromAction")
        }
    }

}