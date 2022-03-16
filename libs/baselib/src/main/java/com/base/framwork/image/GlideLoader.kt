package com.base.framwork.image

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.lang.IllegalArgumentException


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
            return !(context.isRemoving || context.isDetached)
        }
        if (context is Activity) {
            val activity = context as Activity?
            return !(activity!!.isDestroyed || activity.isFinishing)
        }

        if (context is ContextWrapper) {
            return isValidContextForGlide(getActivity(context.baseContext))
        }

        if (context is View) {
            return isValidContextForGlide(context.context)
        }

        if (context is Application) {
            return true
        }
        return true
    }

    fun getActivity(context: Context?): FragmentActivity? {
        var context = context
        if (context is FragmentActivity) {
            return context
        }
        while (context is ContextWrapper) {
            if (context is FragmentActivity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

    fun checkType(any: Any) {
        val b = (any is String || any is Int || any is File || any is Uri)
        if(!b) throw IllegalArgumentException("图片加载url参数不正者")
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
        url: Any,
        config: IimageLoader.Config
    ) : IimageLoader {
        checkType(url)
        if (!isValidContextForGlide(context)) return this
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

            if (url is Int || url is File || url is Uri) {
                var request: RequestManager? = null
                if (context is View) {
                    request = Glide.with(context)
                }else if (context is Activity) {
                    request = Glide.with(context)
                }else if (context is Fragment) {
                    request = Glide.with(context)
                }else if (context is Application) {
                    request = Glide.with(context)
                }else if (context is ContextWrapper) {
                    request = Glide.with(context)
                }
                request?.load(url)?.apply(requestOptions)?.into(imageView)
            }else if (url is String) {
                // 删除多余的 斜杠
                var urlt = UrlUtils.removeExtraSlashOfUrl(url)
                val selfGlideUrl = SelfGlideUrl(urlt)
                var request: RequestManager? = null
                if (context is View) {
                    request = Glide.with(context)
                }else if (context is Activity) {
                    request = Glide.with(context)
                }else if (context is Fragment) {
                    request = Glide.with(context)
                }else if (context is Application) {
                    request = Glide.with(context)
                }else if (context is ContextWrapper) {
                    request = Glide.with(context)
                }
                request?.load(selfGlideUrl)?.apply(requestOptions)?.into(imageView)
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
        url: Any,
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
        url: Any,
        progressId: Int,
        errorId: Int
    ) : IimageLoader {
        return display(context,imageView,url,progressId,errorId,0)
    }

    override fun display(context: Context, imageView: ImageView, url: Any, progressId: Int) : IimageLoader {
        return display(context,imageView,url,progressId,-1)
    }

    override fun display(context: Context, imageView: ImageView, url: Any) : IimageLoader {
        return display(context,imageView,url,-1)
    }

    override fun display(context: Context, imageView: ImageView, url: Any, config: IimageLoader.Config): IimageLoader {
        return displayRealy(context,imageView,url,config)
    }

    /**
     * fragment
     */
    override fun display(
        context: Fragment,
        imageView: ImageView,
        url: Any,
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
        url: Any,
        progressId: Int,
        errorId: Int
    ) : IimageLoader {
        return display(context,imageView,url,progressId,errorId,0)
    }

    override fun display(context: Fragment, imageView: ImageView, url: Any, progressId: Int) : IimageLoader {
        return display(context,imageView,url,progressId,-1)
    }

    override fun display(context: Fragment, imageView: ImageView, url: Any) : IimageLoader {
        return display(context,imageView,url,-1)
    }

    override fun display(context: Fragment, imageView: ImageView, url: Any, config: IimageLoader.Config): IimageLoader {
        return displayRealy(context,imageView,url,config)
    }

    /**
     * View
     */
    override fun display(
        context: View,
        imageView: ImageView,
        url: Any,
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
        url: Any,
        progressId: Int,
        errorId: Int
    ) : IimageLoader {
        return display(context,imageView,url,progressId,errorId,0)
    }

    override fun display(context: View, imageView: ImageView, url: Any, progressId: Int) : IimageLoader {
        return display(context,imageView,url,progressId,-1)
    }

    override fun display(context: View, imageView: ImageView, url: Any) : IimageLoader {
        return display(context,imageView,url,-1)
    }

    override fun display(context: View, imageView: ImageView, url: Any, config: IimageLoader.Config): IimageLoader {
        return displayRealy(context,imageView,url,config)
    }

    override fun display(context: Any, imageView: ImageView, uri: Uri) {
        displayRealy(context,imageView,uri,config)
    }


}
