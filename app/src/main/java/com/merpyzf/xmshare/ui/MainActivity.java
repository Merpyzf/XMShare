package com.merpyzf.xmshare.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.ui.activity.ReceiveActivity;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;
import com.merpyzf.xmshare.ui.activity.SettingActivity;
import com.merpyzf.xmshare.util.SharedPreUtils;

import butterknife.BindView;

/**
 * @author wangke
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_start_server)
    Button btnStartServer;
    @BindView(R.id.btn_receive)
    Button btnReceive;
    @BindView(R.id.btn_send)
    Button btnStarSc;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.edt_nickname)
    EditText edtNickName;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initWidget(Bundle savedInstanceState) {
    }

    @Override
    public void initEvents() {
        btnStartServer.setOnClickListener(this);
        btnReceive.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnStarSc.setOnClickListener(this);
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_item_setting:
                    SettingActivity.start(mContext, SettingActivity.class);
                    break;
                case R.id.menu_item_about:
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    @Override
    protected void initToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("小马快传");
    }

    @Override
    protected void initData() {
        setNickName();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_server:
                break;
            case R.id.btn_receive:
                ReceiveActivity.start(mContext);
                break;
            case R.id.btn_send:
                SelectFilesActivity.start(mContext, SelectFilesActivity.class);
                break;
            case R.id.btn_save:
                // TODO: 2018/1/11 在应用开启并且没有手动设置设备昵称时，获取设备的设备名作为设备的昵称
                String nickName = edtNickName.getText().toString().trim();
                if (!"".equals(nickName)) {
                    SharedPreUtils.putString(mContext, Const.SP_USER, "nickName", nickName);
                } else {
                    Toast.makeText(mContext, "昵称不能为空！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 设置昵称
     */
    private void setNickName() {
        String nickName = SharedPreUtils.getString(mContext, Const.SP_USER, "nickName", "");
        if (!"".equals(nickName)) {
            edtNickName.setText(nickName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

