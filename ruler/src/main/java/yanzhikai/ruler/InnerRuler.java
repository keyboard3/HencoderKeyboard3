package yanzhikai.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.Px;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * 内部尺子抽象类
 */

public class InnerRuler extends View {
    protected Context mContext;
    protected BooheeRuler mParent;

    protected Paint mSmallScalePaint, mBigScalePaint, mTextPaint, mOutLinePaint;
    /**
     * 当前刻度值
     */
    protected float mCurrentScale = 0;
    /**
     * 最大刻度数
     */
    protected int mMaxLength = 0;
    /**
     * 长度、最小可滑动值、最大可滑动值
     */
    protected int mLength, mMinPosition = 0, mMaxPosition = 0;
    /**
     * 控制滑动
     */
    protected OverScroller mOverScroller;
    /**
     * 一格大刻度多少格小刻度
     */
    protected int mCount = 10;
    /**
     * 提前刻画量
     */
    protected int mDrawOffset;
    /**
     * 速度获取
     */
    protected VelocityTracker mVelocityTracker;
    /**
     * 惯性最大最小速度
     */
    protected int mMaximumVelocity, mMinimumVelocity;
    /**
     * 回调接口
     */
    protected List<RulerCallback> mRulerCallbacks = new ArrayList<>();
    /**
     * 边界效果
     */
    protected EdgeEffect mStartEdgeEffect, mEndEdgeEffect;
    /**
     * 边缘效应长度
     */
    protected int mEdgeLength;
    int flag;
    private float mLastY;
    private float mLastX;

