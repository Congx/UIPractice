package com.example.uipractice.rxjava

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean

class RxJavaActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("autodispose", AutoDisposeActivity::class.java),
        ItemBean("背压", BackPressActivity::class.java),
        ItemBean("线程切换", ThreadActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }
}
