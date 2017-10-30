package com.keyboard3.hencoderProduct.like;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.keyboard3.hencoderProduct.R;

/**
 * @author keyboard3
 * @date 2017/10/30
 */

public class LikeImageView extends View {
    private Paint mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean liked = false;
    private float animProgress;
    private int mAnimTime = 500;
    private Bitmap mUnlikeBitmap;
    private Bitmap mLikedBitmap;
    private Bitmap mShiningBitmap;
    ObjectAnimator mAnimator;
    private int startX;

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
        mUnlikeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_unselected);
        mLikedBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected);
        mShiningBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected_shining);
        startX = (int) (mLikedBitmap.getWidth() * 0.05);
        createAnim();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAnimator.isRunning()) {
                    liked = !liked;
                }
                mAnimator.start();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) (mLikedBitmap.getWidth() * 1.1);
        int height = mLikedBitmap.getHeight() + mShiningBitmap.getHeight();
        setMeasuredDimension(width, height);
    }

    private void createAnim() {
        mAnimator = ObjectAnimator.ofFloat(this, "animProgress", 0, 1);
        mAnimator.setDuration(mAnimTime);
    }

    public void setLike(boolean isLike) {
        liked = isLike;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        //绘制点赞图片
        int likeTop = centerY - mUnlikeBitmap.getHeight() / 2;
        drawLike(canvas, likeTop, startX);
        //绘制闪光
        drawShining(canvas, likeTop, startX);
    }

    private void drawLike(Canvas canvas, int likeTop, int likeLeft) {
        boolean mLike = liked;
        if (mLike && animProgress > 0.9 && animProgress < 1) {
            canvas.save();
            canvas.translate((float) (likeLeft - mLikedBitmap.getWidth() * 0.05), (float) (likeTop - mLikedBitmap.getHeight() * 0.05));
            canvas.scale(1.1f, 1.1f);
            canvas.drawBitmap(mLikedBitmap, 0, 0, mImagePaint);
            canvas.restore();
        } else if (mLike && animProgress > 0.5) {
            canvas.drawBitmap(mLike ? mLikedBitmap : mUnlikeBitmap, likeLeft, likeTop, mImagePaint);
        } else {
            canvas.drawBitmap(mUnlikeBitmap, likeLeft, likeTop, mImagePaint);
        }
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
}
