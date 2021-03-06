package com.base.framwork.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @date 2020-01-04
 * @Author luffy
 * @description 旧的懒加载方案，目前有androidx的新方案
 */
@Deprecated
public abstract class AbstractLazyLoadFragment extends Fragment {

    private boolean isViewCreated = false;//布局是否被创建
    private boolean isLoadData = false;//数据是否加载
    private boolean isFirstVisible = true;//是否第一次可见

    abstract protected void onLazyLoad();

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        isViewCreated = true;
    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(isFragmentVisible(this) && this.isAdded()){
            if (this.getParentFragment() == null || isFragmentVisible(this.getParentFragment())) {
                onLazyLoad();
                isLoadData = true;
                if(isFirstVisible)
                    isFirstVisible = false;
            }
        }
    }


    @Override
    @CallSuper
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isFragmentVisible(this) && !isLoadData && isViewCreated && this.isAdded()){
            onLazyLoad();
            isLoadData = true;
        }
    }


    @Override
    @CallSuper
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //只要通过 show+hide 方式控制 Fragment 的显隐，那么在第一次初始化后，Fragment 任何的生命周期方法都不会调用，只有 onHiddenChanged 方法会被调用
        //onHiddenChanged调用在Resumed之前，所以此时可能fragment被add, 但还没resumed
        if(!hidden && !this.isResumed())
            return;
        //使用hide和show时，fragment的所有生命周期方法都不会调用，除了onHiddenChanged（）
        if(!hidden && isFirstVisible && this.isAdded()){
            onLazyLoad();
            isFirstVisible = false;
        }
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        isLoadData = false;
        isFirstVisible = true;
    }

    /**
     * 当前Fragment是否可见
     */
    private boolean isFragmentVisible(Fragment fragment) {
        return !fragment.isHidden() && fragment.getUserVisibleHint();
    }
}
