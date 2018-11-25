package com.merpyzf.xmshare.ui;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * 闪屏页
 *
 * @author wangke
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initEvents() {
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                    SelectFilesActivity.start(mContext, SelectFilesActivity.class);
                    finish();
                });
    }
}
