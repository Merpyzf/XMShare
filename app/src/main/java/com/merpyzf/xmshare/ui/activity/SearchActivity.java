package com.merpyzf.xmshare.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.util.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 用于进行文件搜索的Activity
 *
 * @author wangke
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.edt_search)
    EditText mEdtSearch;
    @BindView(R.id.rv_filelist)
    RecyclerView mRvFileList;
    private ArrayList<FileInfo> mFileList;

    @Override
    protected int getContentLayoutId() {
        initWindowTransition();
        return R.layout.activity_search;
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        mEdtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mEdtSearch.setInputType(EditorInfo.TYPE_CLASS_TEXT);
    }

    @Override
    protected void initEvents() {
        mIvBack.setOnClickListener(this);
        mEdtSearch.setOnEditorActionListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        mFileList = (ArrayList<FileInfo>) getIntent().getSerializableExtra("fileList");
        for (FileInfo fileInfo : mFileList) {
            //Log.i("W2k", "name --> "+fileInfo.getName());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
                break;
        }

    }

    /**
     * 初始化窗口进入，退出的动画效果
     */
    private void initWindowTransition() {
        getWindow().setEnterTransition(new Fade().setDuration(200));
        getWindow().setExitTransition(new Fade().setDuration(200));
    }

    /**
     * 搜索
     */
    private void search(String searchContent) {

        mEdtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            String searchContent = mEdtSearch.getText().toString();
            if (TextUtils.isEmpty(searchContent)) {
                ToastUtils.showShort(mContext, "请输入要查找的文件名称！");
            }else {
                search(searchContent);
            }
            return true;
        }
        return false;
    }
}
