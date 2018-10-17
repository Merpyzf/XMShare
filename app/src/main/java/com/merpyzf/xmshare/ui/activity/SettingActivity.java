package com.merpyzf.xmshare.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.util.SharedPreUtils;
import com.merpyzf.xmshare.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * 应用设置界面
 */
public class SettingActivity extends BaseActivity {


    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    // 设置建立局域网的方式
    @BindView(R.id.switch_transfer_mode)
    Switch mSwitchTransferMode;
    private static final String TAG = SendActivity.class.getSimpleName();


    @Override
    public int getContentLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initWidget(Bundle savedInstanceState) {

        int transferMode = SharedPreUtils.getInteger(mContext, Const.SP_USER, Const.KEY_TRANSFER_MODE, Const.TRANSFER_MODE_LAN);

        if (transferMode == Const.TRANSFER_MODE_LAN) {
            mSwitchTransferMode.setChecked(false);
        } else if (transferMode == Const.TRANSFER_MODE_AP) {
            mSwitchTransferMode.setChecked(true);
        }
    }

    @Override
    public void initEvents() {

        mSwitchTransferMode.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                // 热点
                SharedPreUtils.putInteger(mContext, Const.SP_USER, Const.KEY_TRANSFER_MODE,
                        Const.TRANSFER_MODE_AP);
                Log.i(TAG, "设置 AP模式");

            } else {
                // 局域网
                SharedPreUtils.putInteger(mContext, Const.SP_USER, Const.KEY_TRANSFER_MODE,
                        Const.TRANSFER_MODE_LAN);
                Log.i(TAG, "设置局域网模式");
            }
        });

    }

    @Override
    protected void initToolBar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.rl_clear_cache)
    public void clickClearCache(View view){

        // 清理Glide查看图片时留下的缓存

        ToastUtils.showShort(mContext,"正在为您进行缓存清理...");
        Observable.just("")
                .observeOn(Schedulers.io())
                .subscribe(s -> {

                    Glide.get(mContext)
                            .clearDiskCache();

                });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;

        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
