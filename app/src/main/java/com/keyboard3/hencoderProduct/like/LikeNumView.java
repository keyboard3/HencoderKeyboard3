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
    private int mTextSize;
    private int centerY;
    private float rightPadding;

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
        mTextSize = (int) Utils.dpToPixel(12);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(Color.parseColor("#c3c4c3"));
    }

    protected void setNum(int num) {
        mCurNum = mNewNum = num;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //保证长度足够
        String curNum = (mCurNum + 1) * 10 + "";
        Rect rect = new Rect();
        mPaint.getTextBounds(curNum, 0, curNum.length(), rect);
        float width = rect.width() + rightPadding;
        int height = mTextSize * 3;
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        width = resolveSize((int) width, widthSpecSize);
        setMeasuredDimension((int) width, height);
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
        centerY = getHeight() / 2;
        int leftX = 0;
        Rect rect = new Rect();
        mPaint.getTextBounds("0", 0, 1, rect);
        drawAnimNum(canvas, leftX, centerY - (rect.top + rect.bottom) / 2, mCurNum, mNewNum);
    }

    private void drawAnimNum(Canvas canvas, int leftX, int baseTxtY, int curNum, int newNum) {
        String curNumStr = (curNum + "").toString();
        String newNumStr = (newNum + "").toString();
        int len = Math.max(curNumStr.length(), newNumStr.length());
        float charLen = mPaint.measureText("0");
        int sumLeft = leftX;
        String curCharTxt, newCharTxt;
        for (int i = 0; i < len; i++) {
            if (i > curNumStr.length() - 1) {
                curCharTxt = "";
            } else {
                curCharTxt = curNumStr.substring(i, i + 1);
            }
            if (i > newNumStr.length() - 1) {
                newCharTxt = "";
            } else {
                newCharTxt = newNumStr.substring(i, i + 1);
            }
            optDrawNum(canvas, sumLeft, baseTxtY, curCharTxt, newCharTxt, newNum > curNum);
            sumLeft += charLen;
        }
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

    @SuppressWarnings("unused")
    public void setTranslationY(int translationY) {
        this.translationY = translationY;
        invalidate();
    }

    public void setRightPadding(float rightPadding) {
        this.rightPadding = rightPadding;
    }
}