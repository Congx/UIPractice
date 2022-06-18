package com.example.uipractice.base

import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.item_base_list.view.*


abstract class BaseItemListActivity<T> : BaseListActivity<ItemBean<out T>>() {

    override fun bindData(itemView: View, item: ItemBean<out T>?) {
        itemView.tv_content.text = item?.content
    }

    override fun onItemClick(itemView: View, item: ItemBean<out T>?) {
        startActivity(Intent(this,item?.clazz))
    }

    override fun getListData(): ArrayList<out ItemBean<out T>>? {
        return null
    }
}
