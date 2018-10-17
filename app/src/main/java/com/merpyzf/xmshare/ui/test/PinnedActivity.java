package com.merpyzf.xmshare.ui.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.test.adapter.BaseHeaderAdapter;
import com.merpyzf.xmshare.ui.test.entity.PinnedHeaderEntity;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;
import com.oushangfeng.pinnedsectionitemdecoration.callback.OnHeaderClickListener;

import java.util.ArrayList;
import java.util.List;

public class PinnedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned);
        RecyclerView recyclerView = findViewById(R.id.rv);
        List<PinnedHeaderEntity<Integer>> data = new ArrayList<>();

        data.add(new PinnedHeaderEntity<>(0, BaseHeaderAdapter.TYPE_HEADER, "dog"));

        for (int i = 0; i < 10; i++) {

            data.add(new PinnedHeaderEntity<>(i, BaseHeaderAdapter.TYPE_DATA, "dog"));

        }


        data.add(new PinnedHeaderEntity<>(1, BaseHeaderAdapter.TYPE_HEADER, "cat"));

        for (int i = 0; i < 10; i++) {
            data.add(new PinnedHeaderEntity<>(i, BaseHeaderAdapter.TYPE_DATA, "cat"));
        }


        BaseHeaderAdapter<PinnedHeaderEntity<Integer>> adapter = new BaseHeaderAdapter<PinnedHeaderEntity<Integer>>(data) {
            @Override
            protected void convert(BaseViewHolder helper, PinnedHeaderEntity<Integer> item) {
                switch (helper.getItemViewType()) {
                    case BaseHeaderAdapter.TYPE_DATA:
                        helper.setText(R.id.tv_pos, item.getData() + "");
                        break;
                    case BaseHeaderAdapter.TYPE_HEADER:
                        helper.setText(R.id.tv_pinner_title, item.getPinnedHeaderName());
                        break;
                    default:
                        break;
                }


            }

            @Override
            protected void addItemTypes() {

                addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_data);
                addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_pinned_header);

            }
        };
        recyclerView.addItemDecoration(new PinnedHeaderItemDecoration.Builder(BaseHeaderAdapter.TYPE_HEADER).setDividerId(R.drawable.divider).enableDivider(true)
                .setHeaderClickListener(new OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View view, int id, int position) {

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
}
