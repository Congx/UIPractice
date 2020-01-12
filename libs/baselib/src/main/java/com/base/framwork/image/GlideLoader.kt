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

    override var config: IimageLoader.Config = IimageLoader.Config()

    override fun config(config: IimageLoader.Config) {
        this.config
    }

    override fun isNeedLoadingImg(boolean: Boolean) {
        this.config.isNeedLoadingImg = boolean
    }

    override fun isNeedLoadFault(boolean: Boolean) {
        this.config.isNeedLoadError = boolean
    }

    private fun displayRealy(
        context: Any,
        imageView: ImageView,
        url: String,
        config: IimageLoader.Config
    ) : IimageLoader {
        try { // 以防万一
            val requestOptions:RequestOptions = if (config.radio > 0) {
                RequestOptions.bitmapTransform(RoundedCorners(config.radio))
            }else {
                RequestOptions()
            }
            // 大于0 肯定是有加载占位图
            if(config.progressId != -1) {
                requestOptions.placeholder(config.progressId)
            }else {
                // 在没有设置的占位id的情况下，判断开关是否开启+id是否全局设置，默认关闭
                if(ImageLoader.globeProgressId != -1 && config.isNeedLoadingImg)
                    requestOptions.placeholder(ImageLoader.globeProgressId)
            }
            // 逻辑同上
            if(config.errorId != -1) {
                config.errorId?.let { requestOptions.placeholder(it) }
            }else {
                if(ImageLoader.globeErrorId != -1 && config.isNeedLoadError)
                    requestOptions.placeholder(ImageLoader.globeErrorId)
            }

            if(context is Activity && isValidContextForGlide(context)) {
                Glide.with(context).load(url).apply(requestOptions)
                    .apply(requestOptions).into(imageView)
                return this
            }

            if(context is Fragment && isValidContextForGlide(context)) {
                Glide.with(context).load(url).apply(requestOptions).into(imageView)
                return this
            }

            if(context is View && isValidContextForGlide(context.context)) {
                Glide.with(context).load(url).apply(requestOptions).into(imageView)
                return this
            }

            // 不建议使用
            if (context is Application) {
                Glide.with(context).load(url).apply(requestOptions).into(imageView)
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }

        return this

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
    ) : IimageLoader {
        if(progressId > 0) {
            config.progressId = progressId
        }
        if (errorId > 0) {
            config.errorId = errorId
        }
        if (radio > 0) {
            config.radio = radio
        }
        return displayRealy(context,imageView,url,config)
    }

    override fun display(
        context: Context,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int
    ) : IimageLoader {
        return display(context,imageView,url,progressId,errorId,0)
    }

    override fun display(context: Context, imageView: ImageView, url: String, progressId: Int) : IimageLoader {
        return display(context,imageView,url,progressId,-1)
    }

    override fun display(context: Context, imageView: ImageView, url: String) : IimageLoader {
        return display(context,imageView,url,-1)
    }

    override fun display(context: Context, imageView: ImageView, url: String, config: IimageLoader.Config): IimageLoader {
        return displayRealy(context,imageView,url,config)
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
    ) : IimageLoader {
        config.progressId = progressId
        config.errorId = errorId
        config.radio = radio
        return displayRealy(context,imageView,url,config)
    }

    override fun display(
        context: Fragment,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int
    ) : IimageLoader {
        return display(context,imageView,url,progressId,errorId,0)
    }

    override fun display(context: Fragment, imageView: ImageView, url: String, progressId: Int) : IimageLoader {
        return display(context,imageView,url,progressId,-1)
    }

    override fun display(context: Fragment, imageView: ImageView, url: String) : IimageLoader {
        return display(context,imageView,url,-1)
    }

    override fun display(context: Fragment, imageView: ImageView, url: String, config: IimageLoader.Config): IimageLoader {
        return displayRealy(context,imageView,url,config)
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
    ) : IimageLoader {
        config.progressId = progressId
        config.errorId = errorId
        config.radio = radio
        return displayRealy(context,imageView,url,config)
    }

    override fun display(
        context: View,
        imageView: ImageView,
        url: String,
        progressId: Int,
        errorId: Int
    ) : IimageLoader {
        return display(context,imageView,url,progressId,errorId,0)
    }

    override fun display(context: View, imageView: ImageView, url: String, progressId: Int) : IimageLoader {
        return display(context,imageView,url,progressId,-1)
    }

    override fun display(context: View, imageView: ImageView, url: String) : IimageLoader {
        return display(context,imageView,url,-1)
    }

    override fun display(context: View, imageView: ImageView, url: String, config: IimageLoader.Config): IimageLoader {
        return displayRealy(context,imageView,url,config)
    }

    override fun display(context: Any, imageView: ImageView, uri: Uri){
        // empty
    }


}
