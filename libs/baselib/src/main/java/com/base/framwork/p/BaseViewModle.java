package com.base.framwork.p;

import com.base.rxjavalib.RxUtils;
import com.uber.autodispose.AutoDisposeConverter;


/**
 * @date 2019-12-12
 * @Author luffy
 * @description
 */
public class BaseViewModle extends LifyCycleViewModel {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public <T> AutoDisposeConverter<T> bindLifecycle() {
        return RxUtils.bindLifecycle(this);
    }
}
