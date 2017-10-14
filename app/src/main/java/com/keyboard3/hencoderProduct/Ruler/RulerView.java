package com.keyboard3.hencoderProduct.Ruler;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class RulerView extends ViewGroup {
    Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint0 = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int kgTextSize = 24;
    private int weightTextSize = 70;
    private int centerDivisionStrokeWidth = 8;

    private int textPadding = 50;
    private AnimatorSet animatorSet = new AnimatorSet();
    private int centerX;
    private int centerY;
    private int rulerTop;
    private int rulerBottom;
    private String currentValue = "00.0";
    private InnerRulerView rulerView;

    public RulerView(Context context) {
        super(context);
        init(context);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        paint0.setTextAlign(Paint.Align.CENTER);
        paint0.setColor(Color.parseColor("#dbdeda"));
        paint1.setTextSize(weightTextSize);
        paint1.setTextAlign(Paint.Align.CENTER);
        paint1.setStrokeWidth(centerDivisionStrokeWidth);
        paint1.setColor(Color.parseColor("#67b77b"));
        addView(new InnerRulerView(context));
    }

    public void moveTranslationX(int translationX) {
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(getChildAt(0), "translationX", -Math.abs(translationX));
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                String temp = String.format("%.1f", rulerView.currentValue);
                if (!temp.equals(currentValue)) {
                    currentValue = temp;
                    RulerView.this.invalidate();
                }
            }
        });
        valueAnimator.start();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animatorSet.end();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        rulerTop = centerY - InnerRulerView.rulerHeight / 2;
        rulerBottom = centerY + InnerRulerView.rulerHeight / 2;

        rulerView = (InnerRulerView) getChildAt(0);
        int allWidth = (100 / (getWidth() / InnerRulerView.bigUnit)) * getWidth();//100个刻度单位的长度
        rulerView.layout(l, rulerTop, allWidth, rulerBottom);
        rulerView.setStartValue(1.0f * centerX / InnerRulerView.bigUnit);
        currentValue = String.format("%.1f", rulerView.currentValue);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制尺子背景
        paint0.setColor(Color.parseColor("#f7f9f6"));
        canvas.drawRect(0, rulerTop, getWidth(), rulerBottom, paint0);
        paint0.setColor(Color.parseColor("#dbdeda"));
        paint0.setStrokeWidth(1);
        canvas.drawLine(0, rulerTop, getWidth(), rulerTop, paint0);
        canvas.drawLine(0, rulerBottom, getWidth(), rulerBottom, paint0);
        //绘制数值
        paint1.setTextSize(weightTextSize);
        canvas.drawText(currentValue, centerX, rulerTop - textPadding, paint1);
        //绘制kg
        Rect valueRect = new Rect();
        paint1.getTextBounds("00.0", 0, currentValue.length()-1, valueRect);
        Rect kgRect = new Rect();
        paint1.setTextSize(kgTextSize);
        paint1.getTextBounds("kg", 0, 1, kgRect);
        canvas.drawText("kg", centerX + valueRect.width(), rulerTop - textPadding - valueRect.height() / 2 + (kgRect.top + kgRect.bottom) / 2, paint1);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //中间刻度
        paint1.setStrokeCap(Paint.Cap.SQUARE);
        canvas.drawLine(centerX, rulerTop + 4, centerX, rulerTop + InnerRulerView.centerDivisionHeight, paint1);
        paint1.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPoint(centerX, rulerTop + InnerRulerView.centerDivisionHeight + 4, paint1);
    }

    public static class InnerRulerView extends View {
        Paint paint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
        public float currentValue = 0;
        protected static int bigUnit = 200;
        protected static int rulerHeight = 200;
        private int smallUnit = bigUnit / 10;
        private int textPaddingTop = 4 * rulerHeight / 5;
        protected static int centerDivisionHeight = rulerHeight / 2;
        private int bigDivisionHeight = centerDivisionHeight - 10;
        private int smallDivisionHeight = bigDivisionHeight / 2;
        private int divisionTextSize = 36;
        private int bigCount = 100;
        private int bigDivisionStrokeWidth = 5;
        private int smallDivisionStrokeWidth = 3;
        private float startValue = 0;

        public InnerRulerView(Context context) {
            super(context);
            init();
        }

        public InnerRulerView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public InnerRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            paint0.setTextSize(divisionTextSize);
            paint0.setTextAlign(Paint.Align.CENTER);
            paint0.setColor(Color.parseColor("#dbdeda"));
        }

        @Override
        public void setTranslationX(float translationX) {
            super.setTranslationX(translationX);
            currentValue = -(1.0f * translationX / bigUnit) + startValue;
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //--绘制大刻度
            int bLeftX = 0, sLeftX = 0;
            for (int i = 0; i <= bigCount; i++) {
                //大刻度
                paint0.setColor(Color.parseColor("#dbdeda"));
                paint0.setStrokeWidth(bigDivisionStrokeWidth);
                canvas.drawLine(bLeftX, 0, bLeftX, bigDivisionHeight, paint0);

                //小刻度
                sLeftX = bLeftX;
                paint0.setStrokeWidth(smallDivisionStrokeWidth);
                for (int j = 0; j < 9; j++) {
                    sLeftX += smallUnit;
                    canvas.drawLine(sLeftX, 0, sLeftX, smallDivisionHeight, paint0);
                }

                paint0.setColor(Color.parseColor("#000000"));
                canvas.drawText(i + "", bLeftX, textPaddingTop, paint0);
                bLeftX += bigUnit;
            }
        }

        public void setStartValue(float startValue) {
            this.startValue = startValue;
            this.currentValue = startValue;
        }
    }

}