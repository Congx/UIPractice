package com.base.rxjavalib;


import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * @date 2019-12-04
 * @Author luffy
 * @description
 */
public class RxBus {

    private final Subject bus;

    private RxBus() {
        bus = BehaviorSubject.create().toSerialized();
    }

    private static class Inner {
        protected static RxBus INSTANCE = new RxBus();
    }

    public static RxBus getInstance() {
        return Inner.INSTANCE;
    }

    /**
     * 发送消息
     *
     * @param object
     */
    public void post(Object object) {
        if(hasObservers()) {
            bus.onNext(object);
        }

    }

    /**
     * 接收消息
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return bus.hasObservers();
    }

}
