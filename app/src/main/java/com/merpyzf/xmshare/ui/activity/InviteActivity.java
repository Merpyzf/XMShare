package com.merpyzf.xmshare.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.util.ApkUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 应用邀请安装界面
 * <p>
 *
 * @author wangke
 */
public class InviteActivity extends BaseActivity {


    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    private static final String TAG = InviteActivity.class.getSimpleName();


    public static void start(Context context) {
        context.startActivity(new Intent(context, InviteActivity.class));
    }


    @Override
    public int getContentLayoutId() {
        return R.layout.activity_invite;
    }

    @Override
    public void doCreateView(Bundle savedInstanceState) {

    }

    @Override
    protected void initToolBar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("邀请安装");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void doCreateEvent() {

    }


    /**
     * 通过蓝牙的方式分享
     *
     * @param view
     */
    @OnClick(R.id.btn_bluetooth)
    public void clickBluetoothInvite(View view) {

        String apkFilePath = ApkUtils.getApkFilePath(this, getPackageName());
        if (apkFilePath == null) {
            Toast.makeText(mContext, "获取应用安装包失败!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 通过蓝牙的方式发送本应的apk给对端
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*"); //
        intent.setClassName("com.android.bluetooth"
                , "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkFilePath)));


        // 在发出Intent之前必须通过resolveActivity检查，避免找到不适合调用的组件,造成ActivityNotFoundExpection的异常
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {

            try {

                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }


        }


        startActivity(intent);

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
