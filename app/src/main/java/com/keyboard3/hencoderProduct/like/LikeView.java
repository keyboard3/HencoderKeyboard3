package com.keyboard3.hencoderProduct.like;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.keyboard3.hencoderProduct.R;

/**
 * @author keyboard3
 * @date 2017/10/30
 */

public class LikeView extends LinearLayout {
    private int mAnimTime = 500;
    private LikeNumView likeNumView;
    private LikeImageView likeImageView;
    private AnimatorSet animatorSet;
    private boolean isLike = false;

    public LikeView(Context context) {
        super(context);
    }

    public LikeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray custom = context.obtainStyledAttributes(
                attrs, R.styleable.LikeView, defStyleAttr, 0);
        int likeNum = custom.getInt(R.styleable.LikeView_likeNum, 0);
        boolean liked = custom.getBoolean(R.styleable.LikeView_liked, false);
        float leftPadding = custom.getDimension(R.styleable.LikeView_leftPadding, 0);
        float middlePadding = custom.getDimension(R.styleable.LikeView_middlePadding, 0);
        float rightPadding = custom.getDimension(R.styleable.LikeView_rightPadding, 0);
        int likeSrc = custom.getResourceId(R.styleable.LikeView_likeSrc, R.mipmap.ic_messages_like_selected);
        int unlikeSrc = custom.getResourceId(R.styleable.LikeView_unlikeSrc, R.mipmap.ic_messages_like_unselected);
        int shiningSrc = custom.getResourceId(R.styleable.LikeView_shiningSrc, R.mipmap.ic_messages_like_selected_shining);
        likeImageView.setShiningdSrc(shiningSrc);
        likeImageView.setLikedSrc(likeSrc);
        likeImageView.setUnlikeSrc(unlikeSrc);
        likeNumView.setRightPadding(rightPadding);
        likeImageView.setLeftPadding(leftPadding);
        likeImageView.setMiddlePadding(middlePadding);
        likeNumView.setNum(likeNum);
        setLike(liked);
    }

    {
        setOrientation(HORIZONTAL);
        likeImageView = new LikeImageView(getContext());
        addView(likeImageView);
        likeNumView = new LikeNumView(getContext());
        addView(likeNumView);
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

        setMeasuredDimension(likeImageView.getMeasuredWidth() + likeNumView.getMeasuredWidth(), getMeasuredHeight());
    }

    public void setLike(boolean like) {
        isLike = like;
        likeNumView.setLiked(isLike);
        likeImageView.setLike(isLike);
        invalidate();
    }
}
