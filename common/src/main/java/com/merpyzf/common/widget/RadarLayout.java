package com.merpyzf.common.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangke
 * @date 2017/9/22
 * 雷达圆心扩散
 */

public class RadarLayout extends FrameLayout {

    private static final int DEFAULT_DURATION_TIME = 3000;
    private static final int DEFAULT_RADAR_COLOR = Color.WHITE;
    private static final int DEFAULT_RADAR_COUNT = 4;
    private static final int DEFAULT_RADAR_STROKE_WIDTH = 2;

    private float mCenterX;
    private float mCenterY;
    private float mRadius;
    private Paint mPaint = null;
    private int mRadarCount = DEFAULT_RADAR_COUNT;
    private int mRadarStrokeWidth = dip2px(DEFAULT_RADAR_STROKE_WIDTH);
    private int mDuration = DEFAULT_DURATION_TIME;
    private int mRadarColor = DEFAULT_RADAR_COLOR;
    private boolean mStyleIsFILL = true;
    private boolean mAnimIsStart = false;
    private AnimatorSet mAnimatorSet;

    public RadarLayout(@NonNull Context context) {
        this(context, null);

    }

    public RadarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams();
        addRadarView();
    }


    /**
     * 初始化相关参数
     */
    private void initParams() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(mStyleIsFILL == true ? Paint.Style.FILL : Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRadarStrokeWidth);
        mPaint.setColor(mRadarColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        // 确定圆的圆点坐标及半径
        mCenterX = width * 0.5f;
        mCenterY = height * 0.5f;
        mRadius = Math.min(width, height) * 0.5f;
    }

    private void addRadarView() {
        List animators = new ArrayList<>();
        for (int i = 0; i < mRadarCount; i++) {
            Radar radar = new Radar(getContext());
            radar.setScaleX(0);
            radar.setScaleY(0);
            radar.setAlpha(1);
            addView(radar, i);
            int delay = i * mDuration / mRadarCount;
            animators.add(create(radar, "scaleX", ObjectAnimator.INFINITE, delay, 0, 1));
            animators.add(create(radar, "scaleY", ObjectAnimator.INFINITE, delay, 0, 1));
            animators.add(create(radar, "alpha", ObjectAnimator.INFINITE, delay, 1, 0));
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.setDuration(mDuration);
        mAnimatorSet.addListener(new RadarAnimationListener());
        mAnimatorSet.start();
    }


    private ObjectAnimator create(View target, String propertyName, int repeatCount, long delay, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, propertyName, from, to);
        animator.setRepeatCount(repeatCount);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setStartDelay(delay);
        return animator;
    }


    /**
     * 开启动画
     */
    public synchronized void start() {
        if (mAnimatorSet != null && !mAnimIsStart) {
            mAnimatorSet.start();
            mAnimIsStart = true;
        }
    }


    /**
     * 关闭动画
     */
    public synchronized void stop() {
        if (mAnimatorSet != null && mAnimIsStart) {
            mAnimatorSet.end();
            mAnimIsStart = false;
        }
    }

    /**
     * 当前的动画是否已经开启
     *
     * @return
     */
    public boolean isStarted() {
        return (mAnimatorSet != null && mAnimIsStart);
    }

    /**
     * 根据手机分辨率从dp转成px
     *
     * @param dpValue
     * @return
     */
    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    class RadarAnimationListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {
            mAnimIsStart = true;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mAnimIsStart = false;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            mAnimIsStart = false;
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            mAnimIsStart = true;
        }
    }

    class Radar extends View {

        public Radar(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mPaint.setStyle(mStyleIsFILL == true ? Paint.Style.FILL : Paint.Style.STROKE);
            //绘制一个圆
            canvas.drawCircle(mCenterX, mCenterY, mRadius - mRadarStrokeWidth, mPaint);
        }

    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public void setRadarColor(int mRadarColor) {
        this.mRadarColor = mRadarColor;
    }

    public void setStyleIsFILL(boolean mStyleIsFILL) {
        this.mStyleIsFILL = mStyleIsFILL;
    }
}
