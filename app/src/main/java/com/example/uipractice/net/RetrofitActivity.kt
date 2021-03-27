package com.example.uipractice.net

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.base.framwork.activity.XBaseActivity
import com.base.net.RetrofitServer
import com.base.rxjavalib.iOtransformer
import com.example.uipractice.R
import com.example.uipractice.api.AppApi
import com.example.uipractice.basepractice.viewmodule.StatusViewModule
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class RetrofitActivity : XBaseActivity() {

    val viewmodle by viewModels<StatusViewModule>()

    @SuppressLint("AutoDispose")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)
//        viewmodle.loaddata()
        RetrofitServer.defaultRetrofitClient.create(AppApi::class.java)
            .getPublishList()
            .iOtransformer()
            .subscribe {

        }

        var client = OkHttpClient()
        //使用body传递String对象写法
//        val mediaType = MediaType.parse("application/json; charset=UTF-8")
        val mediaType: MediaType? = "application/json; charset=UTF-8".toMediaTypeOrNull()

        //避免手写错误最好使用三方库
        val bodyContent = "{\"name\":\"zhangsan\",\"age\":\"20\"}"
//        val body: RequestBody = RequestBody.create(mediaType, bodyContent)
        val requestBody = bodyContent.toRequestBody(mediaType)

        var formData = FormBody.Builder().add("xxx","dddd").build()

        MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("sss","",formData)

        var request = Request
            .Builder()
            .url("http:://www.baidu.com")
            .post(requestBody)
            .post(formData)
            .build()


        client.newCall(request).execute()

        client.newCall(request).enqueue(object :Callback {

            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("TAG", "OnResponse: " + response.body.toString())
                response.body?.byteStream()
            }

        })
    }
}
