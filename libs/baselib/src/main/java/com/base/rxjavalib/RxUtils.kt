package com.base.rxjavalib

import androidx.lifecycle.LifecycleOwner
import com.base.net.BaseResponse
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @date 2019-11-15
 * @Author luffy
 * @description 一些功能，提供给java使用，kotlin也可以用RxUtilsKt,用函数扩展实现
 */
object RxUtils {

    /**
     * 统一做Rxjava的变换 具有的功能：
     * 1、线程切换
     * 2、数据转换
     */
    fun <T:BaseResponse<R>,R> transformer(): ObservableTransformer<T,R> {
        return ObservableTransformer { upstream: Observable<T> ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .dataMap()
        }
    }

    @JvmStatic
    fun <T> bindLifecycle(lifecycleOwner: LifecycleOwner?): AutoDisposeConverter<T> {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner))
    }

}