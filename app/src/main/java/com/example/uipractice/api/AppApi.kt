package com.example.uipractice.api

import com.example.uipractice.bean.PublishBean
import com.example.uipractice.net.BaseResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET



/**
 * @date 2019-12-29
 * @Author luffy
 * @description
 */
interface AppApi {

    /**
     * 获取公众号列表
     */
    @GET("/wxarticle/chapters/json")
    fun getPublishList(): Observable<BaseResponse<List<PublishBean>>>
}