package com.merpyzf.xmshare.ui.fragment;


import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.ui.widget.tools.CustomRecyclerScrollViewListener;

/**
 * @author wangke
 */
public class MainFragment extends BaseFragment {
    private CustomRecyclerScrollViewListener mScrollListener;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void doCreateView(View rootView) {
        super.doCreateView(rootView);
        FunctionListFragment functionListFragment = new FunctionListFragment();
        functionListFragment.setScrollListener(mScrollListener);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_main_container, functionListFragment);
        transaction.commit();
    }


    public void setScrollListener(CustomRecyclerScrollViewListener scrollListener) {
        this.mScrollListener = scrollListener;
    }
}
