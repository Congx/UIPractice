package com.base.rxjavalib

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.base.net.ApiErrorOperator
import com.base.net.BaseResponse
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @date 2020-01-10
 * @Author luffy
 * @description 函数扩展 实现功能
 *
 * 按需要自己扩展
 */

/**
 * 绑定生命周期，自动取消订阅
 */
fun <T> Observable<T>.bindLifecycle(lifecycleOwner: LifecycleOwner?): ObservableSubscribeProxy<T> {
    return `as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)))
}

/**
 * 对服务端返回数据->具体javabean的转换
 *
 * 获取BaseResponse 中的data javabean  根据实际情况来
 */
fun <T : BaseResponse<R>, R> Observable<T>.dataMap(): Observable<R> {
    return flatMap {
        Log.i("RxUtilsKt--flatMap", "接口数据转换")
        var list = it.data
        // 对服务端空数据返回做转换
        if (list == null || (list is Collection<*> && list.isEmpty())) {
            Observable.empty()
        } else {
            Observable.just(list)
        }
    }
}

/**
 * 统一做Rxjava的变换 具有的功能：
 * 单独线程切换
 */
fun <T> Observable<T>.iOtransformer(): Observable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

/**
 * 统一做Rxjava的变换 具有的功能：
 * http 请求错误处理
 */
fun <T> Observable<T>.errorTransformer(): Observable<T> {
    return lift(ApiErrorOperator<T>())
}

/**
 * 统一做Rxjava的变换 具有的功能：
 * 1、线程切换
 * 2、数据转换
 * 3、错误处理
 */
fun <T : BaseResponse<R>, R> Observable<T>.transformer(): Observable<R> {
    return iOtransformer().dataMap().errorTransformer()
}