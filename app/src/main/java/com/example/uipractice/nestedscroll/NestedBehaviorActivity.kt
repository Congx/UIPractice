package com.example.uipractice.nestedscroll

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_cordinglayout_behavior.*
import kotlinx.android.synthetic.main.activity_image_hint.*

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
