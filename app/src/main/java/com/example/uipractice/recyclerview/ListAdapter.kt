package com.example.uipractice.recyclerview

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.uipractice.R

var default = mutableListOf(
    "1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18",
    "19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36"
)

class ListAdapter(var list: MutableList<String> = default):BaseQuickAdapter<String,BaseViewHolder>(R.layout.string_list_item,list) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.getView<TextView>(R.id.textView).text = item
    }
}