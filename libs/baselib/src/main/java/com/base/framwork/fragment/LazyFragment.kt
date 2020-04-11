package com.base.framwork.fragment

/**
 * @date 11/4/2020
 * @Author luffy
 * @description 懒加载
 */
abstract class LazyFragment : BaseFragment() {

    private var isLoaded = false

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

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
    }

    /**
     * 第一次可见，调用，一般用于懒加载
     */
    fun lazyLoad() {

    }

    /**
     * 在第一次懒加载之后，如果切换resumue 可见需要刷新数据的情况
     */
    fun afterLazyLoad() {

    }

}
