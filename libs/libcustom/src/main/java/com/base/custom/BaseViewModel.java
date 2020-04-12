package com.base.custom;

import androidx.lifecycle.MutableLiveData;

import com.base.framwork.p.LifyCycleViewModel;


/**
 * @date 2019-12-12
 * @Author luffy
 * @description
 */
public class BaseViewModel extends LifyCycleViewModel {

    private UILiveData ui;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public UI getUi() {
        if (ui == null) {
            ui = new UILiveData();
        }
        return ui;
    }

    /**
     * 简单的一些ViewMoedel 交互
     */
    public static class UILiveData implements UI{

        public MutableLiveData<Integer> uiLiveData = new MutableLiveData<>();


        @Override
        public MutableLiveData<Integer> getLiveData() {
            return uiLiveData;
        }

        @Override
        public void showLoadingDialog() {
            uiLiveData.setValue(TYPE.SHOWLOADINGDIALOG);
        }

        @Override
        public void hideLoadingDialog() {
            uiLiveData.setValue(TYPE.HIDELOADINGDIALOG);
        }

        @Override
        public void showLoading() {
            uiLiveData.setValue(TYPE.SHOWLOADING);
        }

        @Override
        public void showContent() {
            uiLiveData.setValue(TYPE.SHOWCONTENT);
        }

        @Override
        public void showError() {
            uiLiveData.setValue(TYPE.SHOWERROR);
        }

        @Override
        public void showNoNetwork() {
            uiLiveData.setValue(TYPE.SHOWNONETWORK);
        }

        @Override
        public void finish() {
            uiLiveData.setValue(TYPE.FINISH);
        }

        interface TYPE {
            int SHOWLOADINGDIALOG   = 0;
            int HIDELOADINGDIALOG   = 1;
            int SHOWLOADING         = 2;
            int SHOWCONTENT         = 3;
            int SHOWERROR           = 4;
            int SHOWNONETWORK       = 5;
            int FINISH              = 6;
        }
    }

    public interface UI {
        MutableLiveData<Integer> getLiveData();
        void showLoadingDialog();
        void hideLoadingDialog();
        void showLoading();
        void showContent();
        void showError();
        void showNoNetwork();
        void finish();
    }
}
