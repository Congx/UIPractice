package com.example.uipractice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.base.framwork.activity.BaseViewActivity;
import com.base.framwork.image.IimageLoader;
import com.base.framwork.image.ImageLoader;
import com.base.framwork.p.LifyCycleViewModel;

import io.reactivex.Observable;


/**
 * @date 2019-12-09
 * @Author luffy
 * @description
 */
public class SplashActivity extends BaseViewActivity {

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(()-> {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        },2000);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void setStatusBar() {
//        if (JudgeNotchUtils.hasNotchScreen(this)) {
//            // 显示状态栏
////            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            StatusBarUtil.setTranslucentForImageView(this,null);
//        }else {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
////        }
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            getWindow().setStatusBarColor(Color.TRANSPARENT);
////            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
////            getWindow()
////                    .getDecorView()
////                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
////        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            getWindow()
////                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////        }
        //全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        WindowManager.LayoutParams lp = getWindow().getAttributes();

        //下面图1
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        //下面图2
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        //下面图3
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        getWindow().setAttributes(lp);

    }

    @NonNull
    @Override
    public Class getViewModelClass() {
        return LifyCycleViewModel.class;
    }

    @Override
    public int generateIdLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void initEvent() {

    }

    @SuppressLint("AutoDispose")
    @Override
    public void initData() {
//        Observable.just("")
//                .flatMap(t-> Observable.empty()).subscribe(t-> {
//
//        });
//        String imgUrl = "http://p15.qhimg.com/bdm/720_444_0/t01b12dfd7f42342197.jpg";
//        ImageView image = findViewById(R.id.image);
//        IimageLoader request = ImageLoader.getRequest();
//        request.getConfig().setProgressId(R.drawable.ic_svg_chevron_back_solid);
//        request.getConfig().setErrorId(R.drawable.ic_launcher_background);
//        request.display(image.getContext(),image,imgUrl,R.mipmap.app_logo);
    }

    @Override
    public void initView() {

    }
}
