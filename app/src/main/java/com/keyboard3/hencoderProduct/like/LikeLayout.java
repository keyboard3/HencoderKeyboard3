package com.keyboard3.hencoderProduct.like;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.keyboard3.hencoderProduct.R;


public class LikeLayout extends RelativeLayout {
    LikeView view1, view2, view3;
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

        view1 = (LikeView) findViewById(R.id.objectAnimatorView1);
        view2 = (LikeView) findViewById(R.id.objectAnimatorView2);
        view3 = (LikeView) findViewById(R.id.objectAnimatorView3);
        animateBt = (Button) findViewById(R.id.animateBt);

        animateBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.performClick();
                view2.performClick();
                view3.performClick();
            }
        });
    }
}
