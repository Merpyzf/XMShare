package com.merpyzf.xmshare.ui.fragment;


import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.ui.widget.SelectIndicatorView;
import com.merpyzf.xmshare.ui.widget.bean.Indicator;
import com.merpyzf.xmshare.ui.widget.tools.CustomRecyclerScrollViewListener;

import butterknife.BindView;


public class PhotoFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_checked)
    TextView mTvChecked;
    @BindView(R.id.checkbox_all)
    CheckBox mCheckbox;
    @BindView(R.id.fileSelectIndicator)
    SelectIndicatorView mSelectIndicator;
    private CustomRecyclerScrollViewListener mScrollListener;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_photo;
    }

    @Override
    protected void initWidget(View rootView) {
        super.initWidget(rootView);
        mSelectIndicator.addIndicator(new Indicator("相册", ""));
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        PhotoDirsFragment photoDirsFragment = new PhotoDirsFragment();
        photoDirsFragment.setScrollListener(mScrollListener);
        fragmentTransaction.replace(R.id.fl_container, photoDirsFragment);
        fragmentTransaction.commit();
        mCheckbox.setChecked(false);
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }

    public CheckBox getCheckbox() {
        return mCheckbox;
    }

    public TextView getTvChecked() {
        return mTvChecked;
    }

    public SelectIndicatorView getFileSelectIndicator() {
        return mSelectIndicator;
    }

    public void setScrollListener(CustomRecyclerScrollViewListener scrollListener) {
        this.mScrollListener = scrollListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
