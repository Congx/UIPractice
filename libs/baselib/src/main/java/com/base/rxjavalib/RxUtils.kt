package com.base.rxjavalib

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.base.net.BaseResponse
import com.bumptech.glide.request.BaseRequestOptions
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface Comparable<in T> {
    operator fun compareTo(other: T): Int
}

fun demo(x: Comparable<Number>) {
    x.compareTo(1.0) // 1.0 拥有类型 Double，它是 Number 的子类型
    // 因此，我们可以将 x 赋给类型为 Comparable <Double> 的变量
    val y: Comparable<Double> = x // OK！
    y.compareTo(1.0)
}
/**
 * @date 2019-11-15
 * @Author luffy
 * @description
 */
object RxUtils {

    /**
     * 统一做Rxjava的变换
     * 1、
     */
    fun <T:BaseResponse<R>,R> transformer(): ObservableTransformer<T,R> {
        return ObservableTransformer { upstream: Observable<T> ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap{
                    Log.i("RxUtils--flatMap","接口数据转换")
                    var list = it.data
                    if (list == null) {
                        Observable.empty()
                    } else {
                        Observable.just(list)
                    }
                }
        }
    }

    @JvmStatic
    fun <T> bindLifecycle(lifecycleOwner: LifecycleOwner?): AutoDisposeConverter<T> {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner))
    }

}