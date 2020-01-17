package com.base.framwork.interfaces

import android.view.View
import com.base.framwork.ui.statusview.core.LoadService

/**
 * @date 2020-01-05
 * @Author luffy
 * @description
 */
open abstract class AbStatusView:IStatusView {

    abstract fun register(target:View, callback:((View)->Unit))


}