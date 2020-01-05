package com.base.framwork.fragment;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.base.framwork.activity.BaseViewActivity;
import com.base.framwork.activity.BaseViewDelegate;
import com.base.framwork.p.LifyCycleViewModel;
import com.base.framwork.view.IBaseView;

import org.jetbrains.annotations.NotNull;

/**
 * @date 2019-12-08
 * @Author luffy
 * @description 分装与P层交互的一些逻辑和通用的UI的部分实现
 */
public abstract class BaseViewFragment<T extends LifyCycleViewModel> extends BaseFragment implements IBaseView {

    // 具体实现的代理类
    protected  BaseViewDelegate<T> mDelegate;


    @Override
    @CallSuper
    public void initView() {
        createViewModel();
    }

    @NonNull
    public abstract Class<T> getViewModelClass();

    /**
     * 默认实现无参数的构造实例化,默认fragment的生命周期和fragment绑定
     * 如果要和activity绑定
     * @return
     */
    public T createViewModel() {
        if(bindViewModelWithActivity()) {
            FragmentActivity activity = getActivity();
            return getViewDelegate().createViewModel(activity,getViewModelClass());
        }
        return getViewDelegate().createViewModel(this,getViewModelClass());
    }

    /**
     * ViewModel绑定策略
     * @return  true 和Activity的声明周期绑定
     *          false 和fragment绑定
     *          默认和fragment绑定，自己修改需要复写
     */
    public boolean bindViewModelWithActivity() {
        return false;
    }

    /**
     * 获取和实例化代理类
     * @return
     */
    @NonNull
    public BaseViewDelegate<T> getViewDelegate() {
        if(mDelegate == null) {
            mDelegate =  BaseViewDelegate.Companion.create((BaseViewActivity) getActivity());
        }
        return mDelegate;
    }

    public T getViewModel() {
        return getViewDelegate().getViewModule();
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
    public void showLoading(@org.jetbrains.annotations.Nullable String message) {
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
