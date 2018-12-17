package com.merpyzf.xmshare.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.merpyzf.xmshare.R;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Activity基类
 *
 * @author wangke
 * @date 2017/11/20
 */

public abstract class BaseActivity extends RxAppCompatActivity {
    private Unbinder mUnbinder;
    protected Activity mContext;

    public static void start(Context context, Class activity) {
        context.startActivity(new Intent(context, activity));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());
        mContext = this;
        mUnbinder = ButterKnife.bind(this);
        // 设置系统顶部菜单栏为沉浸样式
        initSystemBarTint();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initData();
        doCreateView(savedInstanceState);
        initToolBar();
        doCreateEvent();

    }

    /**
     * 获取Layout id
     *
     * @return
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 初始化Views
     *
     * @param savedInstanceState
     */
    protected abstract void doCreateView(Bundle savedInstanceState);

    /**
     * 初始化ToolBar
     */
    protected void initToolBar() {

    }

    /**
     * 控件事件的初始化
     */
    protected abstract void doCreateEvent();

    /**
     * 初始化RecyclerView
     */
    protected void initRecyclerView() {
    }

    /**
     * 显示ProgressBar
     */
    protected void showProgressBar() {
    }

    /**
     * 隐藏ProgressBar
     */
    protected void hideProgressBar() {
    }

    /**
     * 加载数据
     */
    protected void loadData() {
    }

    /**
     * 加载完毕后设置数据显示
     */
    protected void finishLoadTask() {
    }

    /**
     * 子类可以重写决定是否使用透明状态栏
     *
     * @return
     */
    protected boolean translucentStatusBar() {
        return false;
    }

    /**
     * 子类重写此方法可以设置主题色
     *
     * @return
     */
    protected int setStatusBarColor() {
        return getColorPrimary();
    }

    /**
     * 获取主题色
     */
    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 设置系统状态栏颜色
     */
    protected void initSystemBarTint() {
        Window window = getWindow();
        if (translucentStatusBar()) {
            // 设置状态栏全透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                return;
            }
        }
        // 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上使用原生方法
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(setStatusBarColor());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 仅支持5.0以上的设备
            return;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getApplicationContext()).clearMemory();
        Glide.get(getApplicationContext()).trimMemory(TRIM_MEMORY_COMPLETE);
        System.gc();
    }

    @Override
    protected void onDestroy() {
        // 解除绑定
        mUnbinder.unbind();
        super.onDestroy();
    }
}








