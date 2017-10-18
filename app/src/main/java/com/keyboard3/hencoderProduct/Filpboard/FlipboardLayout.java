package com.keyboard3.hencoderProduct.Filpboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.keyboard3.hencoderProduct.R;


public class FlipboardLayout extends RelativeLayout {
    FlipboardView view;
    Button animateBt;

    public FlipboardLayout(Context context) {
        super(context);
    }

    public FlipboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        view = (FlipboardView) findViewById(R.id.objectAnimatorView);
        animateBt = (Button) findViewById(R.id.animateBt);

        animateBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                view.clearCanvas();

                ObjectAnimator animator1 = ObjectAnimator.ofInt(view, "turnoverDegreeFirst", 0, 30);
                animator1.setDuration(1000);
                animator1.setInterpolator(new LinearInterpolator());

                ObjectAnimator animator2 = ObjectAnimator.ofInt(view, "degree", 0, 270);
                animator2.setDuration(2000);
                animator2.setInterpolator(new AccelerateDecelerateInterpolator());

                ObjectAnimator animator3 = ObjectAnimator.ofInt(view, "turnoverDegreeLast", 0, 30);
                animator3.setDuration(1000);
                animator3.setInterpolator(new LinearInterpolator());

                AnimatorSet animatorSet = new AnimatorSet();
                // 两个动画依次执行
                animatorSet.playSequentially(animator1, animator2, animator3);
                animatorSet.start();
            }
        });
    }
}
