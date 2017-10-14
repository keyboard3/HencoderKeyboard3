package com.keyboard3.hencoderProduct.Like;

import android.animation.AnimatorSet;
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


public class LikeView extends View {
    Paint paint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private AnimatorSet animatorSet = new AnimatorSet();

    private int translationY;
    private int moveY = 40;//至少的文字的移动位置
    private int maxLen = 3;//最多支持多少点赞数 999
    private int textSize = 24;
    private int textPadding = 50;
    private Integer drawNum = 314;//当前赞数量
    private int animTime = 500;
    private int centerX;
    private int centerY;
    private boolean mLike = false;//点赞和取消赞操作
    private Bitmap unlikeBitmap;
    private Bitmap likedBitmap;
    private Bitmap shiningBitmap;

    public LikeView(Context context) {
        super(context);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        paint0.setTextSize(textSize);
        paint1.setTextSize(textSize);
        paint2.setTextSize(textSize);
        paint0.setColor(Color.parseColor("#c3c4c3"));
        paint1.setColor(Color.parseColor("#c3c4c3"));
        paint2.setColor(Color.parseColor("#c3c4c3"));


        unlikeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_unselected);
        likedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected);
        shiningBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected_shining);

        ObjectAnimator animator1 = ObjectAnimator.ofInt(this, "translationY", 0, moveY);
        animator1.setDuration(animTime);
        animatorSet.playSequentially(animator1);
    }

    public boolean isLiked() {
        return mLike;
    }

    public void setLike(boolean isLike) {
        if (mLike != isLike) {
            mLike = isLike;
            animatorSet.start();
        } else {
            mLike = isLike;
        }
    }

    public void unlike() {
        mLike = false;
        animatorSet.start();
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

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        String text = drawNum.toString();
        int leftX = centerX;


        //绘制点赞图片
        int likeTop = centerY - unlikeBitmap.getHeight() / 2;
        int likeLeft = leftX - textPadding;
        drawLike(canvas, likeTop, likeLeft);

        //绘制数字 相对居中
        Rect rect = new Rect();
        paint0.getTextBounds("0", 0, 1, rect);
        drawNum(canvas, text, leftX, centerY - (rect.top + rect.bottom) / 2);

        //绘制闪光
        drawShining(canvas, likeTop, likeLeft);
    }

    private void drawLike(Canvas canvas, int likeTop, int likeLeft) {
        if (mLike && getAnimPercent() > 0.9 && getAnimPercent() < 1) {//放大
            canvas.save();
            canvas.translate((float) (likeLeft - likedBitmap.getWidth() * 0.05), (float) (likeTop - likedBitmap.getHeight() * 0.05));
            canvas.scale(1.1f, 1.1f);
            canvas.drawBitmap(likedBitmap, 0, 0, paint0);
            canvas.restore();
        } else if (mLike && getAnimPercent() > 0.5) {//选中
            canvas.drawBitmap(mLike ? likedBitmap : unlikeBitmap, likeLeft, likeTop, paint0);
        } else {
            canvas.drawBitmap(unlikeBitmap, likeLeft, likeTop, paint0);
        }
    }

    private void drawShining(Canvas canvas, int likeTop, int likeLeft) {
        if (mLike) {
            float scale = getAnimPercent();
            int shiningTop = (int) (likeTop - scale * shiningBitmap.getHeight() / 2);
            int shiningLeft = (int) (likeLeft + (likedBitmap.getWidth() - scale * shiningBitmap.getWidth()) / 2);

            canvas.save();
            canvas.translate(shiningLeft, shiningTop);
            canvas.scale(scale, scale);
            canvas.drawBitmap(shiningBitmap, 0, 0, paint0);
            canvas.restore();
        }
    }

    public float getAnimPercent() {
        return 1.0F * translationY / moveY;
    }

    private void drawNum(Canvas canvas, String text, int leftX, int baseTxtY) {
        for (int i = 0; i < maxLen; i++) {
            if (maxLen - i <= text.length()) {
                int textIndex = text.length() - maxLen + i;//计算出实际的数组的开头
                leftX += i > 0 ? paint0.measureText(text.substring(textIndex, textIndex + 1)) : 0;
                if (textIndex == text.length() - 1) {
                    optDrawNum(canvas, leftX, baseTxtY, text.substring(textIndex, textIndex + 1), false);
                } else {
                    optDrawNum(canvas, leftX, baseTxtY, text.substring(textIndex, textIndex + 1), true);
                }
            } else {//给前面留位置 方面递增
                leftX += paint0.measureText(" ");
                canvas.drawText(" ", leftX, baseTxtY, paint0);
            }
        }
    }

    private void optDrawNum(Canvas canvas, int leftX, int baseTxtY, String num, boolean isNomarl) {
        int value1 = Integer.parseInt(num);
        if (!isNomarl) {//回溯操作之前的值
            value1 = (10 + value1 - getAddOrDecentNum()) % 10;
        }
        int value2 = (10 + value1 + getAddOrDecentNum()) % 10;
        int alpha = (int) ((1 - 1.0 * translationY / moveY) * 255);

        paint1.setAlpha(alpha);
        paint2.setAlpha(255 - alpha);

        if (isNomarl) {
            canvas.drawText(value1 + "", leftX, baseTxtY, paint0);
        } else if (mLike) {
            canvas.drawText(value1 + "", leftX, baseTxtY + translationY, paint1);
            canvas.drawText(value2 + "", leftX, baseTxtY - moveY + translationY, paint2);
        } else {
            canvas.drawText(value1 + "", leftX, baseTxtY - translationY, paint1);
            canvas.drawText(value2 + "", leftX, baseTxtY + moveY - translationY, paint2);
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