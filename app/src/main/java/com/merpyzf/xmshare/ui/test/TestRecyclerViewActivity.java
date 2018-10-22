package com.merpyzf.xmshare.ui.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.test.adapter.TestAdapter;

import java.util.ArrayList;
import java.util.List;

public class TestRecyclerViewActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<String> mDatas = new ArrayList<>();
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recycler_view);
        initData();
        initUI();



    }

    private void initData() {

         for(int i=1;i<=100;i++){

             mDatas.add("这是第"+i+"条数据");

         }

    }

    private void initUI() {
        mRecyclerView = findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TestAdapter testAdapter = new TestAdapter(this, mDatas);
        mRecyclerView.setAdapter(testAdapter);
    }
}
