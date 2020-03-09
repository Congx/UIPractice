package com.base.rxjavalib

import androidx.lifecycle.LifecycleOwner
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable

/**
 * @date 2020-01-10
 * @Author luffy
 * @description
 */

inline fun <T> Observable<T>.bindLifecycle(lifecycleOwner: LifecycleOwner?): ObservableSubscribeProxy<T> {
    return `as` (AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)))
}