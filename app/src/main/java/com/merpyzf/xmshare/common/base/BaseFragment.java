package com.merpyzf.xmshare.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by wangke on 2017/11/20.
 */

public abstract class BaseFragment extends RxFragment {

    protected View mRootView;
    /**
     * 数据是否已加载完成
     */
    private Unbinder mRootUnbinder;
    protected Activity mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        initArgs(arguments);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("WW2K", "onCreateView执行了");
        mContext = getActivity();
        if (null == mRootView) {
            View root = inflater.inflate(getContentLayoutId(), container, false);
            mRootUnbinder = ButterKnife.bind(this, root);
            mRootView = root;
        }
        initWidget(mRootView);
        initEvent();
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 当View创建完成之后初始化数据
        initData();
    }

    /**
     * 初始化相关参数
     *
     * @param bundle
     */
    protected void initArgs(Bundle bundle) {

    }

    /**
     * 得到当前界面资源文件的Id
     *
     * @return 资源文件Id
     */
    protected abstract int getContentLayoutId();


    /**
     * 初始化控件
     *
     * @param rootView 根View
     */
    protected void initWidget(View rootView) {

    }

    /**
     * 加载数据
     */
    protected void initData() {
    }


    /**
     * 初始化事件
     */
    protected void initEvent() {

    }

    @Override
    public void onDestroyView() {
        Log.i("WW2k", "onDestoryView执行了");
        ViewGroup parentView = (ViewGroup) mRootView.getParent();
        if (null != parentView) {
            parentView.removeView(mRootView);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mRootUnbinder.unbind();
        super.onDestroy();
    }
}
