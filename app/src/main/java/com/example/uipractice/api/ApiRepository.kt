package com.example.uipractice.api

import com.base.rxjavalib.RxUtils
import com.example.uipractice.bean.PublishBean
import com.example.uipractice.net.RetrofitServer
import io.reactivex.Observable

/**
 * @date 2019-12-29
 * @Author luffy
 * @description
 */
object ApiRepository {

    fun getPublishList(): Observable<List<PublishBean>> {
        return RetrofitServer.defaultRetrofitClient.create(AppApi::class.java).getPublishList()
            .compose(RxUtils.applySchedulers())
            .flatMap {
                var list = it.data
                if (list == null) {
                    Observable.empty()
                } else {
                    Observable.just(list)
                }
            }

    }
}