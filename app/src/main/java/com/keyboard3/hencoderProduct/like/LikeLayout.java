package com.keyboard3.hencoderProduct.like;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.keyboard3.hencoderProduct.R;


public class LikeLayout extends RelativeLayout {
    LikeView view;
    Button animateBt;
    private EditText etNum;

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
        etNum = (EditText) findViewById(R.id.editNum);
        animateBt = (Button) findViewById(R.id.animateBt);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setDrawNum(Integer.parseInt(etNum.getText().toString()));
                view.clear();
            }
        };
        animateBt.setOnClickListener(listener);
    }
}
