package com.example.uipractice.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_main.*


abstract class BaseListFragment<T> : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = object : BaseListAdapter<T>(getListData()) {
            override fun onItemClick(itemView: View, item: T?) {
                this@BaseListFragment.onItemClick(itemView,item)
            }

            override fun bindData(itemView: View, item: T?) {
                this@BaseListFragment.bindData(itemView,item)
            }

        }
    }

    abstract fun onItemClick(itemView: View, item: T?)
    abstract fun bindData(itemView: View, item: T?)


    abstract fun getListData(): ArrayList<T>?
}
