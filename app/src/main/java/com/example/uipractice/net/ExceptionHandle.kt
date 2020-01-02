package com.example.uipractice.net

import org.json.JSONException
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.net.ConnectException
import java.text.ParseException


/**
 * @date 2020-01-01
 * @Author luffy
 * @description
 */
class ExceptionHandle {

    /**
     * 约定异常
     */
    internal object ERROR {
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
    }

    class ResponeThrowable(throwable: Throwable, var code: Int) : Exception(throwable) {
        override var message: String? = null
    }
    inner class ServerException : RuntimeException() {
        var code: Int = 0
        override var message: String? = null
    }

    companion object {

        private val UNAUTHORIZED = 401
        private val FORBIDDEN = 403
        private val NOT_FOUND = 404
        private val REQUEST_TIMEOUT = 408
        private val INTERNAL_SERVER_ERROR = 500
        private val BAD_GATEWAY = 502
        private val SERVICE_UNAVAILABLE = 503
        private val GATEWAY_TIMEOUT = 504

        fun handleException(e: Throwable): ResponeThrowable {
            val ex: ResponeThrowable
            if (e is HttpException) {
                ex = ResponeThrowable(e, ERROR.HTTP_ERROR)
                when (e.code()) {
                    UNAUTHORIZED, FORBIDDEN, NOT_FOUND, REQUEST_TIMEOUT, GATEWAY_TIMEOUT, INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE -> ex.message =
                        "网络错误"
                    else -> ex.message = "网络错误"
                }
                return ex
            } else if (e is ServerException) {
                ex = ResponeThrowable(e, e.code)
                ex.message = e.message
                return ex
            } else if (e is JsonParseException
                || e is JSONException
                || e is ParseException
            ) {
                ex = ResponeThrowable(e, ERROR.PARSE_ERROR)
                ex.message = "解析错误"
                return ex
            } else if (e is ConnectException) {
                ex = ResponeThrowable(e, ERROR.NETWORD_ERROR)
                ex.message = "连接失败"
                return ex
            } else if (e is javax.net.ssl.SSLHandshakeException) {
                ex = ResponeThrowable(e, ERROR.SSL_ERROR)
                ex.message = "证书验证失败"
                return ex
            } else {
                ex = ResponeThrowable(e, ERROR.UNKNOWN)
                ex.message = "未知错误"
                return ex
            }
        }
    }
}