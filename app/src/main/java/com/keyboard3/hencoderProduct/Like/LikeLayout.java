package com.keyboard3.hencoderProduct.Like;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.keyboard3.hencoderProduct.R;


public class LikeLayout extends RelativeLayout {
    LikeView view;
    Button animateBt;

    public LikeLayout(Context context) {
        super(context);
    }

    public LikeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        view = (LikeView) findViewById(R.id.objectAnimatorView);
        animateBt = (Button) findViewById(R.id.animateBt);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setLike(!view.isLiked());
            }
        };
        animateBt.setOnClickListener(listener);
        view.setOnClickListener(listener);
    }
}
