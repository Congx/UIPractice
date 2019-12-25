package com.example.uipractice.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * @date 2019-12-20
 * @Author luffy
 * @description
 */
public class DogView extends View {
    public DogView(Context context) {
        this(context,null);
    }

    public DogView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int with = 0;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            with = 200;
        }else {
            with = widthSize;
        }

        setMeasuredDimension(with,heightSize);
    }
}
