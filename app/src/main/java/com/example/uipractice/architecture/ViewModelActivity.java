package com.example.uipractice.architecture;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.base.custom.BaseActivity;
import com.base.framwork.p.BaseViewModle;
import com.example.uipractice.R;


public class ViewModelActivity extends BaseActivity<BaseViewModle> {



    @Override
    public int generateIdLayout() {
        return R.layout.activity_view_model;
    }

    @Override
    public void initData() {
        transformationsTest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ViewModelActivity","onDestroy()");
    }

    private void transformationsTest() {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        liveData.observe(this,str-> {
            Log.e("MutableLiveData",str);
        });

        // map 为转换数据，类似于rxJava的map
        LiveData<Integer> map = Transformations.map(liveData, t -> Integer.valueOf(t));
        map.observe(this,t-> Log.e("Transformations",t+""));

        liveData.setValue("1");

        // 过滤发射相同的数据
        LiveData<String> stringLiveData = Transformations.distinctUntilChanged(liveData);
        stringLiveData.observe(this,t-> {
            Log.e("Transformations",t+"");
        });
        liveData.setValue("1");
        liveData.setValue("2");

        // 相当于rxJava的flatmap
        LiveData<Integer> integerLiveData = Transformations.switchMap(liveData, new Function<String, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(String input) {
                return map;
            }
        });
        integerLiveData.observe(this,t-> {
            Log.e("switchMap",t+"");
        });

//        LiveDataReactiveStreams.fromPublisher()
//        LiveDataReactiveStreams.toPublisher()
    }

    @NonNull
    @Override
    public Class<BaseViewModle> getViewModelClass() {
        return BaseViewModle.class;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initEvent() {

    }
}
