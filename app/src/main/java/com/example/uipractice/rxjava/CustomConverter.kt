package com.example.uipractice.rxjava

import com.base.net.BaseResponse
import io.reactivex.Observable
import io.reactivex.ObservableConverter
import io.reactivex.subjects.BehaviorSubject

open class CustomConverter<T>:ObservableConverter<BaseResponse<T>,Observable<T>> {

    override fun apply(upstream: Observable<BaseResponse<T>>): Observable<T> {
        var result = BehaviorSubject.create<T>()
        upstream.subscribe {
            result.onNext(it.data!!)
        }
        return result.share()
    }

}