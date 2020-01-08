package com.base.framwork.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.base.framwork.view.ILifeProcessor;

import org.jetbrains.annotations.NotNull;

/**
 * @date 2019-12-09
 * @Author luffy
 * @description Androidx 有了新的懒加载方案 结合setMaxLifecycle方法来使用
 */
public abstract class BaseFragment extends Fragment implements ILifeProcessor {

    private View rootView;
    private boolean isFirstLoad = true; // 是否第一次加载

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
    }

    public void initParams() {

    }

    @Override
    public void initParams(@NotNull Intent intent) {
        // empty
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (generateIdLayout() > 0) {
            rootView = inflater.inflate(generateIdLayout(),container,false);
        } else if (generateViewLayout() != null) {
            rootView = generateViewLayout();
        }
        setStatusBar();
        initView();
        initEvent();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad && isLazyLoad()) {
            // 将数据加载逻辑放到onResume()方法中
            initData();
            isFirstLoad = false;
        }else {
            initData();
        }
    }

    @NotNull
    @Override
    public View generateViewLayout() {
        return null;
    }

    @Override
    public void setStatusBar() {
        // empty
        // 一般activity 已经有实现了，如果单独fragment需要，重写
    }

    /**
     * 用新的Androidx新的懒加载方案，旧方案用{@link AbstractLazyLoadFragment}
     * 不过旧方案都过时了，google不建议使用
     * 是否懒加载，不需要重写
     * @return 默认为懒加载
     */
    public boolean isLazyLoad() {
        return true;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return rootView.findViewById(id);
    }

}
