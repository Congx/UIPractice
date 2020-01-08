package com.base.framwork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.base.framwork.p.LifyCycleViewModel;
import com.base.framwork.view.IBaseView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @date 2019-12-08
 * @Author luffy
 * @description 分装与P层交互的一些逻辑和通用的UI的部分实现,具体实现通过代理方式实现，
 *              delegate 里面封装了所有通用UI的样式，可以支持不同程度的自定义，可以完全自定义
 *              delegate，也可以自定义delegate的部分实现，不需要再次继承BaseViewActivity，组合由于继承
 */
public abstract class BaseViewActivity<T extends LifyCycleViewModel> extends FlowActivity implements IBaseView {

    // 具体实现的代理类,这样设计便于扩展
    private BaseViewDelegate<T> mDelegate;

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        createViewModel();
        super.onCreate(savedInstanceState);
    }

    /**
     * 这个方法，不用强制实现，并不是每个都需要用intent值
     * 默认空实现
     * @param intent
     */
    @Override
    public void initParams(@NotNull Intent intent) {

    }

    /**
     * 默认以layoutResId形式来填充，用{@link FlowActivity#generateIdLayout()}，如果需要重新这个方法
     *
     * 默认空实现
     * @return
     */
    @NotNull
    @Override
    public View generateViewLayout() {
        return null;
    }

    @NonNull
    public abstract Class<T> getViewModelClass();

    /**
     * 默认实现无参数的构造实例化
     * @return
     */
    public T createViewModel() {
        return getViewDelegate().createViewModel(this,getViewModelClass());
    }

    public T getViewModel() {
        T viewModule = getViewDelegate().getViewModule();
        if(viewModule == null) {
            viewModule = createViewModel();
        }
        return viewModule;
    }

    /**
     * 获取和实例化代理类
     * @return
     */
    @NonNull
    public BaseViewDelegate<T> getViewDelegate() {
        if(mDelegate == null) {
            mDelegate =  BaseViewDelegate.Companion.create(this);
        }
        return mDelegate;
    }

    public void setDelegate(BaseViewDelegate<T> delegate) {
        this.mDelegate = delegate;
    }

    @Override
    public void showDialog(@NotNull String message) {
        getViewDelegate().showDialog(message);
    }

    @Override
    public void showDialog(@NotNull String message, int type) {
        getViewDelegate().showDialog(message,type);
    }

    @Override
    public void showToast(@NotNull String message) {
        getViewDelegate().showToast(message);
    }

    @Override
    public void showLoading() {
        getViewDelegate().showLoading();
    }

    @Override
    public void showLoading(@Nullable String message) {
        getViewDelegate().showLoading(message);
    }

    @Override
    public void hideLoading() {
        getViewDelegate().hideLoading();
    }

    @Override
    public void showNormal() {
        getViewDelegate().showNormal();
    }

    @Override
    public void showEmpty() {
        getViewDelegate().showEmpty();
    }

    @Override
    public void showError() {
        getViewDelegate().showError();
    }

    @Override
    public void showError(@NotNull String message) {
        getViewDelegate().showError(message);
    }

    @Override
    public void showError(@NotNull String message, int type) {
        getViewDelegate().showError(message,type);
    }

    @Override
    public void gotoLoginActivity() {
        getViewDelegate().gotoLoginActivity();
    }


}
