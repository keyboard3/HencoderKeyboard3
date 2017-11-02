package com.keyboard3.hencoderProduct.like;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author keyboard3
 * @date 2017/10/30
 */

public class LikeImageView extends View {
    private Paint mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean liked = false;
    private float animProgress;
    private Bitmap mUnlikeBitmap;
    private Bitmap mLikedBitmap;
    private Bitmap mShiningBitmap;
    private float leftPadding;
    private float middlePadding;
    private float startX;
    private int centerY;

    public LikeImageView(Context context) {
        super(context);
    }

    public LikeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mImagePaint.setColor(Color.parseColor("#c3c4c3"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        startX = mLikedBitmap.getWidth() * 0.1f + leftPadding;
        int width = (int) (mLikedBitmap.getWidth() * 1.1);
        int height = mLikedBitmap.getHeight() + mShiningBitmap.getHeight();
        setMeasuredDimension((int) (width + leftPadding + middlePadding), height);
    }

    public void setLike(boolean isLike) {
        liked = isLike;
        if (!liked) {
            animProgress = 0;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerY = getHeight() / 2;
        //绘制放大圈
        drawCircle(canvas, (int) (startX + mLikedBitmap.getWidth() / 2), centerY);
        //绘制点赞图片
        int likeTop = centerY - mUnlikeBitmap.getHeight() / 2;
        drawLike(canvas, likeTop, (int) startX);
        //绘制闪光
        drawShining(canvas, likeTop, (int) startX);
    }

    private void drawCircle(Canvas canvas, int centerX, int centerY) {
        float radius = 0;
        int alpha = 0;
        if (liked) {
            //透明变实体
            if (animProgress > 0 && animProgress <= 0.5) {
                alpha = (int) (255 * (0.5 + animProgress));
            } else { //实体变透明
                alpha = (int) (255 * (1 - (animProgress - 0.5) * 2));
            }
            radius = (float) (0.6 + animProgress * 0.7);
        }
        mImagePaint.setColor(Color.parseColor("#cc775c"));
        mImagePaint.setAlpha(alpha);
        mImagePaint.setStyle(Paint.Style.STROKE);
        mImagePaint.setStrokeWidth(3);
        canvas.drawCircle(centerX, centerY, radius * mLikedBitmap.getWidth() / 2, mImagePaint);
        mImagePaint.setColor(Color.parseColor("#c3c4c3"));
        mImagePaint.setStyle(Paint.Style.FILL);
    }

    private void drawLike(Canvas canvas, int likeTop, int likeLeft) {
        Bitmap bitmap = null;
        float scale = 0;
        if (liked) {
            //灰色一下变小
            if (animProgress > 0 && animProgress <= 0.1) {
                bitmap = mUnlikeBitmap;
                scale = -0.01f;
            }
            //红色小且半透明 变正常过程就变成了实体
            if (animProgress > 0.1 && animProgress <= 0.5) {
                mImagePaint.setAlpha((int) (255 * (0.5 + animProgress)));
            } else {
                mImagePaint.setAlpha(255);
            }
            //红色放大
            if (animProgress > 0.1 && animProgress <= 0.9) {
                bitmap = mLikedBitmap;
                scale = (float) (-0.01f + animProgress * 0.1);
            }
            //一瞬间变正常
            if (animProgress > 0.9 || animProgress == 0) {
                bitmap = mLikedBitmap;
                scale = 0;
            }
        } else {
            //红色缩小 变半透明
            if (animProgress > 0 && animProgress <= 0.5) {
                bitmap = mLikedBitmap;
                mImagePaint.setAlpha((int) (255 * (0.5 + animProgress)));
                scale = (float) (-animProgress * 0.1);
            }
            //一半的时候变灰色
            if (animProgress > 0.5 || animProgress == 0) {
                mImagePaint.setAlpha(255);
                bitmap = mUnlikeBitmap;
                scale = 0;
            }
        }
        canvas.save();
        canvas.translate((float) (likeLeft - bitmap.getWidth() * 0.05), (float) (likeTop - bitmap.getHeight() * 0.05));
        canvas.scale(1 + scale, 1 + scale);
        canvas.drawBitmap(bitmap, 0, 0, mImagePaint);
        canvas.restore();
    }

    private void drawShining(Canvas canvas, int likeTop, int likeLeft) {
        if (liked) {
            float scale = animProgress;
            int shiningTop = (int) (likeTop - scale * mShiningBitmap.getHeight() / 2);
            int shiningLeft = (int) (likeLeft + (mLikedBitmap.getWidth() - scale * mShiningBitmap.getWidth()) / 2);

            canvas.save();
            canvas.translate(shiningLeft, shiningTop);
            canvas.scale(scale, scale);
            canvas.drawBitmap(mShiningBitmap, 0, 0, mImagePaint);
            canvas.restore();
        }
    }

    @SuppressWarnings("unused")
    public void setAnimProgress(float animProgress) {
        this.animProgress = animProgress;
        invalidate();
    }

    public void setMiddlePadding(float middlePadding) {
        this.middlePadding = middlePadding;
    }

    public void setLeftPadding(float leftPadding) {
        this.leftPadding = leftPadding;
    }

    public void setUnlikeSrc(@IdRes int unlikeSrc) {
        mUnlikeBitmap = BitmapFactory.decodeResource(getResources(), unlikeSrc);
    }

    public void setLikedSrc(@IdRes int likeSrc) {
        mLikedBitmap = BitmapFactory.decodeResource(getResources(), likeSrc);
    }

    public void setShiningdSrc(@IdRes int shiningSrc) {
        mShiningBitmap = BitmapFactory.decodeResource(getResources(), shiningSrc);
    }
}
