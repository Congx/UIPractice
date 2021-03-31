package com.example.uipractice.image

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_glide.*

class GlideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide)
        var url = "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimg.zmtc.com%2F2019%2F0806%2F20190806060939240.jpg&imgrefurl=https%3A%2F%2Fwww.zmtc.com%2Fbizhi%2F387.html&tbnid=IcGT8fbzBfVANM&vet=10CEMQMyiFAWoXChMIgJzP9rLS7wIVAAAAAB0AAAAAEAc..i&docid=iizdsWRKyVf7gM&w=1080&h=1920&q=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&ved=0CEMQMyiFAWoXChMIgJzP9rLS7wIVAAAAAB0AAAAAEAc"
//        var url = R.mipmap.aaa
        var options = RequestOptions().transform()
        Glide.with(this).load(url).transition(DrawableTransitionOptions.withCrossFade())
            .apply(options)
            .circleCrop().into(imageView)
    }
}