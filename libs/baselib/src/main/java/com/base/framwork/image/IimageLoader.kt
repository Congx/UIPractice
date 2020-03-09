package com.base.framwork.image

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment

/**
 * @date 2020-01-09
 * @Author luffy
 * @description
 */
interface IimageLoader {

    var config: Config

    /**
     * 额外的配置,优先级低于
     * 加载图片，首先会取display()中的id（如果有），优先级最高，高于Config配置，其次会去取全局设置的id
     * 但是app的设计并没有搞什么的全局加载图...所以全局加载占位图默认关闭，如果display方法也没有配置
     * 就不用加载占位图
     *
     */
    class Config {
        /**
         * 是否需要加载中的图
         */
        var isNeedLoadingImg:Boolean = false
        /**
         * 是否需要加载失败的图
         */
        var isNeedLoadError:Boolean = false
        var progressId:Int = -1
        var errorId:Int = -1
        var radio:Int = -1

    }

    fun config(config: Config)

    /**
     * 是否需要加载中的图
     */
    fun isNeedLoadingImg(boolean: Boolean)

    /**
     * 是否需要加载失败的图
     */
    fun isNeedLoadFault(boolean: Boolean)

    /**
     * view activity 使用场景
     * @param ImageView
     * @param progressId 正在加载中占位图
     * @param errorId   加载失败占位图
     * @param radio   圆角
     */
    fun display(context: Context, imageView: ImageView, url: String, progressId: Int, errorId: Int,radio:Int) : IimageLoader

    fun display(context: Context, imageView: ImageView, url: String, progressId: Int, errorId: Int) : IimageLoader

    fun display(context: Context, imageView: ImageView, url: String, progressId: Int) : IimageLoader

    fun display(context: Context, imageView: ImageView, url: String) : IimageLoader

    fun display(context: Context, imageView: ImageView, url: String,config: Config) : IimageLoader

    /**
     * view fragment 使用场景
     */
    fun display(context: Fragment, imageView: ImageView, url: String, progressId: Int, errorId: Int,radio:Int) : IimageLoader

    fun display(context: Fragment, imageView: ImageView, url: String, progressId: Int, errorId: Int) : IimageLoader

    fun display(context: Fragment, imageView: ImageView, url: String, progressId: Int) : IimageLoader

    fun display(context: Fragment, imageView: ImageView, url: String) : IimageLoader

    fun display(context: Fragment, imageView: ImageView, url: String,config: Config) : IimageLoader

    /**
     * view 使用场景
     */
    fun display(context: View, imageView: ImageView, url: String, progressId: Int, errorId: Int,radio:Int) : IimageLoader

    fun display(context: View, imageView: ImageView, url: String, progressId: Int, errorId: Int) : IimageLoader

    fun display(context: View, imageView: ImageView, url: String, progressId: Int) : IimageLoader

    fun display(context: View, imageView: ImageView, url: String) : IimageLoader

    fun display(context: View, imageView: ImageView, url: String,config: Config) : IimageLoader

    fun display(context: Any, imageView: ImageView, uri: Uri)

}
