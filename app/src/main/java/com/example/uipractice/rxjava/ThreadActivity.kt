package com.example.uipractice.rxjava

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription


class ThreadActivity : AppCompatActivity() {

    @SuppressLint("CheckResult", "AutoDispose")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_press)
//        Observable.just(1)
//            .flatMap { transform(it) }
//            .doOnSubscribe { Log.e("ThreadActivity","doOnSubscribe:${Thread.currentThread().name}") }
//            .subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())
//            .subscribe {
//                Log.e("ThreadActivity","subscribe:${Thread.currentThread().name} : $it")
//            }

        var flowable = Flowable.create(FlowableOnSubscribe<Int?> {
                var i = 0
                while (i < 50) {
                    it.onNext(i)
                    i++
                }
            }, BackpressureStrategy.DROP)

        flowable.subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Int?> {
                override fun onSubscribe(s: Subscription) {
                    s.request(Long.MAX_VALUE)
                }

                override fun onNext(t: Int?) {
                    try {
                        Log.e("ThreadActivity-trans","doOnSubscribe:${Thread.currentThread().name} :$t")
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    Log.e("Test", "i = $t")
                }

                override fun onError(t: Throwable) {}
                override fun onComplete() {}
            })

    }

    fun <T> transform(updata: T): Observable<String>? {
        return Observable.just("a")
            .doOnSubscribe { Log.e("ThreadActivity-trans","doOnSubscribe:${Thread.currentThread().name} :$updata") }
            .compose (transformer())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnNext { Log.e("ThreadActivity-trans","doOnNext:${Thread.currentThread().name} :$updata - $it") }
    }

    fun <T> transformer(): ObservableTransformer<T, T>? {
        return ObservableTransformer { upstream: Observable<T> ->
                upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        Log.e("ThreadActivity-former","Transformer:doOnSubscribe:${Thread.currentThread().name} : $it")
                    }.doOnNext {
                        Log.e("ThreadActivity-former","Transformer:doOnNext${Thread.currentThread().name} : $it")
                    }
        }
    }

}