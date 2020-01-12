package com.example.uipractice.api

import com.base.rxjavalib.RxUtils
import com.example.uipractice.bean.PublishBean
import com.base.custom.net.RetrofitServer
import io.reactivex.Observable

/**
 * @date 2019-12-29
 * @Author luffy
 * @description
 */
object ApiRepository {

    fun getPublishList(): Observable<List<PublishBean>> {
        return RetrofitServer.defaultRetrofitClient.create(AppApi::class.java).getPublishList()
            .compose(RxUtils.transformer())

    }

}