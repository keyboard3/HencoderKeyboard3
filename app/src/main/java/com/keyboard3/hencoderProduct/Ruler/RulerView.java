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

import com.keyboard3.hencoderProduct.Utils;


public class RulerView extends ViewGroup {
    Paint mPaint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private AnimatorSet animatorSet = new AnimatorSet();
    private InnerRulerView mRulerView;
    private String mCurrentValue = "00.0";
    private int mBackgroundColor;
    private int mDefaultColor;
    private int mValueColor;
    private int mKgTextSize;
    private int mWeightTextSize;
    private int mTextPadding;
    private int mCenterDivisionStrokeWidth;
    private int mCenterX;
    private int mCenterY;
    private int mRulerTop;
    private int mRulerBottom;

    public RulerView(Context context) {
        super(context);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setWillNotDraw(false);
        mKgTextSize = (int) Utils.dpToPixel(12);
        mWeightTextSize = (int) Utils.dpToPixel(35);
        mTextPadding = (int) Utils.dpToPixel(25);
        mCenterDivisionStrokeWidth = (int) Utils.dpToPixel(4);
        mBackgroundColor = Color.parseColor("#f7f9f6");
        mDefaultColor = Color.parseColor("#dbdeda");
        mValueColor = Color.parseColor("#67b77b");

        mPaint0.setTextAlign(Paint.Align.CENTER);
        mPaint0.setColor(mDefaultColor);
        mPaint1.setTextSize(mWeightTextSize);
        mPaint1.setTextAlign(Paint.Align.CENTER);
        mPaint1.setStrokeWidth(mCenterDivisionStrokeWidth);
        mPaint1.setColor(mValueColor);

        addView(new InnerRulerView(getContext()));
    }

    public void moveTranslationX(int translationX) {
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(getChildAt(0), "translationX", -Math.abs(translationX));
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                String temp = String.format("%.1f", mRulerView.mCurrentValue);
                if (!temp.equals(mCurrentValue)) {
                    mCurrentValue = temp;
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
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        mRulerTop = mCenterY - InnerRulerView.RULER_HEIGHT / 2;
        mRulerBottom = mCenterY + InnerRulerView.RULER_HEIGHT / 2;

        mRulerView = (InnerRulerView) getChildAt(0);
        //让尺子实现绘制100个单位
        int allWidth = (100 / (getWidth() / InnerRulerView.BIG_UNIT)) * getWidth();
        mRulerView.layout(l, mRulerTop, allWidth, mRulerBottom);
        mRulerView.setStartValue(1.0f * mCenterX / InnerRulerView.BIG_UNIT);
        mCurrentValue = String.format("%.1f", mRulerView.mCurrentValue);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制尺子背景
        mPaint0.setColor(mBackgroundColor);
        canvas.drawRect(0, mRulerTop, getWidth(), mRulerBottom, mPaint0);
        mPaint0.setColor(mDefaultColor);
        mPaint0.setStrokeWidth(1);
        canvas.drawLine(0, mRulerTop, getWidth(), mRulerTop, mPaint0);
        canvas.drawLine(0, mRulerBottom, getWidth(), mRulerBottom, mPaint0);
        //绘制数值
        mPaint1.setTextSize(mWeightTextSize);
        canvas.drawText(mCurrentValue, mCenterX, mRulerTop - mTextPadding, mPaint1);
        //绘制kg
        Rect valueRect = new Rect();
        mPaint1.getTextBounds("00.0", 0, mCurrentValue.length() - 1, valueRect);
        Rect kgRect = new Rect();
        mPaint1.setTextSize(mKgTextSize);
        mPaint1.getTextBounds("kg", 0, 1, kgRect);
        canvas.drawText("kg", mCenterX + valueRect.width(), mRulerTop - mTextPadding - valueRect.height() / 2 + (kgRect.top + kgRect.bottom) / 2, mPaint1);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //中间刻度
        mPaint1.setStrokeCap(Paint.Cap.SQUARE);
        canvas.drawLine(mCenterX, mRulerTop + 4, mCenterX, mRulerTop + InnerRulerView.CENTER_DIVISION_HEIGHT, mPaint1);
        mPaint1.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPoint(mCenterX, mRulerTop + InnerRulerView.CENTER_DIVISION_HEIGHT + 4, mPaint1);
    }

    public static class InnerRulerView extends View {
        protected static int BIG_UNIT = 200;
        protected static int RULER_HEIGHT = 200;
        protected static int CENTER_DIVISION_HEIGHT = RULER_HEIGHT / 2;

        private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float mStartValue = 0;
        private float mCurrentValue = 0;
        private int mDefaultColor = Color.parseColor("#dbdeda");
        private int mDivisionTextSize = 36;
        private int mTextPaddingTop = 4 * RULER_HEIGHT / 5;
        private int mBigCount = 100;
        private int mBigDivisionStrokeWidth = 5;
        private int mBigDivisionHeight = CENTER_DIVISION_HEIGHT - 10;
        private int mSmallUnit = BIG_UNIT / 10;
        private int mSmallDivisionStrokeWidth = 3;
        private int mSmallDivisionHeight = mBigDivisionHeight / 2;

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
            mPaint.setTextSize(mDivisionTextSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setColor(mDefaultColor);
        }

        @Override
        public void setTranslationX(float translationX) {
            super.setTranslationX(translationX);
            mCurrentValue = -(1.0f * translationX / BIG_UNIT) + mStartValue;
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int bLeftX = 0, sLeftX = 0;
            for (int i = 0; i <= mBigCount; i++) {
                //大刻度
                mPaint.setColor(mDefaultColor);
                mPaint.setStrokeWidth(mBigDivisionStrokeWidth);
                canvas.drawLine(bLeftX, 0, bLeftX, mBigDivisionHeight, mPaint);

                //小刻度
                sLeftX = bLeftX;
                mPaint.setStrokeWidth(mSmallDivisionStrokeWidth);
                for (int j = 0; j < 9; j++) {
                    sLeftX += mSmallUnit;
                    canvas.drawLine(sLeftX, 0, sLeftX, mSmallDivisionHeight, mPaint);
                }

                mPaint.setColor(Color.BLACK);
                canvas.drawText(i + "", bLeftX, mTextPaddingTop, mPaint);
                bLeftX += BIG_UNIT;
            }
        }

        public void setStartValue(float mStartValue) {
            this.mStartValue = mStartValue;
            this.mCurrentValue = mStartValue;
        }
    }
}