package com.merpyzf.xmshare.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.widget.bean.Label;
import com.merpyzf.xmshare.util.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangke on 2018/3/17.
 * <p>
 * 文件选择指示器
 */

public class FileSelectIndicatorImp extends HorizontalScrollView implements FileSelectIndicator, View.OnClickListener {

    private Context mContext = null;
    private LinearLayout mRootView = null;
    private IndicatorClickCallback mCallBack = null;
    private List<Label> mLabelList = null;
    private int mTextColor;


    public FileSelectIndicatorImp(Context context) {
        this(context, null);
    }

    public FileSelectIndicatorImp(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileSelectIndicatorImp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 在这里进行初始化的方法
        this.mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.fileSelectIndicator);

        String baseName = (String) typedArray.getText(R.styleable.fileSelectIndicator_baseName);
        // 字体颜色
        mTextColor = typedArray.getColor(R.styleable.fileSelectIndicator_textColor, getResources().getColor(R.color.colorPrimaryText));
        // 有一个颜色值还没有获取
        //        typedArray.getDimension(R.styleable.fileSelectIndicator_textSize, )


        init(baseName);
    }

    private void init(String baseName) {
        mLabelList = new ArrayList<>();
        mRootView = new LinearLayout(mContext);
        mRootView.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mRootView.setLayoutParams(layoutParams);
        addView(mRootView);
        add(new Label(baseName, ""));

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
    public void add(Label label) {
        // 添加从下标为1的哪个元素开始


        if (mRootView != null) {

            View indicatorView = createIndicatorView(label);
            mRootView.addView(indicatorView);


        }


    }

    /**
     * 移除给定标签之后的所有标签
     *
     * @param label
     */
    @Override
    public void removeAfter(Label label) {

        int start = mLabelList.indexOf(label) + 1;
        int end = mLabelList.size() - 1;

        if (start <= end) {
            for (int i = end; i >= start; i--) {
                mLabelList.remove(i);
                mRootView.removeViewAt(i);
            }

        }
    }

    /**
     * 移除最后一个标签相当于返回
     */
    public void back() {
        int removeIndex = mLabelList.size() - 1;
        // 保留根标签不被移除
        if (removeIndex != 0) {
            mLabelList.remove(removeIndex);
            mRootView.removeViewAt(removeIndex);
        }

    }

    @Override
    public void setBaseStoragePath(Label label) {

    }

    @Override
    public View createIndicatorView(Label label) {


        LinearLayout linearLayout = new LinearLayout(mContext);
        // 设置布局的样式
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        // 设置布局参数
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        TextView textView = new TextView(mContext);

        // 需要对路径的进行分割，区最末尾的哪个文件目录
        textView.setText(label.getName());
        linearLayout.addView(textView);
        LinearLayout.LayoutParams tvParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        tvParams.leftMargin = DisplayUtils.dip2px(mContext, 5);

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.ic_choice_file_folder_separator);
        linearLayout.addView(imageView);

        LinearLayout.LayoutParams ivParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        ivParams.height = DisplayUtils.dip2px(mContext, 40);
        imageView.setLayoutParams(ivParams);

        linearLayout.setTag(label);
        // 将添加进来的路径保存到集合中
        mLabelList.add(label);

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

        int size = mLabelList.size();
        Label label = (Label) v.getTag();

        boolean contains = mLabelList.contains(label);

        if (contains) {

            Log.i("wk", "包含");

        } else {

            Log.i("wk", "不包含");

        }

        // 当前点击标签所在的位置
        int index = mLabelList.indexOf(label);

        Log.i("wk", "index == >" + index);


        boolean isBack = false;


        // 如果点击的标签是最后一个标签则不需要进行移除的操作
        if (index == (size - 1) && index != 0) {

            isBack = false;


        } else if (index == 0) {

            // 需要执行返回上一级的操作
            isBack = true;

        } else {

            // 需要进行移除的操作
            isBack = false;

            removeAfter(label);


        }

        if (mCallBack != null) {
            // false表示不需要返回
            mCallBack.onClick(label.getPath(), false);
        }


    }

    public void setIndicatorClickCallBack(IndicatorClickCallback mCallBack) {
        this.mCallBack = mCallBack;
    }


}


