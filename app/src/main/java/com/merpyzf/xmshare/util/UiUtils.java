package com.merpyzf.xmshare.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.xmshare.R;

import net.qiujuer.genius.ui.animation.AnimatorListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
     * @param view      要隐藏的View
     * @param delayTime 延时毫秒
     */
    @SuppressLint("CheckResult")
    public static void delayHideView(View view, int delayTime) {
        // TODO: 2018/12/8 RxJava实现延时效果
        Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
                    animator.setDuration(1000);
                    animator.addListener(new AnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation, boolean isReverse) {
                            if (view == null) {
                                return;
                            }
                            view.setVisibility(View.INVISIBLE);
                        }
                    });
                    animator.start();
                });
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
            case BaseFileInfo.FILE_TYPE_APP:
                placeHolder = R.drawable.ic_holder_app;
                break;
            case BaseFileInfo.FILE_TYPE_MUSIC:
                placeHolder = R.drawable.ic_holder_album_art;
                break;
            case BaseFileInfo.FILE_TYPE_VIDEO:
                placeHolder = R.drawable.ic_holder_video;
                break;
            case BaseFileInfo.FILE_TYPE_IMAGE:
                placeHolder = R.drawable.ic_holder_image;
                break;
            default:
                break;
        }
        return placeHolder;
    }

    public static int[] getRecyclerViewLastPosition(LinearLayoutManager layoutManager) {
        int lastPosition = 0;
        int lastOffset = 0;
        int[] pos = new int[2];
        //获取可视的第一个view
        View topView = layoutManager.getChildAt(0);
        //获取与该view的顶部的偏移量
        lastOffset = topView.getTop();
        //得到该View的数组位置
        lastPosition = layoutManager.getPosition(topView);
        pos[0] = lastPosition;
        pos[1] = lastOffset;
        return pos;
    }


}


