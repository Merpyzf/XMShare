package com.merpyzf.xmshare.ui.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.util.SingleThreadPool;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestApActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ap);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.btn_start)
    public void clickStart(View view) {

        for (int i = 0; i < 100; i++) {

            Runnable customRunable = () -> {
                while (true) {

                    Log.i("wk", Thread.currentThread().getName());

                }
            };

            SingleThreadPool.getSingleton().execute(customRunable);

        }


    }


    @OnClick(R.id.btn_shutdown)
    public void clickShutDown(View view) {

        SingleThreadPool.getSingleton().shutdownNow();

    }
}
