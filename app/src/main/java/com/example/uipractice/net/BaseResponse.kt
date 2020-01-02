package com.example.uipractice.net

/**
 * @date 2019-12-29
 * @Author luffy
 * @description
 */
class BaseResponse<T> {
    var errorCode: Int = 0
    var errorMsg: String? = ""
    var data: T? = null

    fun isSuccess(): Boolean {
        return errorCode === 200
    }
}
