package com.example.uipractice.rxjava

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.base.framwork.ui.utils.StatusBarUtil
import com.base.net.BaseResponse
import com.example.uipractice.R
import com.rxjava.rxlife.RxLife
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_back_press.btnTest1
import kotlinx.android.synthetic.main.activity_back_press.btnTest2
import kotlinx.android.synthetic.main.activity_back_press.btnTest3
import kotlinx.android.synthetic.main.activity_rx_test.*
import timber.log.Timber
import java.lang.ProcessBuilder.Redirect.to

class RxTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_test)

        btnTest1.setOnClickListener {
            test1()
        }

        btnTest2.setOnClickListener {
            test2()
        }
        btnTest3.setOnClickListener {
            test3()
        }

        btnTest4.setOnClickListener {
            test4()
        }
        StatusBarUtil.setTranslucentForImageView(this,null)
    }

    fun test1() {
        Observable.fromIterable(mutableListOf(1, 2,3))
            .flatMap {
                return@flatMap Observable.just(it*it)
            }.subscribe(PublishSubject.create())

    }

    fun test2() {
        val create = BehaviorSubject.createDefault(true)
        val firstElement = create.filter { it }.firstElement().ignoreElement()
//        create.onNext(true)

        Completable.fromAction({
            Timber.d("start")
        }).startWith(firstElement).subscribe {
            Timber.d("complete")
        }
    }

    fun test3() {
        val create = PublishSubject.create<Int>()
        create.subscribe {
            Timber.d("$it")
        }
        Observable.fromIterable(mutableListOf(1, 2,3)).`as`(RxLife.`as`(this))
//            .flatMap {
//                return@flatMap Observable.just(it*it)
//            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//            .subscribe(create)
    }

    fun test4() {
        val baseResponse = BaseResponse<String>()
        baseResponse.data = "sss"

        Observable.just(baseResponse).`as`(CustomConverter()).subscribe{
            Toast.makeText(this,it,Toast.LENGTH_LONG).show()
        }
    }
}
