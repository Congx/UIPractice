package com.example.uipractice.recyclerview.layoutmanger

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uipractice.R
import com.example.uipractice.recyclerview.ListAdapter
import kotlinx.android.synthetic.main.activity_layout_manager.*


class LayoutManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_manager)

//        recyclerView.adapter = RecyclerAdapter(initData())
//        recyclerView.layoutManager = CustomLayoutManger(1.5f, 0.5f)

        recyclerView.adapter = ListAdapter()

//        recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,true)
        recyclerView.layoutManager = LinearLayoutManager(this)

//        recyclerView.layoutManager = PickerLayoutManager(this,recyclerView,RecyclerView.VERTICAL,false,3,0.4f,true)
        recyclerView.addItemDecoration(DividerItemDecoration(this,RecyclerView.VERTICAL))

//        var snapHelper = LinearSnapHelper()
        // 第一个位置的helper
//        var snapHelper = StartPosSnaphelper()
//        snapHelper.attachToRecyclerView(recyclerView)

//声明一个Callback
        //声明一个Callback
        val callback: ItemTouchHelper.Callback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
                override fun onMove(
                    @NonNull recyclerView: RecyclerView,
                    @NonNull viewHolder: RecyclerView.ViewHolder,
                    @NonNull target: RecyclerView.ViewHolder
                ): Boolean {
                    //拖拽处理
                    return true
                }

                override fun onSwiped(
                    @NonNull viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    //滑动处理
                }

            }
        //创建helper对象
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun initData(): MutableList<Bean> {
        var list = mutableListOf<Bean>()
        for (i in 0..99) {
            list.add(
                Bean(
                    "content = $i",
                    Color.parseColor(ColorUtils.generateRandomColor())
                )
            )
        }

        return list
    }
}