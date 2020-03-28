package com.base.framwork.p;

import androidx.lifecycle.MutableLiveData;

import com.base.framwork.fragment.BaseFragment;
import com.base.rxjavalib.RxUtils;
import com.uber.autodispose.AutoDisposeConverter;

import java.util.Map;


/**
 * @date 2019-12-12
 * @Author luffy
 * @description
 */
public class BaseViewModle extends LifyCycleViewModel {

    private UILiveData ui;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public UILiveData getUi() {
        if (ui == null) {
            ui = new UILiveData();
        }
        return ui;
    }

    public final class UILiveData {

        private MutableLiveData<Void> finishEvent;
        private MutableLiveData<Void> onBackPressedEvent;
        private MutableLiveData<String> showLoadingEvent;
        private MutableLiveData<Void> hideLoadingEvent;
        private MutableLiveData<Map<String, Object>> startActivityEvent;
        private MutableLiveData<Map<String, Object>> startActivityForResultEvent;
        private MutableLiveData<BaseFragment> addFragment;
        private MutableLiveData<BaseFragment> replaceFragment;

        public MutableLiveData<Void> getFinishEvent() {
            return finishEvent = createLiveData(finishEvent);
        }

        public MutableLiveData<Void> getOnBackPressedEvent() {
            return onBackPressedEvent = createLiveData(onBackPressedEvent);
        }

        public MutableLiveData<Map<String, Object>> getStartActivityEvent() {
            return startActivityEvent = createLiveData(startActivityEvent);
        }

        public MutableLiveData<Map<String, Object>> getStartActivityForResultEvent() {
            return startActivityForResultEvent = createLiveData(startActivityForResultEvent);
        }

        public MutableLiveData<String> getshowLoadingEvent() {
            return showLoadingEvent = createLiveData(showLoadingEvent);
        }

        public MutableLiveData<Void> getHideLoadingEvent() {
            return hideLoadingEvent = createLiveData(hideLoadingEvent);
        }

        public MutableLiveData<BaseFragment> getAddFragment() {
            return addFragment = createLiveData(addFragment);
        }

        public MutableLiveData<BaseFragment> getReplaceFragment() {
            return replaceFragment = createLiveData(replaceFragment);
        }

        private MutableLiveData createLiveData(MutableLiveData liveData) {
            if (liveData == null) {
                liveData = new MutableLiveData();
            }
            return liveData;
        }
    }
}
