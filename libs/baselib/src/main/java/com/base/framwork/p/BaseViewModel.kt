package com.base.framwork.p

import android.os.Looper
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.lp.base.viewmodel.LifecycleViewModel


/**
 * 封装和 XBaseFragment/XBaseActivity的UI交互逻辑，参照[BaseViewModel] 的控制逻
 */
open class BaseViewModel : LifecycleViewModel() {

    val ui: UILiveData? by lazy {
        UILiveData()
    }

    /**
     * 简单的一些ViewMoedel 交互,暂时写这么多，需要自己扩展
     */
    class UILiveData {

        /**
         * loading 之类的LiveData，消息类型是handle的Message类，方便扩展
         */
        var statusLiveData: MutableLiveData<Message>? = MutableLiveData<Message>()

        /**
         * @param type dialog 显示的模式，允不允许点击返回键消失 详情： [ todo ]
         * @param messageFactory 允许自己构建 message 类
         *
         * ！！！！！！ 注意 下面所有方法都有默认参数，以防扩展用，一般用默认就行 ！！！！！
         *
         */
        @JvmOverloads
        open fun showLoadingDialog(type:Int = 0, messageFactory: (() -> Message)? = null) {
            var message = if (messageFactory != null) {
                messageFactory()
            }else {
                Message()
            }
            message.arg1 = type
            message.what = TYPE.SHOWLOADINGDIALOG
            statusLiveData?.postValue(message)
        }

        /**
         * @param messageFactory  参考[showLoadingDialog]
         * dismiss loadingDialog
         */
        @JvmOverloads
        open fun hideLoadingDialog(messageFactory: (() -> Message)? = null) {
            var message = if (messageFactory != null) {
                messageFactory()
            }else {
                Message()
            }
            message.what = TYPE.HIDELOADINGDIALOG
            statusLiveData?.postValue(message)
        }

        /**
         * loading页面
         * @param messageFactory  参考[showLoadingDialog]
         * 一般控制 [com.android.base.list.StatusViewLayout]
         * 在 [com.lp.base.fragment.XBaseFragment.showLoading]
         * 或 [com.lp.base.activity.XBaseActivity.showLoading] 回调
         */
        @JvmOverloads
        open fun showLoading(messageFactory: (() -> Message)? = null) {
            var message = if (messageFactory != null) {
                messageFactory()
            }else {
                Message()
            }
            message.what = TYPE.SHOWLOADING
            statusLiveData?.postValue(message)
        }

        /**
         * 显示正常页面
         * @param messageFactory  参考[showLoadingDialog]
         * 一般控制 [com.android.base.list.StatusViewLayout]
         * 在 [com.lp.base.fragment.XBaseFragment.showContent]
         * 或 [com.lp.base.activity.XBaseActivity.showContent] 回调
         */
        @JvmOverloads
        open fun showContent(messageFactory: (() -> Message)? = null) {
            var message = if (messageFactory != null) {
                messageFactory()
            }else {
                Message()
            }
            message.what = TYPE.SHOWCONTENT
            statusLiveData?.postValue(message)
        }

        /**
         * 加载错误
         * @param messageFactory  参考[showLoadingDialog]
         * 一般控制 [com.android.base.list.StatusViewLayout]
         * 在 [com.lp.base.fragment.XBaseFragment.showError]
         * 或 [com.lp.base.activity.XBaseActivity.showError] 回调
         */
        @JvmOverloads
        open fun showError(messageFactory: (() -> Message)? = null) {
            var message = if (messageFactory != null) {
                messageFactory()
            }else {
                Message()
            }
            message.what = TYPE.SHOWERROR
            statusLiveData?.postValue(message)
        }

        /**
         * 空数据
         * @param messageFactory  参考[showLoadingDialog]
         * 一般控制 [com.android.base.list.StatusViewLayout]
         * 在 [com.lp.base.fragment.XBaseFragment.showEmpty]
         * 或 [com.lp.base.activity.XBaseActivity.showEmpty] 回调
         */
        @JvmOverloads
        open fun showEmpty(messageFactory: (() -> Message)? = null) {
            var message = if (messageFactory != null) {
                messageFactory()
            }else {
                Message()
            }
            message.what = TYPE.SHOWEMPTY
            statusLiveData?.postValue(message)
        }

        /**
         * 无网络
         * @param messageFactory  参考[showLoadingDialog]
         * 一般控制 [com.android.base.list.StatusViewLayout]
         * 在 [com.lp.base.fragment.XBaseFragment.showNoNet]
         * 或 [com.lp.base.activity.XBaseActivity.showNoNet] 回调
         */
        @JvmOverloads
        open fun showNoNetwork(messageFactory: (() -> Message)? = null) {
            var message = if (messageFactory != null) {
                messageFactory()
            }else {
                Message()
            }
            message.what = TYPE.SHOWNONETWORK
            statusLiveData?.postValue(message)
        }

        /**
         * back 键事件按钮
         */
        open fun finish() {
            var message = Message()
            message.what = TYPE.FINISH
            statusLiveData?.value = message
        }

        /**
         * back 键事件按钮
         */
        open fun backPressed() {
            var message = Message()
            message.what = TYPE.BACKPRESS
            statusLiveData?.value = message
        }

        /**
         * 消息弹窗
         * @param messageFactory  参考[showLoadingDialog]
         * 一般控制 [com.android.base.list.StatusViewLayout]
         * 在 [com.lp.base.fragment.XBaseFragment.showToast]
         * 或 [com.lp.base.activity.XBaseActivity.showToast] 回调
         * 
         * @param content toast 内容
         * @param type toast 种类，有成功、错误、警告、消息四种 [com.android.ui.toast.ToastAlertUtil]
         *             默认error
         */
        @JvmOverloads
        open fun showToast(content:String?, type: Int = 0, messageFactory: (() -> Message)? = null) {
            var message = if (messageFactory != null) {
                messageFactory()
            }else {
                Message()
            }
            message.what = TYPE.TOAST
            message.obj = content
            message.arg1 = type
            statusLiveData?.postValue(message)
        }

        /**
         * 自定义消息  [TYPE]中的值已经被占用，不要使用其中的值
         * 在 [com.lp.base.fragment.XBaseFragment.onCustomMessage]
         * 或 [com.lp.base.activity.XBaseActivity.onCustomMessage] 回调
         */
        open fun customMessage(messageFactory: (() -> Message)) {
            statusLiveData?.postValue(messageFactory())
        }


        fun handleMessage(statusLiveData: MutableLiveData<Message>?, message: Message) {
            if (isMainThread()) {
                statusLiveData?.value = message
            }else {
                statusLiveData?.postValue(message)
            }
        }

        fun isMainThread(): Boolean {
            return Looper.getMainLooper().thread === Thread.currentThread()
        }

        /**
         * 内置的消息类型 自定义消息用[UILiveData.customMessage]
         */
        internal interface TYPE {
            companion object {
                const val SHOWLOADINGDIALOG = 0x1000
                const val HIDELOADINGDIALOG = 0x1001
                const val SHOWLOADING = 0x1002
                const val SHOWCONTENT = 0x1003
                const val SHOWEMPTY = 0x1004
                const val SHOWERROR = 0x1005
                const val SHOWNONETWORK = 0x1006
                const val BACKPRESS = 0x1007
                const val FINISH = 0x1008
                const val TOAST = 0x1009
            }
        }
    }

}