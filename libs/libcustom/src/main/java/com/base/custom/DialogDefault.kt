package com.base.custom

import android.app.Activity
import android.widget.Toast
import com.base.framwork.interfaces.DialogLoading

/**
 * @date 2020-01-10
 * @Author luffy
 * @description
 */
@Deprecated("")
class DialogDefault(private var activity: Activity) : DialogLoading() {

    override fun hideLoading() {
        Toast.makeText(activity!!, "hideLoading", Toast.LENGTH_LONG).show()
    }

    override fun showLoading() {
        Toast.makeText(activity, "showLoading", Toast.LENGTH_LONG).show()
    }
}