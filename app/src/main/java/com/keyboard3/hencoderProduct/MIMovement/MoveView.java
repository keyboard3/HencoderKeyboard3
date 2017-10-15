package com.keyboard3.hencoderProduct.MIMovement;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.keyboard3.hencoderProduct.R;

import java.util.Random;


public class MoveView extends View {
    Paint paint0 = new Paint(Paint.ANTI_ALIAS_FLAG);

    private AnimatorSet animatorSet = new AnimatorSet();
    private int degree = 0;
    private int centerX;
    private int centerY;
    private int maxMove = 100;
    private boolean drawInner = false;
    private boolean drawOut = false;
    private boolean drawLoading = true;
    private String stepNum = "2274";
    private Bitmap watchBitmap;
    private Random random;


    public MoveView(Context context) {
        super(context);
    }

    public MoveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        paint0.setTextAlign(Paint.Align.CENTER);
        watchBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_watch);
        random = new Random();
    }

    public void startAnimal() {
        drawInner = false;
        drawOut = false;
        drawLoading = true;
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator0 = ObjectAnimator.ofInt(MoveView.this, "degree", 0, 480);
        animator0.setDuration(3000);
        animator0.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawLoading = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "translationY", 0, -maxMove);
        animator1.setDuration(500);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "translationY", -maxMove, 0);
        animator2.setDuration(500);
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                drawOut = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawInner = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.playSequentially(animator0, animator1, animator2);
        animatorSet.start();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animatorSet.end();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + 200);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //刚开始外圈旋转 2周
        //外圈突然消失
        //整体内容往上移动40
        //同时 在绘制外圈圆轮廓放大1.3
        //然后下移 40  缩小至原来大小
        //然后再绘制出内圈圆
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        paint0.setStyle(Paint.Style.FILL);
        paint0.setTextSize(90);
        paint0.setColor(Color.parseColor("#ffffff"));
        Rect stepNumRect = new Rect();
        paint0.getTextBounds(stepNum, 0, stepNum.length() - 1, stepNumRect);
        int stepNumBaseY = centerY - (stepNumRect.top + stepNumRect.bottom) / 2;
        canvas.drawText(stepNum, centerX, stepNumBaseY, paint0);

        paint0.setTextSize(24);
        paint0.setColor(Color.parseColor("#b1d6f8"));
        String kmNum = "1.5公里";
        Rect kmNumRect = new Rect();
        paint0.getTextBounds(kmNum, 0, kmNum.length() - 1, kmNumRect);
        int kmNumBaseX = centerX - 20 - (kmNumRect.left + kmNumRect.right) / 2;
        int kmNumBaseY = stepNumBaseY + 30 + kmNumRect.height();
        canvas.drawText(kmNum, kmNumBaseX, kmNumBaseY, paint0);

        String calNum = "34千卡";
        Rect calNumRect = new Rect();
        paint0.getTextBounds(calNum, 0, calNum.length() - 1, calNumRect);
        int calNumBaseX = centerX + 20 + (calNumRect.left + calNumRect.right) / 2;
        int calNumBaseY = stepNumBaseY + 30 + kmNumRect.height();
        canvas.drawText(calNum, calNumBaseX, calNumBaseY, paint0);

        paint0.setStrokeWidth(2);
        int centerLineTop = kmNumBaseY - kmNumRect.height();
        int centerLineBottom = centerLineTop + kmNumRect.height();
        canvas.drawLine(centerX, centerLineTop, centerX, centerLineBottom, paint0);

        int watchX = centerX - watchBitmap.getWidth() / 2;
        int watchY = centerLineBottom + 40;
        canvas.drawBitmap(watchBitmap, watchX, watchY, paint0);

        if (drawLoading) {
            canvas.save();
            canvas.rotate(degree, centerX, centerY);
            Shader mShader = new SweepGradient(centerX, centerY, Color.parseColor("#00ffffff"), Color.WHITE);
            paint0.setStrokeWidth(1);
            paint0.setShader(mShader);
            paint0.setStyle(Paint.Style.STROKE);
            int loadingRadius = (int) (0.65 * getMeasuredWidth() / 2);
            RectF loadingCircle = new RectF(centerX - loadingRadius, centerY - loadingRadius, centerX + loadingRadius, centerY + loadingRadius);
            Path loadingPath = new Path();
            float loadingLeft = loadingCircle.left, loadingTop = loadingCircle.top, loadingRight = loadingCircle.right, loadingBottom = loadingCircle.bottom;
            for (int i = 0; i < 20; i++) {
                int value = random.nextInt(25);
                int sed = random.nextInt(3);
                loadingCircle.left = loadingLeft + value + sed;
                loadingCircle.top = loadingTop + value - sed;
                loadingCircle.right = loadingRight - value + sed;
                loadingCircle.bottom = loadingBottom - value - sed;
                loadingPath.addArc(loadingCircle, 40, 320);
            }
            canvas.drawPath(loadingPath, paint0);

            loadingPath.reset();
            int decorPointX = centerX + loadingRadius;
            int decorPointY = centerY + 5;
            int tempX, tempY;
            for (int i = 0; i < 10; i++) {
                int value0 = random.nextInt(i + 20);
                int value = i * 2 + random.nextInt(i + 20);
                tempX = decorPointX - value0;
                tempY = decorPointY - i * 2 - value;
                loadingPath.addCircle(tempX, tempY, 5, Path.Direction.CCW);
            }
            paint0.setStyle(Paint.Style.FILL);
            canvas.drawPath(loadingPath, paint0);
            paint0.setShader(null);
            canvas.restore();
        }
        if (drawOut) {
            int outRadius = (int) ((0.65 + 0.15 * getPercent()) * getMeasuredWidth() / 2);
            paint0.setStrokeWidth(25);
            paint0.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, outRadius, paint0);
        }

        if (drawInner) {
            paint0.setStrokeWidth(5);
            int innerRadius = (int) (0.55 * getMeasuredWidth() / 2);
            int startRunAngle = -90, runedAngle = 270, startUnRunAngle = startRunAngle + runedAngle, unRunedAngle = 360 - runedAngle;
            RectF innerCircle = new RectF(centerX - innerRadius, centerY - innerRadius, centerX + innerRadius, centerY + innerRadius);

            PathEffect effects = new DashPathEffect(new float[]{2, 4}, 1);
            Path unRunPath = new Path();
            unRunPath.addArc(innerCircle, startUnRunAngle, unRunedAngle);
            paint0.setPathEffect(effects);
            canvas.drawPath(unRunPath, paint0);
            paint0.setPathEffect(null);

            paint0.setColor(Color.parseColor("#ffffff"));
            Path runedPath = new Path();
            runedPath.addArc(innerCircle, startRunAngle, runedAngle);
            canvas.drawPath(runedPath, paint0);
        }
    }

    private float getPercent() {
        return Math.abs(getTranslationY()) / maxMove;
    }

    @Override
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        invalidate();
    }

    public void setDegree(int degree) {
        this.degree = degree;
        invalidate();
    }
}