package com.example.uipractice.base

import android.view.View
import androidx.fragment.app.Fragment
import com.example.uipractice.R
import kotlinx.android.synthetic.main.item_base_list.view.*


abstract class FragmentItemListActivity : BaseListActivity<ItemBean<out Fragment>>() {

    override fun bindData(itemView: View, item: ItemBean<out Fragment>?) {
        itemView.tv_content.text = item?.content
    }

    override fun onItemClick(itemView: View, item: ItemBean<out Fragment>?) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(android.R.id.content, item!!.clazz, null)
            .commit()
    }

    override fun getListData(): ArrayList<out ItemBean<out Fragment>>? {
        return null
    }
}
