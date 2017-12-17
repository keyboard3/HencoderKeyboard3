package com.keyboard3;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by yany on 2017/10/17.
 * 用于包着显示具体数字刻度Layout
 */

public class RulerNumberLayout extends RelativeLayout implements RulerCallback {
    private TextView tv_scale, tv_kg;
    //字体大小
    private float mScaleTextSize = 80, mKgTextSize = 40;
    //字体颜色
    private @ColorInt
    int mScaleTextColor = getResources().getColor(R.color.colorForgiven);
    private @ColorInt
    int mKgTextColor = getResources().getColor(R.color.colorForgiven);
    @IdRes
    int mTargetRuler;
    //kg单位文字
    private String mUnitText = "kg";


    public RulerNumberLayout(Context context) {
        super(context);
        init(context);
    }

    public RulerNumberLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init(context);
    }

    public RulerNumberLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RulerNumberLayout, 0, 0);
        mScaleTextSize = typedArray.getDimension(R.styleable.RulerNumberLayout_scaleTextSize, mScaleTextSize);
        mKgTextSize = typedArray.getDimension(R.styleable.RulerNumberLayout_kgTextSize, mKgTextSize);
        mScaleTextColor = typedArray.getColor(R.styleable.RulerNumberLayout_scaleTextColor, mScaleTextColor);
        mKgTextColor = typedArray.getColor(R.styleable.RulerNumberLayout_kgTextColor, mKgTextColor);
        mKgTextColor = typedArray.getColor(R.styleable.RulerNumberLayout_kgTextColor, mKgTextColor);
        mTargetRuler = typedArray.getResourceId(R.styleable.RulerNumberLayout_targetRuler, 0);
        String text = typedArray.getString(R.styleable.RulerNumberLayout_kgUnitText);
        if (text != null) {
            mUnitText = text;
        }
        typedArray.recycle();

    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_kg_number, this);
        tv_scale = (TextView) findViewById(R.id.tv_scale);
        tv_kg = (TextView) findViewById(R.id.tv_kg);

        tv_scale.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScaleTextSize);
        tv_scale.setTextColor(mScaleTextColor);

        tv_kg.setTextSize(TypedValue.COMPLEX_UNIT_PX, mKgTextSize);
        tv_kg.setTextColor(mKgTextColor);
        tv_kg.setText(mUnitText);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTargetRuler != 0) {
            View ruler = getRootView().findViewById(mTargetRuler);
            if (ruler instanceof BooheeRuler) {
                ((BooheeRuler) ruler).addCallback(this);
            }
        }

    }

    public void bindRuler(BooheeRuler booheeRuler) {
        booheeRuler.addCallback(this);
    }

    @Override
    public void onScaleChanging(float scale) {
        tv_scale.setText(String.valueOf(scale / 10));
    }
}