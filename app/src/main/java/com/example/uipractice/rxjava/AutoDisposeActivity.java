package com.example.uipractice.rxjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.base.framwork.ui.utils.StatusBarUtil;
import com.base.rxjavalib.RxUtils;
import com.example.uipractice.R;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class AutoDisposeActivity extends AppCompatActivity {
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_dispose);
        StatusBarUtil.setTranslucentForImageView(this,0,null);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // 全屏显示，隐藏状态栏和导航栏，拉出状态栏和导航栏显示一会儿后消失。
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.INVISIBLE
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        );

        findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index % 2 == 0) {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.INVISIBLE
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );
                }
                else {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                           | View.SYSTEM_UI_FLAG_VISIBLE
//                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.INVISIBLE
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );}

                index++;
            }
        });

        Disposable disposable = Observable.just("str")
                .doOnDispose(()->{Log.e("AutoDisposeActivity","dispose");})
                .as(RxUtils.bindLifecycle(this))
                .subscribe(str -> {
                    Log.e("AutoDisposeActivity",str);
                });
    }

}