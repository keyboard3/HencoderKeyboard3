package com.keyboard3.hencoderProduct.Like;

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
    private Button addBtn;
    private Button decentBtn;

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
        addBtn = (Button) findViewById(R.id.addBtn);
        decentBtn = (Button) findViewById(R.id.decentBtn);
        animateBt = (Button) findViewById(R.id.animateBt);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setDrawNum(Integer.parseInt(etNum.getText().toString()));
            }
        };
        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setLike(true);
            }
        });
        decentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setLike(false);
            }
        });
        animateBt.setOnClickListener(listener);
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                view.setLike(!view.isLiked());
            }
        });
    }
}
