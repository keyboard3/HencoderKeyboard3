package com.keyboard3.hencoderProduct.like;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author keyboard3
 * @date 2017/10/30
 */

public class LikeView extends LinearLayout {
    int spacePadding;
    private LikeNumView likeNumView;
    private LikeImageView likeImageView;

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
        spacePadding = 20;
        addView(new LikeImageView(getContext()));
        addView(new LikeNumView(getContext()));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        likeImageView = (LikeImageView) getChildAt(0);
        likeNumView = (LikeNumView) getChildAt(1);
    }
}
