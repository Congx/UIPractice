package com.base.framwork.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.base.framwork.activity.BaseActivity

/**
 * @date 2019-12-08
 * @Author luffy
 * @description 同[BaseActivity]
 */
open class BaseFragment : Fragment(){

    var isLoaded = false
    var rootView: View? = null

    @CallSuper
    override fun onResume() {
        super.onResume()
        if (!isLoaded) {
            lazyLoad()
            isLoaded = true
        }
        // 有的需要后续可见的时候
        if (isLoaded) {
            afterLazyLoad()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rootView = view
        // 防止事件穿透
        view.isClickable = true
        super.onViewCreated(view, savedInstanceState)
    }


    /**
     * 显示dialog
     */
//    open fun showLoadingDialog(): LoadingDialog? {
//        return LoadingDialogHelper.instance.showLoading(activity, getLoadingAnimateSrc())
//    }

    /**
     * 隐藏dialog
     *
     */
    open fun hideLoadingDialog() {
//        LoadingDialogHelper.instance.hideLoading(activity)
    }

    /***
     * 定制当前Activity loading动画资源
     *
     * @return lottie json资源，assets中的文件资源全名：如**yello.json** <br></br>
     * 如果返回空则使用默认数据,见[LoadingDialog]
     */
    open fun getLoadingAnimateSrc(): String? {
        return null
    }


    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
        rootView = null

    }

    /**
     * 第一次可见，调用，一般用于懒加载
     */
    open fun lazyLoad() {

    }

    /**
     * 在第一次懒加载之后，如果切换resumue 可见需要刷新数据的情况
     */
    open fun afterLazyLoad() {

    }

    /**
     * 有轮询任务的时候复写
     */
    open fun refreshByPoll() {

    }
}