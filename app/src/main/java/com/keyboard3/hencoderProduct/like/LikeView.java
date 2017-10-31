package com.keyboard3.hencoderProduct.like;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author keyboard3
 * @date 2017/10/30
 */

public class LikeView extends LinearLayout {
    int spacePadding;
    private int mAnimTime = 500;
    private LikeNumView likeNumView;
    private LikeImageView likeImageView;
    private AnimatorSet animatorSet;
    private boolean isLike = false;

    public LikeView(Context context) {
        super(context);
    }

    public LikeView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public LikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setOrientation(HORIZONTAL);
        spacePadding = 0;
        addView(new LikeImageView(getContext()));
        addView(new LikeNumView(getContext()));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!animatorSet.isRunning()) {
                    likeNumView.changeLike(isLike);
                    isLike = !isLike;
                    likeImageView.setLike(isLike);
                    animatorSet.start();
                }
            }
        });
    }

    public void setNum(int num) {
        likeNumView.setNum(num);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animatorSet.end();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mMoveY = getMeasuredHeight() / 2;

        ObjectAnimator numAnimator = ObjectAnimator.ofInt(likeNumView, "translationY", 0, mMoveY);
        numAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                likeNumView.init();
            }
        });
        numAnimator.setDuration(mAnimTime);

        ObjectAnimator imageAnimator = ObjectAnimator.ofFloat(likeImageView, "animProgress", 0, 1);
        imageAnimator.setDuration(mAnimTime);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(numAnimator, imageAnimator);

        setMeasuredDimension(likeNumView.getMeasuredWidth() + likeImageView.getMeasuredWidth() + spacePadding, getMeasuredHeight());
    }

    public void setLike(boolean like) {
        isLike = like;
        likeNumView.setLiked(isLike);
        likeImageView.setLike(isLike);
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        likeImageView = (LikeImageView) getChildAt(0);
        likeNumView = (LikeNumView) getChildAt(1);
        likeImageView.spacePadding = spacePadding;
    }
}
