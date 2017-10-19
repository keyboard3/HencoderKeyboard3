package com.keyboard3.hencoderProduct.movement;

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
import com.keyboard3.hencoderProduct.Utils;

import java.util.Random;


/**
 * @author keyboard3
 */
public class MoveView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private boolean mDrawInner = false;
    private boolean mDrawOut = false;
    private boolean mDrawLoading = true;
    private String mStepNum;
    private String mKmNum;
    private String mCalNum;
    private Bitmap mWatchBitmap;
    private Random mRandom;
    private int transparentWhite;
    private int degree = 0;
    private int maxMove;
    private int centerX;
    private int centerY;


    public MoveView(Context context) {
        super(context);
    }

    public MoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setWillNotDraw(false);
        mPaint.setTextAlign(Paint.Align.CENTER);
        transparentWhite = Color.parseColor("#00ffffff");
        maxMove = (int) Utils.dpToPixel(50);
        mWatchBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_watch);
        mRandom = new Random();
        mStepNum = "2274";
        mKmNum = "1.5公里";
        mCalNum = "34千卡";
    }

    public void startAnimal() {
        mDrawInner = false;
        mDrawOut = false;
        mDrawLoading = true;
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator0 = ObjectAnimator.ofInt(MoveView.this, "degree", 0, 480);
        animator0.setDuration(3000);
        animator0.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mDrawLoading = false;
            }
        });
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "translationY", 0, -maxMove);
        animator1.setDuration(500);
        animator1.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mDrawOut = true;
            }
        });
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "translationY", -maxMove, 0);
        animator2.setDuration(500);
        animator2.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mDrawInner = true;
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
        mAnimatorSet.end();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + 200);//延长内容
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        //绘制步数
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(90);
        mPaint.setColor(Color.parseColor("#ffffff"));
        Rect stepNumRect = new Rect();
        mPaint.getTextBounds(mStepNum, 0, mStepNum.length() - 1, stepNumRect);
        int stepNumBaseY = centerY - (stepNumRect.top + stepNumRect.bottom) / 2;
        canvas.drawText(mStepNum, centerX, stepNumBaseY, mPaint);
        //绘制km
        mPaint.setTextSize(24);
        mPaint.setColor(Color.parseColor("#b1d6f8"));
        Rect kmNumRect = new Rect();
        mPaint.getTextBounds(mKmNum, 0, mKmNum.length() - 1, kmNumRect);
        int kmNumBaseX = centerX - 20 - (kmNumRect.left + kmNumRect.right) / 2;
        int kmNumBaseY = stepNumBaseY + 30 + kmNumRect.height();
        canvas.drawText(mKmNum, kmNumBaseX, kmNumBaseY, mPaint);
        //绘制卡路里
        Rect calNumRect = new Rect();
        mPaint.getTextBounds(mCalNum, 0, mCalNum.length() - 1, calNumRect);
        int calNumBaseX = centerX + 20 + (calNumRect.left + calNumRect.right) / 2;
        int calNumBaseY = stepNumBaseY + 30 + kmNumRect.height();
        canvas.drawText(mCalNum, calNumBaseX, calNumBaseY, mPaint);
        //绘制中间线
        mPaint.setStrokeWidth(2);
        int centerLineTop = kmNumBaseY - kmNumRect.height();
        int centerLineBottom = centerLineTop + kmNumRect.height();
        canvas.drawLine(centerX, centerLineTop, centerX, centerLineBottom, mPaint);
        //绘制最底部手表
        int watchX = centerX - mWatchBitmap.getWidth() / 2;
        int watchY = centerLineBottom + 40;
        canvas.drawBitmap(mWatchBitmap, watchX, watchY, mPaint);
        //绘制刚开始的加载的旋转动画
        if (mDrawLoading) {
            canvas.save();
            canvas.rotate(degree, centerX, centerY);
            Shader mShader = new SweepGradient(centerX, centerY, transparentWhite, Color.WHITE);
            mPaint.setStrokeWidth(1);
            mPaint.setShader(mShader);
            mPaint.setStyle(Paint.Style.STROKE);
            int loadingRadius = (int) (0.65 * getMeasuredWidth() / 2);
            RectF loadingCircle = new RectF(centerX - loadingRadius, centerY - loadingRadius, centerX + loadingRadius, centerY + loadingRadius);
            Path loadingPath = new Path();
            float loadingLeft = loadingCircle.left, loadingTop = loadingCircle.top, loadingRight = loadingCircle.right, loadingBottom = loadingCircle.bottom;
            for (int i = 0; i < 20; i++) {
                int value = mRandom.nextInt(25);
                int sed = mRandom.nextInt(3);
                loadingCircle.left = loadingLeft + value + sed;
                loadingCircle.top = loadingTop + value - sed;
                loadingCircle.right = loadingRight - value + sed;
                loadingCircle.bottom = loadingBottom - value - sed;
                loadingPath.addArc(loadingCircle, 40, 320);
            }
            canvas.drawPath(loadingPath, mPaint);

            loadingPath.reset();
            int decorPointX = centerX + loadingRadius;
            int decorPointY = centerY + 5;
            int tempX, tempY;
            for (int i = 0; i < 10; i++) {
                int value0 = mRandom.nextInt(i + 20);
                int value = i * 2 + mRandom.nextInt(i + 20);
                tempX = decorPointX - value0;
                tempY = decorPointY - i * 2 - value;
                loadingPath.addCircle(tempX, tempY, 5, Path.Direction.CCW);
            }
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(loadingPath, mPaint);
            mPaint.setShader(null);
            canvas.restore();
        }
        //绘制外轮廓
        if (mDrawOut) {
            int outRadius = (int) ((0.65 + 0.15 * getPercent()) * getMeasuredWidth() / 2);
            mPaint.setStrokeWidth(25);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, outRadius, mPaint);
        }
        //绘制内轮廓
        if (mDrawInner) {
            mPaint.setStrokeWidth(5);
            int innerRadius = (int) (0.55 * getMeasuredWidth() / 2);
            int startRunAngle = -90, runedAngle = 270, startUnRunAngle = startRunAngle + runedAngle, unRunedAngle = 360 - runedAngle;
            RectF innerCircle = new RectF(centerX - innerRadius, centerY - innerRadius, centerX + innerRadius, centerY + innerRadius);

            PathEffect effects = new DashPathEffect(new float[]{2, 4}, 1);
            Path unRunPath = new Path();
            unRunPath.addArc(innerCircle, startUnRunAngle, unRunedAngle);
            mPaint.setPathEffect(effects);
            canvas.drawPath(unRunPath, mPaint);
            mPaint.setPathEffect(null);

            mPaint.setColor(Color.WHITE);
            Path runedPath = new Path();
            runedPath.addArc(innerCircle, startRunAngle, runedAngle);
            canvas.drawPath(runedPath, mPaint);
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

    @SuppressWarnings("unused")
    public void setDegree(int degree) {
        this.degree = degree;
        invalidate();
    }

    public abstract class AnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }
    }
}