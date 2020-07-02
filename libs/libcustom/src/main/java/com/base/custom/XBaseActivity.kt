package com.base.custom

import android.os.Bundle
import androidx.lifecycle.Observer
import com.base.custom.BaseViewModel.UILiveData.TYPE.*
import com.base.framwork.activity.AbstractActivity
import com.base.framwork.activity.BaseActivity

/**
 * @date 2020-01-05
 * @Author luffy
 * @description  App相关的Activity基类，不同的app自己拷贝一份
 *
 * 这里 暂时空实现，便于应对基类需要扩展的情况 而不需要扩展[BaseActivity]
 * 作用等于[AbstractActivity]
 *
 * 否则由于基类的一些扩展或者一些改动，必须去更改基本类，影响太大
 */
@Deprecated("")
open class XBaseActivity : BaseActivity() {

}
