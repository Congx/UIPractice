package com.base.net

import com.base.Constans
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.safframework.http.interceptor.Logger
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * @date 2019-12-29
 * @Author luffy
 * @description
 */
object RetrofitServer {

    val defaultOkHttpClient:OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(10,TimeUnit.SECONDS)
            .connectTimeout(10,TimeUnit.SECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .addInterceptor(InterceptorHelper.headerInterceptor)
            .addInterceptor(InterceptorHelper.logInterceptor)
            .setTrustAllCertificate()
            .build()
    }

    val defaultRetrofitClient:Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constans.URL.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(defaultOkHttpClient)
            .build()
    }

}