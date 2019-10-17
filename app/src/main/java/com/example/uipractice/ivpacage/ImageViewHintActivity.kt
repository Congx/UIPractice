package com.example.uipractice.ivpacage

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_image_hint.*

open class ImageViewHintActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_hint)
        image.setColorFilter(Color.RED,PorterDuff.Mode.SRC_IN)
    }
}
