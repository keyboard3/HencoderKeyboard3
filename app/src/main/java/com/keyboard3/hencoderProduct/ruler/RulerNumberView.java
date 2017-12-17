package com.keyboard3.hencoderProduct.ruler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keyboard3.hencoderProduct.R;
import com.keyboard3.hencoderProduct.Utils;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.RulerCallback;


/**
 * @author keyboard3
 */
@SuppressLint("AppCompatCustomView")
public class RulerNumberView extends TextView implements RulerCallback {
    Paint mPaint;
    private int mSmallSize;
    private String smallText;
    private int mTextPadding;
    private Rect smallRect;
    private Rect normalRect;
    private @IdRes
    int mTargetId;

    public RulerNumberView(Context context) {
        super(context);
    }

    public RulerNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RulerNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        smallRect = new Rect();
        mPaint.getTextBounds(smallText, 0, smallText.length() - 1, smallRect);
        normalRect = new Rect();
        mPaint.getTextBounds(getText().toString(), 0, getText().length() - 1, normalRect);
        int tempWidth = normalRect.width() + mTextPadding + smallRect.width();
        int tempHeight = normalRect.height() + mTextPadding + (smallRect.top + smallRect.bottom) / 2;
        if (widthMode == ViewGroup.LayoutParams.WRAP_CONTENT || heightMode == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(tempWidth, tempHeight);
        } else {
            if (tempWidth > width || tempHeight > height) {
                width = tempWidth;
                height = tempHeight;
                setMeasuredDimension(width, height);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(smallText, normalRect.width() + mTextPadding, canvas.getHeight() - normalRect.height() / 2 + (smallRect.top + smallRect.bottom) / 2, mPaint);
    }

    public void bind(BooheeRuler ruler) {
        ruler.addCallback(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTargetId != 0) {
            View ruler = getRootView().findViewById(mTargetId);
            if (ruler instanceof BooheeRuler) {
                ((BooheeRuler) ruler).addCallback(this);
            }
        }
    }

    @Override
    public void onScaleChanging(float scale) {
        setText(String.valueOf(scale / 10));
    }
}