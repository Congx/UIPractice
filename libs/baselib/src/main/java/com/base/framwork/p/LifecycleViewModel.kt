package com.lp.base.viewmodel

import android.util.Log
import androidx.lifecycle.*

/**
 * @date 2019-12-11
 * @Author luffy
 * @description 具有生命周期的ViewModel，
 * 让viewmode像Activity/Fragment一样具有生命周期事件接收能力，
 * 和生命周期分发能力，完全解耦View层和viewModle层
 */
open class LifecycleViewModel : ViewModel(), LifecycleEventObserver, LifecycleOwner {

    private val TAG = this.javaClass.simpleName

    private var mLifecycleRegistry: LifecycleRegistry? = null

    override fun getLifecycle(): LifecycleRegistry {

        if (mLifecycleRegistry == null) {
            mLifecycleRegistry = LifecycleRegistry(this)
        }
        return mLifecycleRegistry as LifecycleRegistry
    }

    /**
     * 是否注册给，lifecyclerOwner，默认为true，不需要监听生命周期，重写返回false
     * @return
     */
    open fun needObserver(): Boolean {
        return true
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        // 不需要生命周期 直接返回
        if (!needObserver()) return
        Log.i(TAG, "lifecycle:$event")
        when (event) {
            Lifecycle.Event.ON_CREATE -> onCreate()
            Lifecycle.Event.ON_START -> onStart()
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_PAUSE -> onPause()
            Lifecycle.Event.ON_STOP -> onStop()
            Lifecycle.Event.ON_DESTROY -> onDestroy()
            else -> Lifecycle.Event.ON_DESTROY
        }
        // 事件转发，具有LifecycleOwner能力
        lifecycle.handleLifecycleEvent(event)
    }

    open fun onCreate() {}
    open fun onStart() {}
    open fun onResume() {}
    open fun onPause() {}
    open fun onStop() {}
    open fun onDestroy() {}
}