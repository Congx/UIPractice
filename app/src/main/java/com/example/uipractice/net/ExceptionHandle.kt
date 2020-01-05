package com.example.uipractice.net

import org.json.JSONException
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import org.apache.http.conn.ConnectTimeoutException
import retrofit2.HttpException
import java.net.ConnectException
import java.text.ParseException


/**
 * @date 2020-01-01
 * @Author luffy
 * @description
 */
class ExceptionHandle {

    private val UNAUTHORIZED = 401
    private val FORBIDDEN = 403
    private val NOT_FOUND = 404
    private val NOT_ALLOW = 405
    private val REQUEST_TIMEOUT = 408
    private val INTERNAL_SERVER_ERROR = 500
    private val SERVICE_UNAVAILABLE = 503

    fun handleException(e: Throwable?): ServiceException {
        e?.printStackTrace()
        val ex: ServiceException
        if (e is HttpException) {
            val httpException = e as HttpException?
            var message: String? = null
            when (httpException!!.code()) {
                UNAUTHORIZED -> message = "操作未授权"
                FORBIDDEN -> message = "请求被拒绝"
                NOT_FOUND -> message = "服务器不可用"
                REQUEST_TIMEOUT -> message = "服务器执行超时"
                INTERNAL_SERVER_ERROR -> message = "服务器内部错误"
                SERVICE_UNAVAILABLE -> message = "服务器不可用"
                NOT_ALLOW -> message = "HTTP 405 not allowed"
                else -> message = "网络错误"
            }
            ex = ServiceException(httpException.code(), message)
        } else if (e is JsonParseException
            || e is JSONException
            || e is android.net.ParseException || e is MalformedJsonException
        ) {
            ex = ServiceException(ERROR.PARSE_ERROR, "解析错误")
        } else if (e is ConnectException) {
            ex = ServiceException(ERROR.NETWORD_ERROR, "连接失败")
        } else if (e is javax.net.ssl.SSLException) {
            ex = ServiceException(ERROR.SSL_ERROR, "证书验证失败")
        } else if (e is ConnectTimeoutException) {
            ex = ServiceException(ERROR.TIMEOUT_ERROR, "连接超时")
        } else if (e is java.net.SocketTimeoutException) {
            ex = ServiceException(ERROR.TIMEOUT_ERROR, "连接超时")
        } else if (e is java.net.UnknownHostException) {
            ex = ServiceException(ERROR.TIMEOUT_ERROR, "网络异常、请检查网络！")
        } else {
            ex = ServiceException(ERROR.UNKNOWN, "未知错误")
        }
        ex.setRawThrowable(e)
        return ex
    }


    /**
     * 约定异常 这个具体规则需要与服务端或者领导商讨定义
     */
    object ERROR {
        /**
         * 未知错误
         */
        val UNKNOWN = 1000
        /**
         * 解析错误
         */
        val PARSE_ERROR = 1001
        /**
         * 网络错误
         */
        val NETWORD_ERROR = 1002
        /**
         * 协议出错
         */
        val HTTP_ERROR = 1003

        /**
         * 证书出错
         */
        val SSL_ERROR = 1005

        /**
         * 连接超时
         */
        val TIMEOUT_ERROR = 1006
    }
}