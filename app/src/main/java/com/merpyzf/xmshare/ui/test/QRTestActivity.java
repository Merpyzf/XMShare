package com.merpyzf.xmshare.ui.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.merpyzf.qrcodescan.google.activity.CaptureActivity;
import com.merpyzf.xmshare.R;

public class QRTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrtest);


        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CaptureActivity.class));
                Log.i("wk", "被点击");
            }
        });
    }


}
