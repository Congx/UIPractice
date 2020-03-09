package com.base.net

/**
 * @date 2019-12-29
 * @Author luffy
 * @description
 */
class BaseResponse<T> {
    // !!!!!!!! 注意 ！！！！！
    // 根据服务器的返回值而定
    var errorCode: Int = 0
    var errorMsg: String? = ""
    var data: T? = null

    fun isSuccess(): Boolean {
        return errorCode === 200
    }
}
