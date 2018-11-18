package com.merpyzf.xmshare.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.widget.bean.Indicator;
import com.merpyzf.xmshare.util.DisplayUtils;
import com.merpyzf.xmshare.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangke on 2018/3/17.
 * <p>
 * 选择层级指示器
 */

public class SelectIndicatorView extends HorizontalScrollView implements FileSelectIndicator, View.OnClickListener {

    private Context mContext = null;
    private LinearLayout mRootView = null;
    private IndicatorChangedCallback mCallBack = null;
    private List<Indicator> mIndicatorList = null;
    private int mTextColor;


    public SelectIndicatorView(Context context) {
        this(context, null);
    }

    public SelectIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 在这里进行初始化的方法
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.fileSelectIndicator);
        // 字体颜色
        mTextColor = typedArray.getColor(R.styleable.fileSelectIndicator_textColor, getResources().getColor(R.color.colorPrimaryText));
        init();
    }

    private void init() {
        mIndicatorList = new ArrayList<>();
        mRootView = new LinearLayout(mContext);
        mRootView.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mRootView.setLayoutParams(layoutParams);
        addView(mRootView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mRootView != null) {
            int childCount = mRootView.getChildCount();
            if (childCount > 0) {
                View view = mRootView.getChildAt(childCount - 1);
                float x = view.getX();
                scrollTo((int) view.getX(), (int) view.getX());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void addIndicator(Indicator indicator) {
        if (mRootView != null && indicator != null) {
            View indicatorView = createIndicatorView(indicator);
            mRootView.addView(indicatorView);
        }
    }

    /**
     * 移除给定标签之后的所有标签
     *
     * @param indicator
     */
    @Override
    public void removeAfter(Indicator indicator) {
        if (mIndicatorList.contains(indicator)) {
            int start = mIndicatorList.indexOf(indicator) + 1;
            int end = mIndicatorList.size() - 1;
            if (start <= end) {
                for (int i = end; i >= start; i--) {
                    mIndicatorList.remove(i);
                    mRootView.removeViewAt(i);
                }
                if (mCallBack != null) {
                    mCallBack.onIndicatorChanged(mIndicatorList.get(mIndicatorList.size() - 1));
                }
            }
        }

        //  回调当前标签的值
    }

    @Override
    public void removeAfterByIndex(int index) {
        int start = index + 1;
        int end = mIndicatorList.size() - 1;
        if (start <= end) {
            for (int i = end; i >= start; i--) {
                mIndicatorList.remove(i);
                mRootView.removeViewAt(i);
            }
            if (mCallBack != null) {
                mCallBack.onIndicatorChanged(mIndicatorList.get(mIndicatorList.size() - 1));
            }
        }

        // 回调当前所在标签的值
    }
    @Override
    public void pop() {
        if (mIndicatorList.size() > 1) {
            int pos = mIndicatorList.size() - 1;
            mIndicatorList.remove(pos);
            mRootView.removeViewAt(pos);
            if (mCallBack != null) {
                mCallBack.onIndicatorChanged(mIndicatorList.get(mIndicatorList.size() - 1));
            }
        }
    }

    @Override
    public boolean isRoot() {
        if (mIndicatorList.size() == 1) {
            return true;
        }
        return false;
    }


    /**
     * 移除最后一个标签相当于返回
     */
    public boolean back() {
        String currentPath = null;
        int removeIndex = mIndicatorList.size() - 1;
        // 保留根标签不被移除
        if (removeIndex != 0) {
            mIndicatorList.remove(removeIndex);
            mRootView.removeViewAt(removeIndex);
            currentPath = mIndicatorList.get(mIndicatorList.size() - 1).getValue();
            return true;
        }
        return false;
    }


    @Override
    public View createIndicatorView(Indicator indicator) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        // 设置布局的样式
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        // 设置布局参数
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        TextView textView = new TextView(mContext);

        // 需要对路径的进行分割，区最末尾的哪个文件目录
        textView.setText(indicator.getName());
        linearLayout.addView(textView);
        LinearLayout.LayoutParams tvParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        tvParams.leftMargin = DisplayUtils.dip2px(mContext, 5);

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.ic_choice_file_folder_separator);
        linearLayout.addView(imageView);

        LinearLayout.LayoutParams ivParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        ivParams.height = DisplayUtils.dip2px(mContext, 40);
        imageView.setLayoutParams(ivParams);

        linearLayout.setTag(indicator);
        // 将添加进来的路径保存到集合中
        mIndicatorList.add(indicator);
        // 设置标签的点击事件
        linearLayout.setOnClickListener(this);
        return linearLayout;
    }


    /**
     * 指示器标签点击实事件的回调用
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int size = mIndicatorList.size();
        if (size <= 1) {
            return;
        }
        Indicator indicator = (Indicator) v.getTag();
        // 当前点击标签所在的位置
        int clickPos = mIndicatorList.indexOf(indicator);
        // 如果点击的是最后一个不需要执行移除的操作
        if (clickPos != size - 1) {
            removeAfterByIndex(clickPos);
            if (mCallBack != null) {
                mCallBack.onIndicatorChanged(indicator);
            }
        } else {
            ToastUtils.showShort(mContext, "不需要移除");
        }
    }

    public void setIndicatorClickCallBack(IndicatorChangedCallback mCallBack) {
        this.mCallBack = mCallBack;
    }
}


