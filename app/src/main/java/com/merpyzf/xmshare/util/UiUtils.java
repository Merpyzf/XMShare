package com.merpyzf.xmshare.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.util.timer.OSTimer;
import com.merpyzf.xmshare.R;

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

    public static ColorStateList getColorStateListTest(Context context, int colorRes) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };
        int color = ContextCompat.getColor(context, colorRes);
        int[] colors = new int[]{color, color, color, color};
        return new ColorStateList(states, colors);
    }

    /**
     * 根据不同的文件类型获取占位图片
     *
     * @param fileInfoType
     * @return
     */
    public static int getPlaceHolder(int fileInfoType) {
        int placeHolder = 0;
        switch (fileInfoType) {
            case FileInfo.FILE_TYPE_APP:
                placeHolder = R.drawable.ic_default_app;
                break;
            case FileInfo.FILE_TYPE_MUSIC:
                placeHolder = R.drawable.ic_default_album_art;
                break;
            case FileInfo.FILE_TYPE_VIDEO:
                placeHolder = R.drawable.ic_default_video;
                break;
            case FileInfo.FILE_TYPE_IMAGE:
                placeHolder = R.drawable.ic_default_image;
                break;
            default:
                break;
        }
        return placeHolder;
    }

}


