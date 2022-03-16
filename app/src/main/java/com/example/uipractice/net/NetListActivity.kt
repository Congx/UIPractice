package com.example.uipractice.net

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean

class NetListActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("websocket", WebsocketActivity::class.java),
        ItemBean("retrofit", RetrofitActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }

}
