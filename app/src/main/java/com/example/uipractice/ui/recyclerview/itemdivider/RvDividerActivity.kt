package com.example.uipractice.ui.recyclerview.itemdivider

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.uipractice.R
import com.example.uipractice.base.BaseListAdapter
import com.example.uipractice.utils.TestDataUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class RvDividerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rv_divider)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        recyclerView.addItemDecoration(SimpleItemDecoration(this))

        recyclerView.adapter = object : BaseListAdapter<String>(getListData()) {
            override fun onItemClick(itemView: View, item: String?) {
                this@RvDividerActivity.onItemClick(itemView,item)
            }

            override fun bindData(itemView: View, item: String?) {
                this@RvDividerActivity.bindData(itemView,item)
            }

            override fun getItemRes(): Int {
                return R.layout.item_base_list_temp
            }

        }
    }

    private fun bindData(itemView: View, item: String?) {
        itemView.findViewById<TextView>(R.id.tv_content).text = item
    }

    private fun onItemClick(itemView: View, item: String?) {

    }

    private fun getListData(): ArrayList<String>? {
        return TestDataUtils.getListData()
    }
}
