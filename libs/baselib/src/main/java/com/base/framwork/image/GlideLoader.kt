package com.base.framwork.image

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


/**
 * @date 2020-01-09
 * @Author luffy
 * @description
 */
class GlideLoader : IimageLoader {

    companion object {

        fun isValidContextForGlide(context: Any?): Boolean {
            if (context == null) {
                return false
            }
            if (context is Fragment) {
                if(context.isRemoving || context.isDetached) {
                    return false
                }
            }
            if (context is Activity) {
                val activity = context as Activity?
                if (activity!!.isDestroyed || activity.isFinishing) {
                    Log.e("GlideLoader","activity is destoryed")
                    return false
                }
            }
            return true
        }
    }

    fun displayRealy(
        context: Any,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int,
        radio: Int
    ) {
        try { // 以防万一
            val requestOptions = RequestOptions.bitmapTransform(RoundedCorners(radio))
            if(progressId != -1) {
                requestOptions.placeholder(progressId)
            }else {
                if(ImageLoader.globeErrorId != -1)requestOptions.placeholder(ImageLoader.globeProgressId)
            }

            if(errorId != -1) {
                progressId?.let { requestOptions.placeholder(it) }
            }else {
                if(ImageLoader.globeErrorId != -1)requestOptions.placeholder(ImageLoader.globeErrorId)
            }

            if(context is Activity && isValidContextForGlide(context)) {
                Glide.with(context).load(url).apply(requestOptions)
                    .apply(requestOptions).into(imageView)
                return
            }

            if(context is Fragment && isValidContextForGlide(context)) {
                Glide.with(context).load(url).apply(requestOptions).into(imageView)
                return
            }

            if(context is View && isValidContextForGlide(context.context)) {
                Glide.with(context).load(url).apply(requestOptions).into(imageView)
                return
            }

            // 不建议使用
            if (context is Application) {
                Glide.with(context).load(url).apply(requestOptions).into(imageView)
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }


    }

    /**
     * context 场景
     * @param ImageView
     * @param progressId 正在加载中占位图
     * @param errorId   加载失败占位图
     * @param radio   圆角
    */
    override fun display(
        context: Context,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int,
        radio: Int
    ) {
        displayRealy(context,imageView,url,progressId,errorId,radio)
    }

    override fun display(
        context: Context,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int
    ) {
        display(context,imageView,url,progressId,errorId,0)
    }

    override fun display(context: Context, imageView: ImageView, url: String, progressId: Int) {
        display(context,imageView,url,progressId,-1)
    }

    override fun display(context: Context, imageView: ImageView, url: String) {
        display(context,imageView,url,-1)
    }

    /**
     * fragment
     */
    override fun display(
        context: Fragment,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int,
        radio: Int
    ) {
        displayRealy(context,imageView,url,progressId,errorId,radio)
    }

    override fun display(
        context: Fragment,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int
    ) {
        displayRealy(context,imageView,url,progressId,errorId,0)
    }

    override fun display(context: Fragment, imageView: ImageView, url: String, progressId: Int) {
        display(context,imageView,url,progressId,-1)
    }

    override fun display(context: Fragment, imageView: ImageView, url: String) {
        display(context,imageView,url,-1)
    }

    /**
     * View
     */
    override fun display(
        context: View,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int,
        radio: Int
    ) {
        displayRealy(context,imageView,url,progressId,errorId,radio)
    }

    override fun display(
        context: View,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int
    ) {
        display(context,imageView,url,progressId,errorId,0)
    }

    override fun display(context: View, imageView: ImageView, url: String, progressId: Int) {
        display(context,imageView,url,progressId,-1)
    }

    override fun display(context: View, imageView: ImageView, url: String) {
        display(context,imageView,url,-1)
    }

    override fun display(context: Any, imageView: ImageView, uri: Uri) {
        // empty
    }


}
