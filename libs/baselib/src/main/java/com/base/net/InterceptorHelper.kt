package com.base.net

import android.util.Log
import com.base.context.ContextProvider
import com.base.framwork.BuildConfig
import com.base.framwork.utils.NetworkUtils
import com.base.utils.removeExtraSlashOfUrl
import com.safframework.http.interceptor.LoggingInterceptor
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * @date 2020-01-02
 * @Author luffy
 * @description
 */
object InterceptorHelper {

    var TAG = "Interceptor"
    private val LINE_SEPARATOR = System.getProperty("line.separator")

    @JvmStatic
    fun getJsonString(msg: String): String {

        var message: String
        try {
            if (msg.startsWith("{")) {
                val jsonObject = JSONObject(msg)
                message = jsonObject.toString(3)
            } else if (msg.startsWith("[")) {
                val jsonArray = JSONArray(msg)
                message = jsonArray.toString(3)
            } else {
                message = msg
            }
            message = message.replace("\\/", "/")
        } catch (e: JSONException) {
            message = msg
        }

        return message
    }

    /**
     * 日志拦截器
     */
    //设置打印数据的级别
    val logInterceptor: Interceptor
        get() {
            return HttpLoggingInterceptor(object :HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    var formate = getJsonString(message)
//                    Log.d("httplog",LINE_SEPARATOR + formate)
                    Timber.tag("httplog").d(formate + formate + formate)
                }

            }).apply { level = HttpLoggingInterceptor.Level.BODY }
//            val interceptor: LoggingInterceptor = LoggingInterceptor.Builder()
//                    .loggable(BuildConfig.DEBUG)
//                    .request()
//                    .requestTag("Request")
//                    .response()
//                    .responseTag("Response") //.hideVerticalLine()// 隐藏竖线边框
//                    .hideVerticalLine()
//                    .build()
//            return interceptor
        }

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
                var request = checkUrl(chain.request())
                val compressedRequest = request.newBuilder()
                    .header("Content-Encoding", "gzip")
                    .header("User-Agent", "OkHttp Headers.java")
                    .addHeader("Accept", "application/json;charset=UTF-8")
                    .addHeader("Accept-Encoding", "identity")
                    .build()
                return chain.proceed(compressedRequest)
            }

            /**
             * 校验url是否有多余的 斜杠，有的话去掉
             */
            private fun checkUrl(request: Request): Request {
                var uUrl = request.url.toUrl()
                uUrl.host
                var url = uUrl.toString()
                // 校验多余的斜杠
                url = removeExtraSlashOfUrl(url)
                return request.newBuilder().url(url).build()
            }
        }

}


