package com.keyboard3.hencoderProduct.filpboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.keyboard3.hencoderProduct.R;


/**
 * @author keyboard3
 */
public class FlipboardView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Camera mCamera = new Camera();
    private Bitmap mBitmap;
    private int turnoverDegreeFirst;
    private int turnoverDegreeLast;
    private int degree;

    public FlipboardView(Context context) {
        super(context);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.filpboard);
    }

    public void clearCanvas() {
        degree = 0;
        turnoverDegreeFirst = 0;
        turnoverDegreeLast = 0;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int x = centerX - bitmapWidth / 2;
        int y = centerY - bitmapHeight / 2;


        //绘制上半部分
        canvas.save();
        canvas.rotate(-degree, centerX, centerY);
        canvas.clipRect(0, 0, centerX, getHeight());
        canvas.rotate(degree, centerX, centerY);

        //翻折画布
        mCamera.save();
        mCamera.rotateX(-turnoverDegreeLast);
        canvas.translate(centerX, centerY);
        mCamera.applyToCanvas(canvas);
        canvas.translate(-centerX, -centerY);
        mCamera.restore();

        canvas.drawBitmap(mBitmap, x, y, mPaint);
        canvas.restore();

        //绘制右边部分
        canvas.save();
        canvas.rotate(-degree, centerX, centerY);
        canvas.clipRect(centerX, 0, getWidth(), getHeight());

        //翻折画布
        mCamera.save();
        mCamera.rotateY(-turnoverDegreeFirst);
        canvas.translate(centerX, centerY);
        mCamera.applyToCanvas(canvas);
        canvas.translate(-centerX, -centerY);
        mCamera.restore();
        canvas.rotate(degree, centerX, centerY);

        //向画布绘制内容
        canvas.drawBitmap(mBitmap, x, y, mPaint);
        canvas.restore();

    }

    @SuppressWarnings("unused")
    public void setDegree(int degree) {
        this.degree = degree;
        invalidate();
    }

    @SuppressWarnings("unused")
    public void setTurnoverDegreeLast(int turnoverDegreeLast) {
        this.turnoverDegreeLast = turnoverDegreeLast;
        invalidate();
    }

    @SuppressWarnings("unused")
    public void setTurnoverDegreeFirst(int turnoverDegreeFirst) {
        this.turnoverDegreeFirst = turnoverDegreeFirst;
        invalidate();
    }
}