    public InnerRuler(Context context, BooheeRuler booheeRuler, int flag) {
        super(context);
        mParent = booheeRuler;
        this.flag = flag;
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mMaxLength = mParent.getMaxScale() - mParent.getMinScale();
        mCurrentScale = mParent.getCurrentScale();
        mCount = mParent.getCount();
        mDrawOffset = mCount * mParent.getInterval() / 2;

        initPaints();

        mOverScroller = new OverScroller(mContext);

        //配置速度
        mVelocityTracker = VelocityTracker.obtain();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

        initEdgeEffects();

        //第一次进入，跳转到设定刻度
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                scroll2Scale(mCurrentScale);
            }
        });
        checkAPILevel();
    }

    private void initPaints() {
        mSmallScalePaint = new Paint();
        mSmallScalePaint.setStrokeWidth(mParent.getSmallScaleWidth());
        mSmallScalePaint.setColor(mParent.getScaleColor());
        mSmallScalePaint.setStrokeCap(Paint.Cap.ROUND);

        mBigScalePaint = new Paint();
        mBigScalePaint.setColor(mParent.getScaleColor());
        mBigScalePaint.setStrokeWidth(mParent.getBigScaleWidth());
        mBigScalePaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mParent.getTextColor());
        mTextPaint.setTextSize(mParent.getTextSize());
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mOutLinePaint = new Paint();
        mOutLinePaint.setStrokeWidth(0);
        mOutLinePaint.setColor(mParent.getScaleColor());
    }

    /**
     * 初始化边缘效果
     */
    public void initEdgeEffects() {
        if (mParent.canEdgeEffect()) {
            if (mStartEdgeEffect == null || mEndEdgeEffect == null) {
                mStartEdgeEffect = new EdgeEffect(mContext);
                mEndEdgeEffect = new EdgeEffect(mContext);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mStartEdgeEffect.setColor(mParent.getEdgeColor());
                    mEndEdgeEffect.setColor(mParent.getEdgeColor());
                }
                mEdgeLength = mParent.getCursorHeight() + mParent.getInterval() * mParent.getCount();
            }
        }
    }


    private void checkAPILevel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_NONE, null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mRulerCallbacks = null;
    }

    /**
     * 设置尺子当前刻度
     *
     * @param currentScale
     */
    public void setCurrentScale(float currentScale) {
        this.mCurrentScale = currentScale;
        scroll2Scale(mCurrentScale);
    }

    public void addRulerCallback(RulerCallback RulerCallback) {
        mRulerCallbacks.add(RulerCallback);
    }

    public float getCurrentScale() {
        return mCurrentScale;
    }

    //======绘制逻辑=======
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRulerBody(canvas, flag);
    }

    protected void drawRulerBody(Canvas canvas, int flag) {
        float location = 0, length;
        if (isVerticalRuler(flag)) {
            //刻度是水平的 Y是固定的
            location = getScrollY();
            length = canvas.getHeight();
        } else {
            //刻度是竖直的 x是固定的
            location = getScrollX();
            length = canvas.getWidth();
        }
        int start = (int) ((location - mDrawOffset) / mParent.getInterval() + mParent.getMinScale());
        int end = (int) ((location + length + mDrawOffset) / mParent.getInterval() + mParent.getMinScale());

        for (int i = start; i <= end; i++) {
            if (i >= mParent.getMinScale() && i <= mParent.getMaxScale()) {
                if (i % mCount == 0) {
                    PointF[] line = getLine(mParent, flag, mParent.getBigScaleLength(), i, canvas.getHeight(), canvas.getWidth());
                    canvas.drawLine(line[0].x, line[0].y, line[1].x, line[1].y, mBigScalePaint);

                    PointF textEndPoint = getTextPoint(mParent, flag, i, canvas.getHeight(), canvas.getWidth());
                    canvas.drawText(String.valueOf(i / 10), textEndPoint.x, textEndPoint.y, mTextPaint);
                } else {
                    PointF[] line = getLine(mParent, flag, mParent.getSmallScaleLength(), i, canvas.getHeight(), canvas.getWidth());
                    canvas.drawLine(line[0].x, line[0].y, line[1].x, line[1].y, mSmallScalePaint);
                }
            }
        }
        //画轮廓线
        PointF[] outLine = getOutLine(this, flag, canvas.getHeight(), canvas.getWidth());
        canvas.drawLine(outLine[0].x, outLine[0].y, outLine[1].x, outLine[1].y, mOutLinePaint);
        PointF[] outLine2 = getOutLine(this, getNegativeFlag(flag), canvas.getHeight(), canvas.getWidth());
        canvas.drawLine(outLine2[0].x, outLine2[0].y, outLine2[1].x, outLine2[1].y, mOutLinePaint);
        PointF[] outLine3 = getOutLeftOrRightLine(this, flag, true, canvas.getHeight(), canvas.getWidth());
        canvas.drawLine(outLine3[0].x, outLine3[0].y, outLine3[1].x, outLine3[1].y, mOutLinePaint);
        PointF[] outLine4 = getOutLeftOrRightLine(this, flag, false, canvas.getHeight(), canvas.getWidth());
        canvas.drawLine(outLine4[0].x, outLine4[0].y, outLine4[1].x, outLine4[1].y, mOutLinePaint);
    }

    protected static boolean isVerticalRuler(int flag) {
        return flag == 0 || flag == 2;
    }

    protected static int getNegativeFlag(int flag) {
        return (flag + 2) % 4;
    }

    private static PointF getTextPoint(BooheeRuler mParent, int flag, float location, int height, int width) {
        PointF pointF = new PointF();
        float locationY = 0, locationX = 0;
        if (isVerticalRuler(flag)) {
            //刻度是水平的 Y是固定的
            locationY = (location - mParent.getMinScale()) * mParent.getInterval();
        } else {
            //刻度是竖直的 x是固定的
            locationX = (location - mParent.getMinScale()) * mParent.getInterval();
        }
        switch (flag) {
            case BooheeRuler.LEFT_LAYOUT:
                pointF.x = mParent.getTextMarginHead();
                pointF.y = locationY + mParent.getTextSize() / 2;
                break;
            case BooheeRuler.TOP_LAYOUT:
                pointF.x = locationX;
                pointF.y = mParent.getTextMarginHead();
                break;
            case BooheeRuler.RIGHT_LAYOUT:
                pointF.x = width - mParent.getTextMarginHead();
                pointF.y = locationY + mParent.getTextSize() / 2;
                break;
            case BooheeRuler.BOTTOM_LAYOUT:
                pointF.x = locationX;
                pointF.y = height - mParent.getTextMarginHead();
                break;
            default:
        }
        return pointF;
    }


    private static PointF[] getLine(BooheeRuler mParent, int flag, int length, float location, int height, int width) {
        PointF startPoint = new PointF();
        PointF stopPoint = new PointF();
        float locationY = 0, locationX = 0;
        if (isVerticalRuler(flag)) {
            //刻度是水平的 Y是固定的
            locationY = (location - mParent.getMinScale()) * mParent.getInterval();
        } else {//水平的尺子
            //刻度是竖直的 x是固定的
            locationX = (location - mParent.getMinScale()) * mParent.getInterval();
        }
        switch (flag) {
            case BooheeRuler.LEFT_LAYOUT:
                startPoint.y = locationY;
                stopPoint.x = length;
                stopPoint.y = locationY;
                break;
            case BooheeRuler.TOP_LAYOUT:
                startPoint.x = locationX;
                stopPoint.x = locationX;
                stopPoint.y = length;
                break;
            case BooheeRuler.RIGHT_LAYOUT:
                startPoint.x = width - length;
                startPoint.y = locationY;
                stopPoint.x = width;
                stopPoint.y = locationY;
                break;
            case BooheeRuler.BOTTOM_LAYOUT:
                startPoint.x = locationX;
                startPoint.y = height - length;
                stopPoint.x = locationX;
                stopPoint.y = height;
                break;
            default:
        }
        return new PointF[]{startPoint, stopPoint};
    }

    private static PointF[] getOutLine(View contentView, int flag, int height, int width) {
        PointF startPoint = new PointF();
        PointF stopPoint = new PointF();
        float locationY = 0, locationX = 0;
        if (isVerticalRuler(flag)) {
            //边界线是竖直的 竖直滚动的起始位置
            locationY = contentView.getScrollY();
        } else {
            //边界线是水平的 水平滚动的起始位置
            locationX = contentView.getScrollX();
        }
        switch (flag) {
            case BooheeRuler.LEFT_LAYOUT:
                startPoint.y = locationY;
                stopPoint.y = locationY + height;
                break;
            case BooheeRuler.TOP_LAYOUT:
                startPoint.x = locationX;
                stopPoint.x = locationX + width;
                break;
            case BooheeRuler.RIGHT_LAYOUT:
                startPoint.y = locationY;
                stopPoint.y = locationY + height;
                startPoint.x = width - 0.1F;
                stopPoint.x = width - 0.1F;
                break;
            case BooheeRuler.BOTTOM_LAYOUT:
                startPoint.x = locationX;
                startPoint.y = height - 0.1F;
                stopPoint.x = locationX + width;
                stopPoint.y = height - 0.1F;
                break;
            default:
        }
        return new PointF[]{startPoint, stopPoint};
    }

    private static PointF[] getOutLeftOrRightLine(View contentView, int flag, boolean isleft, int height, int width) {
        PointF startPoint = new PointF();
        PointF stopPoint = new PointF();
        float locationY = 0, locationX = 0;
        if (isVerticalRuler(flag)) {
            //边界线是竖直的 竖直滚动的起始位置
            locationY = contentView.getScrollY();
        } else {
            //边界线是水平的 水平滚动的起始位置
            locationX = contentView.getScrollX();
        }
        switch (flag) {
            case BooheeRuler.LEFT_LAYOUT:
                if (isleft) {
                    startPoint.y = locationY;
                    stopPoint.y = locationY;
                    stopPoint.x = width;
                } else {
                    startPoint.y = locationY + height - 0.1F;
                    stopPoint.y = locationY + height - 0.1F;
                    stopPoint.x = width;
                }
                break;
            case BooheeRuler.TOP_LAYOUT:
                if (isleft) {
                    startPoint.x = locationX;
                    stopPoint.x = locationX;
                    stopPoint.y = height;
                } else {
                    startPoint.x = locationX + width - 0.1F;
                    stopPoint.x = locationX + width - 0.1F;
                    stopPoint.y = height;
                }
                break;
            case BooheeRuler.RIGHT_LAYOUT:
                if (isleft) {
                    startPoint.y = locationY;
                    stopPoint.y = locationY;
                    startPoint.x = width - 0.1F;
                } else {
                    startPoint.y = locationY + height - 0.1F;
                    stopPoint.y = locationY + height - 0.1F;
                    startPoint.x = width - 0.1F;
                }
                break;
            case BooheeRuler.BOTTOM_LAYOUT:
                if (isleft) {
                    startPoint.x = locationX;
                    startPoint.y = height - 0.1F;
                    stopPoint.x = locationX;
                } else {
                    startPoint.x = locationX + width - 0.1F;
                    startPoint.y = height - 0.1F;
                    stopPoint.x = locationX + width - 0.1F;
                }
                break;
            default:
        }
        return new PointF[]{startPoint, stopPoint};
    }

    //======滑动事件逻辑======
    //获取控件宽高，设置相应信息
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLength = (mParent.getMaxScale() - mParent.getMinScale()) * mParent.getInterval();
        int mHalf = (isVerticalRuler(flag) ? h : w) / 2;
        mMinPosition = -mHalf;
        mMaxPosition = mLength - mHalf;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float currentY = event.getY();
        float currentX = event.getX();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                mLastY = currentY;
                mLastX = currentX;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = isVerticalRuler(flag) ? 0 : mLastX - currentX;
                float moveY = isVerticalRuler(flag) ? mLastY - currentY : 0;
                scrollBy((int) moveX, (int) moveY);
                mLastY = currentY;
                mLastX = currentX;
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = 0, velocityX = 0;
                boolean startScroll = false;
                if (isVerticalRuler(flag)) {
                    velocityY = (int) mVelocityTracker.getYVelocity();
                    startScroll = Math.abs(velocityY) > mMinimumVelocity;
                } else {
                    velocityX = (int) mVelocityTracker.getXVelocity();
                    startScroll = Math.abs(velocityX) > mMinimumVelocity;
                }
                if (startScroll) {
                    fling(isVerticalRuler(flag), -velocityX, -velocityY);
                } else {
                    scrollBackToCurrentScale(isVerticalRuler(flag));
                }

                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                //releaseEdgeEffects();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                scrollBackToCurrentScale(isVerticalRuler(flag));
                //VelocityTracker回收
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                //releaseEdgeEffects();
                break;
            default:
        }
        return true;
    }

    private void fling(boolean isVertical, int vx, int vy) {
        int startX = 0, startY = 0, velocityX = 0, velocityY = 0, minX = 0, minY = 0, maxX = 0, maxY = 0;
        startX = isVertical ? 0 : getScrollX();
        startY = isVertical ? getScrollY() : 0;
        velocityX = isVertical ? 0 : vx;
        velocityY = isVertical ? vy : 0;
        int min = mMinPosition - mEdgeLength;
        int max = mMaxPosition + mEdgeLength;
        minX = isVertical ? 0 : min;
        minY = isVertical ? 0 : max;
        maxX = isVertical ? min : 0;
        maxY = isVertical ? max : 0;
        mOverScroller.fling(startX, startY, velocityX, velocityY, minX, minY, maxX, maxY);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
            if (!mOverScroller.computeScrollOffset() && mCurrentScale != Math.round(mCurrentScale)) {
                scrollBackToCurrentScale(isVerticalRuler(flag));
            }
            invalidate();
        }
    }


    protected void scrollBackToCurrentScale(boolean isVertical) {
        int startX = 0, startY = 0, destX, destY = 0;
        startX = isVertical ? 0 : getScrollX();
        startY = isVertical ? getScrollY() : 0;
        destX = isVertical ? 0 : scale2ScrollXY(Math.round(mCurrentScale)) - startX;
        destY = isVertical ? scale2ScrollXY(Math.round(mCurrentScale)) - startY : 0;
        mOverScroller.startScroll(startX, startY, destX, destY, 1000);
        invalidate();
    }

    protected void scroll2Scale(float scale) {
        mCurrentScale = Math.round(scale);
        scrollTo(0, scale2ScrollXY(mCurrentScale));
    }

    /**
     * 将移动过程中经过的刻度显示出来
     *
     * @param x
     * @param y
     */
    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (isVerticalRuler(flag)) {
            if (y < mMinPosition || y > mMaxPosition) {
                y = mMinPosition;
            }
            if (y != getScrollY()) {
                super.scrollTo(x, y);
            }
            mCurrentScale = scrollXY2Scale(y);
        } else {
            if (x < mMinPosition || x > mMaxPosition) {
                x = mMinPosition;
            }
            if (x != getScrollX()) {
                super.scrollTo(x, y);
            }
            mCurrentScale = scrollXY2Scale(x);
        }
        if (mRulerCallbacks != null) {
            for (RulerCallback item :
                    mRulerCallbacks) {
                item.onScaleChanging(Math.round(mCurrentScale));
            }
        }
    }

    /**
     * 将滚动位置转化为刻度
     *
     * @param scrollXY
     * @return
     */
    private float scrollXY2Scale(int scrollXY) {
        return ((float) (scrollXY - mMinPosition) / mLength) * mMaxLength + mParent.getMinScale();
    }

    /**
     * 将刻度转化为移动的位置
     *
     * @param scale
     * @return
     */
    private int scale2ScrollXY(float scale) {
        return (int) ((scale - mParent.getMinScale()) / mMaxLength * mLength + mMinPosition);
    }
}
