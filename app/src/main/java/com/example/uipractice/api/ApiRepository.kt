package com.example.uipractice.api

import com.base.net.RetrofitServer
import com.base.rxjavalib.transformer
import com.example.uipractice.bean.PublishBean
import io.reactivex.Observable

/**
 * @date 2019-12-29
 * @Author luffy
 * @description
 */
object ApiRepository {

    fun getPublishList(): Observable<List<PublishBean>> {
        return RetrofitServer.defaultRetrofitClient.create(AppApi::class.java).getPublishList()
            .transformer()

    }

}