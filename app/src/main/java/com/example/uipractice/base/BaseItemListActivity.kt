package com.example.uipractice.base

import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.item_base_list.view.*


abstract class BaseItemListActivity : BaseListActivity<ItemBean>() {

    override fun bindData(itemView: View, item: ItemBean?) {
        itemView.tv_content.text = item?.content
    }

    override fun onItemClick(itemView: View, item: ItemBean?) {
        startActivity(Intent(this,item?.clazz))
    }
}
