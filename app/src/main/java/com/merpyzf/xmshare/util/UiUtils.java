package com.merpyzf.xmshare.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;

import com.merpyzf.transfermanager.util.timer.OSTimer;

/**
 * Created by merpyzf on 2018/4/15.
 * 与界面显示相关的工具类
 */

public class UiUtils {

    private static final int CLICK_INTERVAL = 1000;
    private static long lastClickTime = 0;

    /**
     * 延时隐藏View并带有渐变得动画
     *
     * @param context   上下文
     * @param view      要隐藏的View
     * @param delayTime 延时毫秒
     */
    public static void delayHideView(Activity context, View view, int delayTime) {

        //时间3s
        OSTimer mHideTipTimer = new OSTimer(null, () -> {
            context.runOnUiThread(() -> {

                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
                animator.setDuration(1000);//时间1s
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        if (view == null) {
                            return;
                        }
                        view.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            });

        }, delayTime, false);

        mHideTipTimer.start();

    }

    /**
     * View点击的防抖动
     *
     * @return
     */
    public static boolean clickValid() {
        boolean clickable = false;
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime >= CLICK_INTERVAL) {
            clickable = true;
        } else {
            clickable = false;
        }
        lastClickTime = currentClickTime;
        return clickable;
    }

}
