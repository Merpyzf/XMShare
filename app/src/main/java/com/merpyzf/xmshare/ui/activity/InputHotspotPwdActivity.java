package com.merpyzf.xmshare.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.merpyzf.qrcodescan.google.activity.CaptureActivity;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;


public class InputHotspotPwdActivity extends BaseActivity {

    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    @BindView(R.id.edt_hotspot_pwd)
    EditText mEdtHotspotPwd;
    @BindView(R.id.btn_conn)
    Button mBtnConn;

    public static final int RESULT_GET_HOTSPOT_INFO = 0x001;


    private String mConnSsid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_input_hotspot_pwd;
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        mConnSsid = getIntent().getStringExtra("ssid");
        mTvTip.setText(mConnSsid + "由Android8.0及以上的设备创建，因系统限制无法直接为您建立连接，请尝试下面的方法。");
        mBtnConn.setEnabled(false);

    }

    @Override
    protected void initToolBar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("设备连接");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initEvents() {

        mEdtHotspotPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() >= 8) {
                    mBtnConn.setEnabled(true);

                } else {
                    mBtnConn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick(R.id.btn_open_qrscan)
    public void clickOpenQrScan(View view) {

        Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
        startActivityForResult(intent, 0);

    }

    @OnClick(R.id.btn_conn)
    public void clickConn(View view) {

        String hotspotPwd = mEdtHotspotPwd.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ssid", mConnSsid);
            jsonObject.put("preSharedKey", hotspotPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String hotspotInfo = jsonObject.toString();
        Intent intent = new Intent();
        intent.putExtra("hotspot_info", hotspotInfo);
        InputHotspotPwdActivity.this.setResult(RESULT_GET_HOTSPOT_INFO, intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == CaptureActivity.RESULT_CODE_QR_SCAN) {

            String hotspotInfo = data.getStringExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);


            try {
                JSONObject jsonObject = new JSONObject(hotspotInfo);
                jsonObject.get("ssid");
                jsonObject.get("preSharedKey");


            } catch (JSONException e) {
                e.printStackTrace();

                ToastUtils.showShort(getBaseContext(), "没试别到有效的字符，请扫描本应用内的二维码");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("hotspot_info", hotspotInfo);
            InputHotspotPwdActivity.this.setResult(RESULT_GET_HOTSPOT_INFO, intent);
            finish();
        }


    }


}
