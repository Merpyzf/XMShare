package com.merpyzf.xmshare.ui.test;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.widget.SelectIndicatorView;
import com.merpyzf.xmshare.ui.widget.bean.Indicator;

public class TestFileIndicatorActivity extends AppCompatActivity {

    private SelectIndicatorView mSelectIndicatorView;
    private Button mBtnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_file_indicator);
        mBtnAdd = findViewById(R.id.btn_add);

        String path = Environment.getExternalStorageDirectory().getPath();

        Log.i("wk", path);

        mSelectIndicatorView = findViewById(R.id.fileSelectIndicator);


        mSelectIndicatorView.addIndicator(new Indicator("内部文件存储", Environment.getExternalStorageDirectory().getParent()));

        final int[] num = {2};

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mSelectIndicatorView.addIndicator(new Indicator("wangke"+(num[0]++), "wangke"));


            }
        });

    //
        //    mSelectIndicatorView.setIndicatorClickCallBack((currentPath, isBack) -> {
        //
        //        Log.i("wk", "当前点击的路径 -> "+currentPath);
        //
        //
        //        if(isBack){
        //
        //            Log.i("wk", "返回到上一个界面");
        //
        //        }
        //
        //
        //
        //
        //});
    }


}
