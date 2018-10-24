package com.merpyzf.xmshare.ui.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.Section;
import com.merpyzf.xmshare.ui.test.adapter.TestSelctionAdapter;
import com.merpyzf.xmshare.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class TestSelctionRvActivity extends AppCompatActivity {
    private RecyclerView mRvSection;
    private List<Section> mDatas;
    private TestSelctionAdapter mSelctionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_selction_rv);
        initData();
        initUI();
        initEvent();

    }

    private void initEvent() {
        mSelctionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showShort(TestSelctionRvActivity.this, "被点击了");

                Section section = (Section) adapter.getItem(position);
                if (section.isHeader) {
                    section.setCheckedAllChild(!section.isCheckedAllChild());
                    String lastChanged = section.header;
                    if (section.isCheckedAllChild()) {
                        for (Section mData : mDatas) {
                            if (!mData.isHeader) {
                                if (mData.t.getLastModified().equals(lastChanged)) {
                                    App.addSendFile(mData.t);
                                }
                            }
                        }
                    } else {
                        for (Section mData : mDatas) {
                            if (!mData.isHeader) {
                                if (mData.t.getLastModified().equals(lastChanged)) {
                                    App.removeSendFile(mData.t);
                                }
                            }
                        }

                    }

                }

                mSelctionAdapter.notifyDataSetChanged();

            }
        });

        mSelctionAdapter.setOnItemClickListener((adapter, view, position) -> {
            Section section = (Section) adapter.getItem(position);
            if (!section.isHeader) {
                if(App.getSendFileList().contains(section.t)){

                    App.removeSendFile(section.t);
                }else {
                    App.addSendFile(section.t);
                }


                mSelctionAdapter.notifyItemChanged(position);
                // 检查并更新head的选中状态
                String headName = section.t.getLastModified();
                for (Section mData : mDatas) {
                    if (!mData.isHeader) {
                        if (headName.equals(mData.t.getLastModified()) && !App.getSendFileList().contains(mData.t)) {
                            for (Section data : mDatas) {
                                if (data.isHeader && data.header.equals(headName)) {
                                    data.setCheckedAllChild(false);
                                    int pos = mDatas.indexOf(data);
                                    mSelctionAdapter.notifyItemChanged(pos);
                                    return;
                                }
                            }
                        }
                    }

                }

                for (Section mData : mDatas) {
                    if (mData.isHeader && mData.header.equals(headName)) {
                        mData.setCheckedAllChild(true);
                        int pos = mDatas.indexOf(mData);
                        mSelctionAdapter.notifyItemChanged(pos);
                        break;
                    }
                }


            }
        });


    }

    private void initData() {

        mDatas = new ArrayList<>();
        mDatas.add(new Section(true, "今天"));
        for (int i = 0; i < 3; i++) {
            PicFile picFile = new PicFile("今天拍的照片: " + i, "", 0, 0);
            picFile.setLastModified("今天");
            mDatas.add(new Section(picFile));
        }

        mDatas.add(new Section(true, "昨天"));
        for (int i = 0; i < 5; i++) {
            PicFile picFile = new PicFile("昨天拍的照片: " + i, "", 0, 0);
            picFile.setLastModified("昨天");
            mDatas.add(new Section(picFile));
        }

        mDatas.add(new Section(true, "前天"));
        for (int i = 0; i < 6; i++) {
            PicFile picFile = new PicFile("前天拍的照片: " + i, "", 0, 0);
            picFile.setLastModified("前天");
            mDatas.add(new Section(picFile));
        }
        mDatas.add(new Section(true, "2018.10.20"));
        for (int i = 0; i < 6; i++) {
            PicFile picFile = new PicFile("2018.10.20拍的照片: " + i, "", 0, 0);
            picFile.setLastModified("2018.10.20");
            mDatas.add(new Section(picFile));
        }



    }

    private void initUI() {
        mRvSection = findViewById(R.id.rv_section);
        mRvSection.getItemAnimator().setChangeDuration(0);
        mRvSection.setLayoutManager(new LinearLayoutManager(this));
        mSelctionAdapter = new TestSelctionAdapter(R.layout.item_rv_music, R.layout.item_selction_head, mDatas);
        mRvSection.setAdapter(mSelctionAdapter);


    }
}
