package com.merpyzf.xmshare.ui.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.test.adapter.BaseHeaderAdapter;
import com.merpyzf.xmshare.ui.test.entity.PinnedHeaderEntity;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;
import com.oushangfeng.pinnedsectionitemdecoration.callback.OnHeaderClickListener;

import java.util.ArrayList;
import java.util.List;

public class PinnedActivity extends AppCompatActivity {

    private List<PinnedHeaderEntity<PicFile>> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned);
        RecyclerView recyclerView = findViewById(R.id.rv);

        initData();


        BaseHeaderAdapter<PinnedHeaderEntity<PicFile>> adapter = new BaseHeaderAdapter<PinnedHeaderEntity<PicFile>>(mDatas) {
            @Override
            protected void convert(BaseViewHolder helper, PinnedHeaderEntity<PicFile> item) {
                switch (helper.getItemViewType()) {
                    case BaseHeaderAdapter.TYPE_DATA:
                        helper.setText(R.id.tv_title, item.getData().getName() + "");
                        break;
                    case BaseHeaderAdapter.TYPE_HEADER:
                        helper.setText(R.id.tv_selction_head, item.getPinnedHeaderName());
                        break;
                    default:
                        break;
                }


            }

            @Override
            protected void addItemTypes() {

                addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_rv_music);
                addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_selction_head);

            }
        };
        recyclerView.
                addItemDecoration(new PinnedHeaderItemDecoration.Builder(BaseHeaderAdapter.TYPE_HEADER)
                        .setDividerId(R.drawable.divider)
                        .enableDivider(false)   // 通过传入包括标签和其内部的子控件的ID设置其对应的点击事件
                        .setClickIds(R.id.checkBox)
                        // 是否关闭标签点击事件，默认开启
                        .disableHeaderClick(false)
                .setHeaderClickListener(new OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View view, int id, int position) {

                        Log.i("WW2k", String.valueOf(view instanceof CheckBox));

                    }

                    @Override
                    public void onHeaderLongClick(View view, int id, int position) {

                    }

                    @Override
                    public void onHeaderDoubleClick(View view, int id, int position) {

                    }


                }).create());

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


    }

    private void initData() {
        mDatas = new ArrayList<>();
        mDatas.add(new PinnedHeaderEntity<PicFile>(null, BaseHeaderAdapter.TYPE_HEADER, "今天"));
        for (int i = 0; i < 10; i++) {
            PicFile picFile = new PicFile("今天拍的照片: " + i, "", 0, 0);
            picFile.setLastChanged("今天");
            mDatas.add(new PinnedHeaderEntity<PicFile>(picFile, BaseHeaderAdapter.TYPE_DATA,picFile.getLastChanged()));
        }

        mDatas.add(new PinnedHeaderEntity<PicFile>(null, BaseHeaderAdapter.TYPE_HEADER, "昨天"));
        for (int i = 0; i < 6; i++) {
            PicFile picFile = new PicFile("昨天拍的照片: " + i, "", 0, 0);
            picFile.setLastChanged("昨天");
            mDatas.add(new PinnedHeaderEntity<PicFile>(picFile, BaseHeaderAdapter.TYPE_DATA,picFile.getLastChanged()));
        }


        mDatas.add(new PinnedHeaderEntity<PicFile>(null, BaseHeaderAdapter.TYPE_HEADER, "前天"));
        for (int i = 0; i < 3; i++) {
            PicFile picFile = new PicFile("前天拍的照片: " + i, "", 0, 0);
            picFile.setLastChanged("前天");
            mDatas.add(new PinnedHeaderEntity<PicFile>(picFile, BaseHeaderAdapter.TYPE_DATA,picFile.getLastChanged()));
        }



    }
}
