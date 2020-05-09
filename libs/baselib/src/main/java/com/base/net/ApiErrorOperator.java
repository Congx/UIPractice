package com.base.net;

import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @date 25/3/2020
 * @Author luffy
 * @description
 */
public final class ApiErrorOperator<T> implements ObservableOperator<T, T> {


    @Override
    public Observer<? super T> apply(final Observer<? super T> observer) {
        return new Observer<T>() {
            @Override
            public void onComplete() {
                observer.onComplete();
            }

            @Override
            public void onError(final Throwable e) {
                observer.onError(ExceptionHandle.handleException(e));
            }

            @Override
            public void onSubscribe(Disposable d) {
                observer.onSubscribe(d);
            }

            @Override
            public void onNext(T response) {
                observer.onNext(response);
            }
        };
    }
}
