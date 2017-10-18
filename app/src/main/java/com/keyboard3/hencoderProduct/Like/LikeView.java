package com.keyboard3.hencoderProduct.Like;

import android.animation.Animator;
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
    private int mCurNum = 9;//当前赞数量
    private int mNewNum = mCurNum;//当前赞数量
    private int translationY;
    private boolean liked = false;
    private int mMoveY;//至少的文字的移动位置
    private int mMaxLen = 0;
    private int mTextSize;
    private int mTextPadding;
    private int mAnimTime = 500;
    private int centerX;
    private int centerY;
    private Bitmap mUnlikeBitmap;
    private Bitmap mLikedBitmap;
    private Bitmap mShiningBitmap;


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
        mMaxLen = 0;
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
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCurNum = mNewNum;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.setDuration(mAnimTime);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mAnimator.isRunning()) {
                    if (isLiked()) {
                        if (mCurNum != 0)
                            mNewNum = mCurNum - 1;
                    } else {
                        mNewNum = mCurNum + 1;
                    }
                    liked = !isLiked();
                    mAnimator.start();
                }
            }
        });
    }

    public void clear() {
        mMaxLen = 0;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLike(boolean isLike) {
        if (liked) {
            setTranslationY(mMoveY);
        } else {
            setTranslationY(0);
        }
        liked = isLike;
        invalidate();
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
        int leftX = centerX;

        //绘制点赞图片
        int likeTop = centerY - mUnlikeBitmap.getHeight() / 2;
        int likeLeft = leftX - mTextPadding;
        drawLike(canvas, likeTop, likeLeft);

        //绘制数字 相对居中
        Rect rect = new Rect();
        mPaint0.getTextBounds("0", 0, 1, rect);
        drawNum(canvas, leftX, centerY - (rect.top + rect.bottom) / 2, mCurNum, mNewNum);
        //绘制闪光
        drawShining(canvas, likeTop, likeLeft);
    }

    private void drawLike(Canvas canvas, int likeTop, int likeLeft) {
        boolean mLike = liked;
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
        if (liked) {
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

    private void drawNum(Canvas canvas, int leftX, int baseTxtY, int curNum, int newNum) {
        String curNumStr = (curNum + "").toString();
        String newNumStr = (newNum + "").toString();
        mMaxLen = Math.max(Math.max(curNumStr.length(), newNumStr.length()), mMaxLen);

        if (newNum < curNum) {//-1 下降滑动
            boolean lengthChange = false;
            if (newNumStr.length() < curNumStr.length()) {
                lengthChange = true;
            }
            int sumLeft = leftX;
            String tempNumStr = curNumStr;
            for (int i = 0; i < tempNumStr.length(); i++) {
                float len = i == 0 ? 0 : mPaint0.measureText(tempNumStr.substring(i - 1, i));
                sumLeft += len;
                String curBitStr = curNumStr.substring(i, i + 1);
                String newBitStr = "";
                if (lengthChange && i == 0) newBitStr = " ";
                else if (lengthChange) newBitStr = newNumStr.substring(i - 1, i);
                else newBitStr = newNumStr.substring(i, i + 1);
                optDrawNum(canvas, sumLeft, baseTxtY, curBitStr, newBitStr, false);
            }
        } else if (newNum > curNum) {//+1 上升滑动
            //长度发生变化
            boolean lengthChange = false;
            if (newNumStr.length() > curNumStr.length()) {
                lengthChange = true;
            }
            int sumLeft = leftX;
            String tempNumStr = newNumStr;
            for (int i = 0; i < tempNumStr.length(); i++) {
                float len = i == 0 ? 0 : mPaint0.measureText(tempNumStr.substring(i - 1, i));
                sumLeft += len;
                String curBitStr = "";
                if (lengthChange && i == 0) curBitStr = " ";
                else if (lengthChange) curBitStr = curNumStr.substring(i - 1, i);
                else curBitStr = curNumStr.substring(i, i + 1);
                String newBitStr = newNumStr.substring(i, i + 1);
                optDrawNum(canvas, sumLeft, baseTxtY, curBitStr, newBitStr, true);
            }
        } else if (newNum == curNum) {//纯绘制数字
            float emptyLen = mPaint0.measureText("0");
            leftX += (mMaxLen - curNumStr.length()) * emptyLen;//处理100-99 位移的情况

            int sumLeft = leftX;
            String tempNumStr = curNumStr;
            for (int i = 0; i < tempNumStr.length(); i++) {
                float len = i == 0 ? 0 : mPaint0.measureText(tempNumStr.substring(i - 1, i));
                sumLeft += len;
                String curBitStr = tempNumStr.substring(i, i + 1);
                String newBitStr = tempNumStr.substring(i, i + 1);
                optDrawNum(canvas, sumLeft, baseTxtY, curBitStr, newBitStr);
            }
        }
    }

    private void optDrawNum(Canvas canvas, int leftX, int baseTxtY, String curNum, String newNum) {
        optDrawNum(canvas, leftX, baseTxtY, curNum, newNum, false);
    }

    private void optDrawNum(Canvas canvas, int leftX, int baseTxtY, String curNum, String newNum, boolean upOrDown) {
        if (curNum.equals(newNum)) {
            canvas.drawText(curNum, leftX, baseTxtY, mPaint0);
            return;
        }
        int alpha = (int) ((1 - 1.0 * translationY / mMoveY) * 255);
        mPaint1.setAlpha(alpha);//当前 消失
        mPaint2.setAlpha(255 - alpha);//当前 显示
        int curBaseY = baseTxtY;
        int newBaseY, transY;
        if (upOrDown) {//up +1
            transY = -translationY;//上移
            newBaseY = baseTxtY + mMoveY;
        } else {//down -1
            transY = translationY;//下移
            newBaseY = baseTxtY - mMoveY;
        }
        canvas.drawText(curNum, leftX, curBaseY + transY, mPaint1);
        canvas.drawText(newNum, leftX, newBaseY + transY, mPaint2);
    }

    public void setDrawNum(Integer mDrawNum) {
        this.mCurNum = mDrawNum;
        this.mNewNum = mDrawNum;
        invalidate();
    }

    @SuppressWarnings("unused")
    public void setTranslationY(int translationY) {
        this.translationY = translationY;
        invalidate();
    }
}