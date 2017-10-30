package com.keyboard3.hencoderProduct.like;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.keyboard3.hencoderProduct.Utils;


/**
 * @author keyboard3
 */
public class LikeNumView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mCurNum = 0;
    private int mNewNum = 0;
    private int translationY;
    private boolean liked = false;
    private int mMoveY;
    private int mMaxLen = 0;
    private int mTextSize;
    private int centerX;
    private int centerY;

    public LikeNumView(Context context) {
        super(context);
    }

    public LikeNumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeNumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mMaxLen = 0;
        mTextSize = (int) Utils.dpToPixel(12);

        mPaint.setTextSize(mTextSize);
        mPaint.setColor(Color.parseColor("#c3c4c3"));
    }

    protected void setNum(int num) {
        mCurNum = mNewNum = num;
        mMaxLen = 0;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        String curNum = mCurNum + 1 + "";
        Rect rect = new Rect();
        mPaint.getTextBounds(curNum + "0", 0, curNum.length() + 1, rect);
        int width = rect.width();
        int height = mTextSize * 3;
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int specSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        switch (heightSpecMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                if (specSize > mTextSize && specSize < height) {
                    height = specSize;
                }
                break;
            default:
        }
        width = Math.max(widthSpecSize, width);
        setMeasuredDimension(width, height);
        mMoveY = height / 2;
    }

    protected void changeLike(boolean isLike) {
        if (isLike) {
            if (mCurNum != 0) {
                mNewNum = mCurNum - 1;
            }
        } else {
            mNewNum = mCurNum + 1;
        }
        liked = !isLike;
    }

    protected void init() {
        mCurNum = mNewNum;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        int leftX = 0;
        //绘制数字 相对居中
        Rect rect = new Rect();
        mPaint.getTextBounds("0", 0, 1, rect);
        drawNum(canvas, leftX, centerY - (rect.top + rect.bottom) / 2, mCurNum, mNewNum);
    }


    private void drawNum(Canvas canvas, int leftX, int baseTxtY, int curNum, int newNum) {
        String curNumStr = (curNum + "").toString();
        String newNumStr = (newNum + "").toString();
        mMaxLen = Math.max(Math.max(curNumStr.length(), newNumStr.length()), mMaxLen);

        if (newNum < curNum) {
            boolean lengthChange = false;
            if (newNumStr.length() < curNumStr.length()) {
                lengthChange = true;
            }
            int sumLeft = leftX;
            String tempNumStr = curNumStr;
            for (int i = 0; i < tempNumStr.length(); i++) {
                float len = i == 0 ? 0 : mPaint.measureText(tempNumStr.substring(i - 1, i));
                sumLeft += len;
                String curBitStr = curNumStr.substring(i, i + 1);
                String newBitStr = "";
                if (lengthChange && i == 0) {
                    newBitStr = " ";
                } else if (lengthChange) {
                    newBitStr = newNumStr.substring(i - 1, i);
                } else {
                    newBitStr = newNumStr.substring(i, i + 1);
                }
                optDrawNum(canvas, sumLeft, baseTxtY, curBitStr, newBitStr, false);
            }
        } else if (newNum > curNum) {
            //长度发生变化
            boolean lengthChange = false;
            if (newNumStr.length() > curNumStr.length()) {
                lengthChange = true;
            }
            int sumLeft = leftX;
            String tempNumStr = newNumStr;
            for (int i = 0; i < tempNumStr.length(); i++) {
                float len = i == 0 ? 0 : mPaint.measureText(tempNumStr.substring(i - 1, i));
                sumLeft += len;
                String curBitStr = "";
                if (lengthChange && i == 0) {
                    curBitStr = " ";
                } else if (lengthChange) {
                    curBitStr = curNumStr.substring(i - 1, i);
                } else {
                    curBitStr = curNumStr.substring(i, i + 1);
                }
                String newBitStr = newNumStr.substring(i, i + 1);
                optDrawNum(canvas, sumLeft, baseTxtY, curBitStr, newBitStr, true);
            }
        } else if (newNum == curNum) {
            float emptyLen = mPaint.measureText("0");
            //处理100-99 位移的情况
            leftX += (mMaxLen - curNumStr.length()) * emptyLen;

            int sumLeft = leftX;
            String tempNumStr = curNumStr;
            for (int i = 0; i < tempNumStr.length(); i++) {
                float len = i == 0 ? 0 : mPaint.measureText(tempNumStr.substring(i - 1, i));
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
            mPaint.setAlpha(255);
            canvas.drawText(curNum, leftX, baseTxtY, mPaint);
            return;
        }
        int alpha = (int) ((1 - 1.0 * translationY / mMoveY) * 255);
        int curBaseY = baseTxtY;
        int newBaseY, transY;
        if (upOrDown) {
            transY = -translationY;
            newBaseY = baseTxtY + mMoveY;
        } else {//down -1
            transY = translationY;
            newBaseY = baseTxtY - mMoveY;
        }
        mPaint.setAlpha(alpha);
        canvas.drawText(curNum, leftX, curBaseY + transY, mPaint);
        mPaint.setAlpha(255 - alpha);
        canvas.drawText(newNum, leftX, newBaseY + transY, mPaint);
        mPaint.setAlpha(255);
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
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