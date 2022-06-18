package com.example.uipractice.rxjava

import android.app.Activity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import java.util.*

class RxJavaActivity : BaseItemListActivity<Activity>() {

    var list: ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("autodispose", AutoDisposeActivity::class.java),
        ItemBean("背压", BackPressActivity::class.java),
        ItemBean("线程切换", ThreadActivity::class.java),
        ItemBean("rxjava冷热", HotObserverActivity::class.java),
        ItemBean("练习测试", RxTestActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }
}
