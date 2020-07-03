package com.base.net

import com.base.context.ContextProvider
import com.base.framwork.utils.NetworkUtils
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * @date 2020-01-02
 * @Author luffy
 * @description
 */
object InterceptorHelper {

    var TAG = "Interceptor"

    /**
     * 日志拦截器
     */
    //设置打印数据的级别
    val logInterceptor: HttpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    /**
     * 缓存拦截器
     *
     * @return
     */
    // CONTEXT不能为空
    // 离线时缓存保存4周,单位:秒
    val cacheInterceptor: Interceptor
        get() = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                var request = chain.request()
                if (!NetworkUtils.isConnected(ContextProvider.getContext())) {
                    val maxStale = 4 * 7 * 24 * 60
                    val tempCacheControl = CacheControl.Builder()
                        .onlyIfCached()
                        .maxStale(maxStale, TimeUnit.SECONDS)
                        .build()
                    request = request.newBuilder()
                        .cacheControl(tempCacheControl)
                        .build()
                }
                return chain.proceed(request)
            }
        }


    /**
     * 重试拦截器
     *
     * @return
     */
    //最大重试次数
    //假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
    val retryInterceptor: Interceptor
        get() = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val maxRetry = 10
                var retryNum = 5

                val request = chain.request()
                var response = chain.proceed(request)
                while (!response.isSuccessful && retryNum < maxRetry) {
                    retryNum++
                    response = chain.proceed(request)
                }
                return response
            }
        }

    /**
     * 请求头拦截器
     *
     * @return
     */
    //在这里你可以做一些想做的事,比如token失效时,重新获取token
    //或者添加header等等
    //                    .addHeader(Constants.WEB_TOKEN, webi_token)
    val headerInterceptor: Interceptor
        get() = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest = chain.request()
                if (null == originalRequest.body) {
                    return chain.proceed(originalRequest)
                }
                val compressedRequest = originalRequest.newBuilder()
                    .header("Content-Encoding", "gzip")
                    .header("User-Agent", "OkHttp Headers.java")
                    .addHeader("Accept", "application/json; q=0.5")
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .addHeader("Accept-Encoding", "identity")
                    .build()
                return chain.proceed(compressedRequest)
            }
        }
}


