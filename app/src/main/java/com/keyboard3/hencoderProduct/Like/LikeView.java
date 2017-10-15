package com.keyboard3.hencoderProduct.Like;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.keyboard3.hencoderProduct.R;
import com.keyboard3.hencoderProduct.Utils;


public class LikeView extends View {
    private Paint mPaint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ObjectAnimator mAnimator;
    private Integer mDrawNum = 314;//当前赞数量
    private int translationY;
    private int mMoveY;//至少的文字的移动位置
    private int mMaxLen = 3;//最多支持多少点赞数 999
    private int mTextSize;
    private int mTextPadding;
    private int mAnimTime = 500;
    private int centerX;
    private int centerY;
    private boolean mLike = false;//点赞和取消赞操作
    private Bitmap mUnlikeBitmap;
    private Bitmap mLikedBitmap;
    private Bitmap mShiningBitmap;


    public LikeView(Context context) {
        super(context);
        init();
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMoveY = (int) Utils.dpToPixel(20);
        mTextSize = (int) Utils.dpToPixel(12);
        mTextPadding = (int) Utils.dpToPixel(25);

        mPaint0.setTextSize(mTextSize);
        mPaint1.setTextSize(mTextSize);
        mPaint2.setTextSize(mTextSize);
        mPaint0.setColor(Color.parseColor("#c3c4c3"));
        mPaint1.setColor(Color.parseColor("#c3c4c3"));
        mPaint2.setColor(Color.parseColor("#c3c4c3"));

        mUnlikeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_unselected);
        mLikedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected);
        mShiningBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected_shining);

        mAnimator = ObjectAnimator.ofInt(this, "translationY", 0, mMoveY);
        mAnimator.setDuration(mAnimTime);
    }

    public boolean isLiked() {
        return mLike;
    }

    public void setLike(boolean isLike) {
        if (mLike != isLike) {
            mLike = isLike;
            mAnimator.start();
        } else {
            mLike = isLike;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimator.end();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        String text = mDrawNum.toString();
        int leftX = centerX;

        //绘制点赞图片
        int likeTop = centerY - mUnlikeBitmap.getHeight() / 2;
        int likeLeft = leftX - mTextPadding;
        drawLike(canvas, likeTop, likeLeft);

        //绘制数字 相对居中
        Rect rect = new Rect();
        mPaint0.getTextBounds("0", 0, 1, rect);
        drawNum(canvas, text, leftX, centerY - (rect.top + rect.bottom) / 2);

        //绘制闪光
        drawShining(canvas, likeTop, likeLeft);
    }

    private void drawLike(Canvas canvas, int likeTop, int likeLeft) {
        if (mLike && getAnimPercent() > 0.9 && getAnimPercent() < 1) {//放大
            canvas.save();
            canvas.translate((float) (likeLeft - mLikedBitmap.getWidth() * 0.05), (float) (likeTop - mLikedBitmap.getHeight() * 0.05));
            canvas.scale(1.1f, 1.1f);
            canvas.drawBitmap(mLikedBitmap, 0, 0, mPaint0);
            canvas.restore();
        } else if (mLike && getAnimPercent() > 0.5) {//选中
            canvas.drawBitmap(mLike ? mLikedBitmap : mUnlikeBitmap, likeLeft, likeTop, mPaint0);
        } else {
            canvas.drawBitmap(mUnlikeBitmap, likeLeft, likeTop, mPaint0);
        }
    }

    private void drawShining(Canvas canvas, int likeTop, int likeLeft) {
        if (mLike) {
            float scale = getAnimPercent();
            int shiningTop = (int) (likeTop - scale * mShiningBitmap.getHeight() / 2);
            int shiningLeft = (int) (likeLeft + (mLikedBitmap.getWidth() - scale * mShiningBitmap.getWidth()) / 2);

            canvas.save();
            canvas.translate(shiningLeft, shiningTop);
            canvas.scale(scale, scale);
            canvas.drawBitmap(mShiningBitmap, 0, 0, mPaint0);
            canvas.restore();
        }
    }

    public float getAnimPercent() {
        return 1.0F * translationY / mMoveY;
    }

    private void drawNum(Canvas canvas, String text, int leftX, int baseTxtY) {
        for (int i = 0; i < mMaxLen; i++) {
            if (mMaxLen - i <= text.length()) {
                int textIndex = text.length() - mMaxLen + i;//计算出实际的数组的开头
                leftX += i > 0 ? mPaint0.measureText(text.substring(textIndex, textIndex + 1)) : 0;
                if (textIndex == text.length() - 1) {
                    optDrawNum(canvas, leftX, baseTxtY, text.substring(textIndex, textIndex + 1), false);
                } else {
                    optDrawNum(canvas, leftX, baseTxtY, text.substring(textIndex, textIndex + 1), true);
                }
            } else {//给前面留位置 方面递增
                leftX += mPaint0.measureText(" ");
                canvas.drawText(" ", leftX, baseTxtY, mPaint0);
            }
        }
    }

    private void optDrawNum(Canvas canvas, int leftX, int baseTxtY, String num, boolean isNormal) {
        int value1 = Integer.parseInt(num);
        if (!isNormal) {//回溯操作之前的值
            value1 = (10 + value1 - getAddOrDecentNum()) % 10;
        }
        int value2 = (10 + value1 + getAddOrDecentNum()) % 10;
        int alpha = (int) ((1 - 1.0 * translationY / mMoveY) * 255);

        mPaint1.setAlpha(alpha);
        mPaint2.setAlpha(255 - alpha);

        if (isNormal) {
            canvas.drawText(value1 + "", leftX, baseTxtY, mPaint0);
        } else if (mLike) {
            canvas.drawText(value1 + "", leftX, baseTxtY + translationY, mPaint1);
            canvas.drawText(value2 + "", leftX, baseTxtY - mMoveY + translationY, mPaint2);
        } else {
            canvas.drawText(value1 + "", leftX, baseTxtY - translationY, mPaint1);
            canvas.drawText(value2 + "", leftX, baseTxtY + mMoveY - translationY, mPaint2);
        }
    }

    private int getAddOrDecentNum() {
        return mLike ? 1 : -1;
    }

    @SuppressWarnings("unused")
    public void setTranslationY(int translationY) {
        this.translationY = translationY;
        invalidate();
    }
}