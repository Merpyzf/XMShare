package com.merpyzf.xmshare.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.design.widget.FloatingActionButton;


import com.merpyzf.xmshare.R;

import net.qiujuer.genius.ui.widget.FloatActionButton;


/**
 * 动画工具类
 */
public class AnimationUtils {

    private AnimationUtils() {
        throw new UnsupportedOperationException("不能被实例化");
    }

    /**
     * 创建动画层
     *
     * @param activity
     * @return
     */
    public static ViewGroup createAnimLayout(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    /**
     * 添加任务动画
     *
     * @param activity
     * @param startView  起始view
     * @param targetView 目标view
     */
    public static void setAddTaskAnimation(Activity activity, View startView, View targetView, final AddTaskAnimationListener listener) {
        //1.创建遮罩动画层
        ViewGroup animMaskLayout = createAnimLayout(activity);
        final ImageView imageView = new ImageView(activity);
        animMaskLayout.addView(imageView);

        //2.创建Animation
        int[] startLocArray = new int[2];
        int[] endLocArray = new int[2];
        startView.getLocationInWindow(startLocArray);
        targetView.getLocationInWindow(endLocArray);

        //3.设置遮罩层ImageView的LayoutParams
        ViewGroup.LayoutParams startViewLayoutParams = startView.getLayoutParams();
        ViewGroup.LayoutParams targetViewLayoutParams = targetView.getLayoutParams();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                startViewLayoutParams.width,
                startViewLayoutParams.height);
        lp.leftMargin = startLocArray[0];
        lp.topMargin = startLocArray[1];
        imageView.setLayoutParams(lp);
        //设置遮罩层ImageView的背景
        if (startView != null && (startView instanceof ImageView)) {
            ImageView iv = (ImageView) startView;
            imageView.setImageDrawable(iv.getDrawable() == null ? null : iv.getDrawable());
        }

        // 计算位移
        int xOffset = endLocArray[0] - startLocArray[0] + targetViewLayoutParams.width / 2;// 动画位移的X坐标
        int yOffset = endLocArray[1] - startLocArray[1] + targetViewLayoutParams.height / 2;// 动画位移的y坐标
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                xOffset, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                0, yOffset);
        translateAnimationY.setInterpolator(new LinearInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.2f, 1.0f, 0.2f);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);// 动画的执行时间
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (listener != null) {
                    listener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(set);
    }

    public interface AddTaskAnimationListener {

        void onAnimationStart(Animation animation);

        void onAnimationEnd(Animation animation);
    }

    /**
     * 放大封面图
     *
     * @param view
     * @param duration
     */
    public static void zoomInCover(View view, long duration) {
        ObjectAnimator scanY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1f);
        ObjectAnimator scanX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scanX, scanY);
        animatorSet.setDuration(duration);
        animatorSet.start();
    }

    /**
     * 缩小封面图
     *
     * @param view
     * @param duration
     */
    public static void zoomOutCover(View view, long duration) {
        ObjectAnimator scanY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f);
        ObjectAnimator scanX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.8f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scanX, scanY);
        animatorSet.setDuration(duration);
        animatorSet.start();
    }


    public static void showFabClearAll(Context context, FloatingActionButton fab, int color) {
        // 隐藏发送按钮，显示清除全部的按钮
        // 当动画未执行结束，按钮不可点击
        fab.setClickable(false);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab, "scaleY", 1f, 0f);
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(150);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab.setBackgroundTintList(UiUtils.getColorStateListTest(context, color));
                fab.setImageResource(R.drawable.ic_clear_all);
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab, "scaleX", 0f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab, "scaleY", 0f, 1f);
                animatorSet.play(scaleX).with(scaleY);
                animatorSet.setInterpolator(new OvershootInterpolator());
                animatorSet.setDuration(150);
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fab.setClickable(true);
                    }
                });
            }
        });
    }

    public static void showFabSend(Context context, FloatingActionButton fab) {
        // 隐藏发送按钮，显示清除全部的按钮
        // 当动画未执行结束，按钮不可点击
        fab.setClickable(false);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab, "scaleY", 1f, 0f);
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(150);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab.setBackgroundTintList(UiUtils.getColorStateListTest(context, R.color.colorAccent));
                fab.setImageResource(R.drawable.ic_send);
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab, "scaleX", 0f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab, "scaleY", 0f, 1f);
                animatorSet.play(scaleX).with(scaleY);
                animatorSet.setInterpolator(new OvershootInterpolator());
                animatorSet.setDuration(150);
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fab.setClickable(true);
                    }
                });
            }
        });

    }


}
