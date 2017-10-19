package com.keyboard3.hencoderProduct.movement;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.keyboard3.hencoderProduct.R;


public class MoveLayout extends RelativeLayout {
    MoveView view;
    Button animateBt;

    public MoveLayout(Context context) {
        super(context);
    }

    public MoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        view = (MoveView) findViewById(R.id.objectAnimatorView);
        animateBt = (Button) findViewById(R.id.animateBt);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                view.startAnimal();
            }
        };
        animateBt.setOnClickListener(listener);
        view.setOnClickListener(listener);
    }
}
