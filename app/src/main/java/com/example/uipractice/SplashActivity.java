package com.example.uipractice;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.base.framwork.activity.BaseActivity;
import com.lp.base.viewmodel.LifecycleViewModel;


/**
 * @date 2019-12-09
 * @Author luffy
 * @description
 */
public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_splash);
    }

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

}
