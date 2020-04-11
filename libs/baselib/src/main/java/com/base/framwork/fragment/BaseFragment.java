package com.base.framwork.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.base.framwork.view.ILifeProcessor;

import org.jetbrains.annotations.NotNull;

/**
 * @date 2019-12-09
 * @Author luffy
 * @description
 */
public abstract class BaseFragment extends Fragment implements ILifeProcessor {

    private View rootView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams(null);
    }

    /**
     * @param intent 在fragment 中总是null
     */
    public void initParams(@NotNull Intent intent) {

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
        initData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @NotNull
    @Override
    public View generateViewLayout() {
        return null;
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }

    @Override
    public void setStatusBar() {
        // empty
        // 一般activity 已经有实现了，如果单独fragment需要，重写
    }

//    /**
//     * 用新的Androidx新的懒加载方案，旧方案用{@link AbstractLazyLoadFragment}
//     * 不过旧方案都过时了，google不建议使用
//     * 是否懒加载，不需要重写
//     * @return 默认为懒加载
//     */
//    public boolean isLazyLoad() {
//        return true;
//    }

    /**
     * 提供给java 用，尽早用kotlin吧
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T findViewById(@IdRes int id) {
        return rootView.findViewById(id);
    }

}
