package com.merpyzf.xmshare.ui.fragment;


import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseFragment;

public class MainFragment extends BaseFragment {

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initWidget(View rootView) {
        super.initWidget(rootView);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_main_container, new FunctionListFragment());
        transaction.commit();
    }
}
