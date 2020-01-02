package com.example.uipractice.api

import com.example.uipractice.bean.PublishBean
import com.example.uipractice.net.RetrofitServer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @date 2019-12-29
 * @Author luffy
 * @description
 */
object ApiRepository {

    fun getPublishList(): Observable<List<PublishBean>> {
        return RetrofitServer.defaultRetrofitClient.create(AppApi::class.java).getPublishList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                var list = it.data
                if (list == null) {
                    return@flatMap Observable.empty<List<PublishBean>>()
                } else {
                    Observable.just(list)
                }
            }
    }
}