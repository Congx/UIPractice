package com.example.uipractice.ui.nestedscroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_cordinglayout_behavior.*

/**
 * 基本用法
 */
open class NestedBehaviorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cordinglayout_behavior)

        depentent.setOnClickListener{
                v->
            v.translationY = v.translationY+10
//                v->ViewCompat.offsetTopAndBottom(v, 10)
//            v->v.offsetTopAndBottom(10)

//            scrollView2.fling(5000)
        }

    }
}
