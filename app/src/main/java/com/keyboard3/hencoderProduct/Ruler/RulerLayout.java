package com.keyboard3.hencoderProduct.Ruler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.keyboard3.hencoderProduct.R;


public class RulerLayout extends RelativeLayout {
    RulerView view;
    Button animateBt;

    public RulerLayout(Context context) {
        super(context);
    }

    public RulerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RulerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        view = (RulerView) findViewById(R.id.objectAnimatorView);
        animateBt = (Button) findViewById(R.id.animateBt);

        OnClickListener listener = new OnClickListener() {

            private int translationX = 600;

            @Override
            public void onClick(View v) {
                translationX += 600;
                view.moveTranslationX(translationX);
            }
        };
        animateBt.setOnClickListener(listener);
        view.setOnClickListener(listener);
    }
}
