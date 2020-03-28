package com.example.uipractice.anim

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_anim_test.*

class AnimTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_test)

        val scale = ObjectAnimator.ofFloat(target, "scaleX", 1f, 2f)
        scale.duration = 3000

        val scale2 = ObjectAnimator.ofFloat(target, "scaleX", 2f, 1f)
        scale2.duration = 3000

        btn1.setOnClickListener{
            scale.start()
        }

        btn2.setOnClickListener {
            scale.reverse()
        }
    }
}
