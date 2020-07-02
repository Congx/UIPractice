package com.base.framwork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.base.framwork.p.BaseViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * @author Levi
 * @date 2019/3/27
 * @desc
 */
public abstract class VMBaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends XBaseFragment {

    protected V binding;
    protected VM viewModel;
    private int viewModelId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getContentViewLayout(inflater, container, savedInstanceState), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化ViewModel dataBinding
        initViewDataBinding();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding.unbind();
        }
    }

    public void initViewDataBinding() {
        viewModel = getViewModel();
        viewModelId = getViewModelId();
        binding.setVariable(viewModelId, viewModel);
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int getContentViewLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    public abstract int getViewModelId();

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    public abstract VM getViewModel();


    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends BaseViewModel> T createViewModel(Class<T> cls) {
        return createViewModel(this, cls);
    }

    public <T extends BaseViewModel> T createViewModel(ViewModelStoreOwner owner, Class<T> cls) {
        return new ViewModelProvider(owner).get(cls);
    }

}
