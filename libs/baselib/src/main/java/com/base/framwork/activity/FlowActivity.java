package com.base.framwork.activity;

import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.base.framwork.R;
import com.base.framwork.ui.utils.StatusBarUtil;
import com.base.framwork.view.ILifeProcessor;

/**
 * @date 2020-01-03
 * @Author luffy
 * @description 规范开发的方法 其他的，暂时还没想好
 */
public abstract class FlowActivity extends AppCompatActivity implements ILifeProcessor{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams(getIntent());
        if (generateIdLayout() > 0) {
            setContentView(generateIdLayout());
        } else if (generateViewLayout() != null) {
            setContentView(generateViewLayout());
        }
        setStatusBar();
        initView();
        initData();
        initEvent();
    }

    /**
     * 状态栏跟随主题变色
     */
    @Override
    public void setStatusBar() {
        TypedValue typedValue = new TypedValue();
        boolean b = getTheme().resolveAttribute(R.attr.statuBarColor, typedValue, true);
        if (b) {
            int color = typedValue.data;
            StatusBarUtil.setColor(this, color);
        }
    }
}
