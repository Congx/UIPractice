package com.example.uipractice.ui.others

import android.graphics.Outline
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_others_u_i.*

class OthersUIActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others_u_i)
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
//                outline?.setRoundRect(0, 0, view!!.measuredWidth, view.measuredHeight, 30f)
                val background = view!!.background
                if (background != null) {
                    background.getOutline(outline)
                } else {
                    outline?.setRoundRect(0, 0, view!!.measuredWidth, view.measuredHeight, 30f)
                }

            }

        }
//        view.clipBounds = Rect(0, 0, view!!.measuredWidth, view.measuredHeight)
        view.clipToOutline = true
    }
}
