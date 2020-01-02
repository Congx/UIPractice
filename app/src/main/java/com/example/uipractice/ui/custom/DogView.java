package com.example.uipractice.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.uipractice.R;

/**
 * @date 2019-12-20
 * @Author luffy
 * @description
 */
public class DogView extends View {

    private int color;

    public DogView(Context context) {
        this(context,null);
    }

    public DogView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DogView, 0, 0);

        color = typedArray.getColor(R.styleable.DogView_dogColor, 0);
        // 复用系统自带的attr
//        color = typedArray.getColor(R.styleable.DogView_android_textColor, 0);

        int dogStyle = typedArray.getResourceId(R.styleable.DogView_dogStyle, 0);


        // 直接从 attr中获取样式
//        Resources.Theme theme = context.getTheme();
//        TypedValue typedValue = new TypedValue();
//        boolean attribute = theme.resolveAttribute(R.attr.dogColor, typedValue, true);
//
//        if(attribute) {
//            this.color = typedValue.data;
//        }

        // 直接获取attr中的style
//        boolean attribute2 = theme.resolveAttribute(R.attr.xiaoHuang, typedValue, true);
//
//        if(attribute2) {
//            int resourceId = typedValue.resourceId;
//            int[] attrss = new int[]{R.attr.dogColor2};
//            TypedArray typedArray1 = context.obtainStyledAttributes(resourceId, attrss);
//            this.color = typedArray1.getColor(0, color);
//        }

        // style 用自定义的形式
        int[] attrss = new int[]{R.attr.dogColor2};
        TypedArray typedArray1 = context.obtainStyledAttributes(dogStyle, attrss);
        this.color = typedArray1.getColor(0, this.color);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

        canvas.drawCircle(50,50,50,paint);
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
