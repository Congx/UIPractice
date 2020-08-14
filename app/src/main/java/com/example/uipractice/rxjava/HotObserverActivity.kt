package com.example.uipractice.rxjava

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_hot_observer.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

class HotObserverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot_observer)

        btnCold.setOnClickListener {
            coldFun()
        }

        btnHot.setOnClickListener {
            hotFun()
        }


        btnShare.setOnClickListener {
            shareFun()
        }

//        Executors.newSingleThreadExecutor().submi

        //input2生成， 需要耗费3秒
        //input2生成， 需要耗费3秒
        val input2_futuretask: FutureTask<Int?> = FutureTask(Callable<Int?> {
            Thread.sleep(3000)
            5
        })

        Thread(input2_futuretask).start()
    }

    /**
     * 用来测试冷 Observable
     * 1、一个被观察者可以订阅多个观察者，观察者的订阅事件可单独取消
     * 2、多个观察者之间事件序列互不影响
     * 3、一旦重新订阅，事件从头开始
     */
    fun coldFun() {
        var dispose: Disposable? = null
        var observable = Observable.interval(1,TimeUnit.SECONDS);
        observable
            .doOnSubscribe {
                dispose = it
            }
            .subscribe {
            log("code",it)
        }

        var observer:Observer<Long> = object : Observer<Long> {
            override fun onComplete() {
                log("onComplete","onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                dispose = d
                log("onSubscribe","onSubscribe")

            }

            override fun onNext(t: Long) {
                log("onNext",t)
            }

            override fun onError(e: Throwable) {
                log("onError",e)
            }

        }

        btnCodeDispose.setOnClickListener {
            dispose?.dispose()
        }

        btnCodeSubscribe.setOnClickListener {
            observable.subscribe(observer)
        }

    }

    fun hotFun() {
        var dispose: Disposable? = null

        var observable = Observable.interval(1, TimeUnit.SECONDS)

        observable.subscribe {
            log("observable",it)
        }

        val publish = observable.publish().refCount()
        publish.subscribe {
            log("publish1",it)
        }

        var observer:Observer<Long> = object : Observer<Long> {
            override fun onComplete() {
                log("publish2","onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                dispose = d
                log("publish2","onSubscribe")

            }

            override fun onNext(t: Long) {
                log("publish2",t)
            }

            override fun onError(e: Throwable) {
                log("publish2",e)
            }

        }

        btnHotSubscribe.setOnClickListener {
            publish.subscribe(observer)
        }

        btnHotDispose.setOnClickListener {
            dispose?.dispose()
        }

        btnConnet.setOnClickListener {
//            publish.connect()
        }

        btnDisconnet.setOnClickListener {

        }
    }

    fun shareFun() {
        var dispose: Disposable? = null

        var observable = Observable.interval(1, TimeUnit.SECONDS)

        observable.subscribe {
            log("observable",it)
        }

        val publish = observable.publish().refCount()
        publish.subscribe {
            log("publish1",it)
        }

        var observer:Observer<Long> = object : Observer<Long> {
            override fun onComplete() {
                log("publish2","onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                dispose = d
                log("publish2","onSubscribe")

            }

            override fun onNext(t: Long) {
                log("publish2",t)
            }

            override fun onError(e: Throwable) {
                log("publish2",e)
            }

        }

        btnHotSubscribe.setOnClickListener {
            publish.subscribe(observer)
        }

        btnHotDispose.setOnClickListener {
            dispose?.dispose()
        }

        btnConnet.setOnClickListener {
//            publish.connect()
        }

        btnDisconnet.setOnClickListener {

        }
    }

    fun log(tag:String,value:Any) {
        Log.e("HotObserverActivity","$tag  $value")
    }
}
