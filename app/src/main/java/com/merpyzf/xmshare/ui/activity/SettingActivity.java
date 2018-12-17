package com.merpyzf.xmshare.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.common.utils.ToastUtils;

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
    @BindView(R.id.switch_transfer_mode)
    Switch mSwitchTransferMode;
    @BindView(R.id.switch_show_hidden)
    Switch mSwitchShowHidden;
    @BindView(R.id.switch_close_curr_page)
    Switch mSwitchErrorIsCloseCurrPage;
    @BindView(R.id.rl_trello)
    RelativeLayout mRlTrello;
    private static final String TAG = SendActivity.class.getSimpleName();


    @Override
    public int getContentLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void doCreateView(Bundle savedInstanceState) {
        int transferMode = PersonalSettingUtils.getTransferMode(mContext);
        if (transferMode == PersonalSettingUtils.TRANSFER_MODE_LAN) {
            mSwitchTransferMode.setChecked(false);
        } else if (transferMode == PersonalSettingUtils.TRANSFER_MODE_AP) {
            mSwitchTransferMode.setChecked(true);
        }
        mSwitchShowHidden.setChecked(PersonalSettingUtils.getIsShowHiddenFile(mContext));

        int pageCloseMode = PersonalSettingUtils.getIsCloseCurrPageWhenError(mContext);
        if (pageCloseMode == PersonalSettingUtils.CLOSE_CURRENT_PAGE_WHEN_ERROR) {
            mSwitchErrorIsCloseCurrPage.setChecked(true);
        } else {
            mSwitchErrorIsCloseCurrPage.setChecked(false);
        }


    }

    @Override
    public void doCreateEvent() {
        //设置传输文件时优先使用的模式
        mSwitchTransferMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                PersonalSettingUtils.updateTransferMode(mContext, PersonalSettingUtils.TRANSFER_MODE_AP);
            } else {
                PersonalSettingUtils.updateTransferMode(mContext, PersonalSettingUtils.TRANSFER_MODE_LAN);
            }
        });
        // 设置是否显示隐藏文件和目录
        mSwitchShowHidden.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                PersonalSettingUtils.updateIsShowHiddenFile(mContext, PersonalSettingUtils.SHOW_HIDDEN_FILE);
            } else {
                PersonalSettingUtils.updateIsShowHiddenFile(mContext, PersonalSettingUtils.DONT_SHOW_HIDDEN_FILE);
            }
        });
        // 设置在传输过程中程序出错或认为的退出是否直接关闭当前所在的传输页面
        mSwitchErrorIsCloseCurrPage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                PersonalSettingUtils.updateIsCloseCurrPageWhenError(mContext, PersonalSettingUtils.CLOSE_CURRENT_PAGE_WHEN_ERROR);
            } else {
                PersonalSettingUtils.updateIsCloseCurrPageWhenError(mContext, PersonalSettingUtils.DONT_CLOSE_CURRENT_PAGE_WHEN_ERROR);

            }
        });

        // 查看本项目在trello上的最新开发计划
        mRlTrello.setOnClickListener(v -> {
            String trelloUrl = "https://trello.com/b/LnZuAbAs/xmshare";
            Intent intent = new Intent();
            intent.setData(Uri.parse(trelloUrl));
            intent.setAction(Intent.ACTION_VIEW);
            this.startActivity(intent);
        });


    }

    @Override
    protected void initToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.rl_clear_cache)
    public void clickClearCache(View view) {
        // 清理Glide查看图片时留下的缓存
        ToastUtils.showShort(mContext, "正在为您进行缓存清理...");
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
