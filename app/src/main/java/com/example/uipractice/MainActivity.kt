package com.example.uipractice

import android.app.Activity
import com.example.uipractice.anim.AnimListActivity
import com.example.uipractice.architecture.ArchitectureActivity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.basepractice.BaseLibActivity
import com.example.uipractice.camera.Camera2Activity
import com.example.uipractice.camera.CameraActivity
import com.example.uipractice.camera.CameraXActivity
import com.example.uipractice.fragment.FragmentListActivity
import com.example.uipractice.image.GlideActivity
import com.example.uipractice.ndk.NDKActivity
import com.example.uipractice.net.NetListActivity
import com.example.uipractice.opengl.OpenglMainActivity
import com.example.uipractice.others.FileProviderActivity
import com.example.uipractice.plugin.PluginTestActivity
import com.example.uipractice.rxjava.RxJavaActivity
import com.example.uipractice.ui.UIActivity
import com.example.uipractice.window.WindowTestActivity

class MainActivity : BaseItemListActivity<Activity>() {

    var list:ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("ui相关", UIActivity::class.java),
        ItemBean("架构组件", ArchitectureActivity::class.java),
        ItemBean("rxJava", RxJavaActivity::class.java),
        ItemBean("base库的一些用法", BaseLibActivity::class.java),
        ItemBean("fragment", FragmentListActivity::class.java),
        ItemBean("动画", AnimListActivity::class.java),
        ItemBean("网络", NetListActivity::class.java),
        ItemBean("FileProvider", FileProviderActivity::class.java),
        ItemBean("NDK", NDKActivity::class.java),
        ItemBean("camera", CameraActivity::class.java),
        ItemBean("camera2", Camera2Activity::class.java),
        ItemBean("camerax", CameraXActivity::class.java),
        ItemBean("window", WindowTestActivity::class.java),
        ItemBean("插件化", PluginTestActivity::class.java),
        ItemBean("图片相关", GlideActivity::class.java),
        ItemBean("opengl", OpenglMainActivity::class.java),
        ItemBean("新架构", NewFragmentListActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }


}
