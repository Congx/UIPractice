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

    /**
     * view activity 使用场景
     * @param ImageView
     * @param progressId 正在加载中占位图
     * @param errorId   加载失败占位图
     * @param radio   圆角
     */
    fun display(context: Context, imageView: ImageView, url: String, progressId: Int, errorId: Int,radio:Int)

    fun display(context: Context, imageView: ImageView, url: String, progressId: Int, errorId: Int)

    fun display(context: Context, imageView: ImageView, url: String, progressId: Int)

    fun display(context: Context, imageView: ImageView, url: String)

    /**
     * view fragment 使用场景
     */
    fun display(context: Fragment, imageView: ImageView, url: String, progressId: Int, errorId: Int,radio:Int)

    fun display(context: Fragment, imageView: ImageView, url: String, progressId: Int, errorId: Int)

    fun display(context: Fragment, imageView: ImageView, url: String, progressId: Int)

    fun display(context: Fragment, imageView: ImageView, url: String)

    /**
     * view 使用场景
     */
    fun display(context: View, imageView: ImageView, url: String, progressId: Int, errorId: Int,radio:Int)

    fun display(context: View, imageView: ImageView, url: String, progressId: Int, errorId: Int)

    fun display(context: View, imageView: ImageView, url: String, progressId: Int)

    fun display(context: View, imageView: ImageView, url: String)

    fun display(context: Any, imageView: ImageView, uri: Uri)
}
