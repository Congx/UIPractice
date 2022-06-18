package com.example.uipractice.net

import android.app.Activity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean

class NetListActivity : BaseItemListActivity<Activity>() {

    var list:ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("websocket", WebsocketActivity::class.java),
        ItemBean("retrofit", RetrofitActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }

}
