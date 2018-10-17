package com.merpyzf.xmshare.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.merpyzf.xmshare.R;

/**
 * description: 自定义带搜索和清除图标的EditText
 * author: wangke
 * date: 2018/8/21.
 * version:1.0
 */
public class SearchEditText extends AppCompatEditText {

    private Drawable mSearchDrawable;
    private Drawable mClearDrawable;
    private Context mContext;
    private boolean mClearIsVisible = false;
    private int mHeight;
    private int mWidth;
    private int mLength;

    public SearchEditText(Context context) {
        super(context);
        init(context);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getHeight();
        mWidth = getWidth();
        setDrawable();

    }

    private void setDrawable() {
        mSearchDrawable = getResources().getDrawable(R.drawable.ic_action_search, mContext.getTheme());
        mClearDrawable = getResources().getDrawable(R.drawable.ic_action_clear, mContext.getTheme());
        mLength = (int) (mHeight * 0.5f);
        mSearchDrawable.setBounds(0, 0, mLength, mLength);
        mClearDrawable.setBounds(0, 0, mLength, mLength);
        setCompoundDrawables(mSearchDrawable, null, null, null);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (text.length() != 0) {
            if (!mClearIsVisible) {
                setClearIcoVisible(true);
            }
        } else {
            setClearIcoVisible(false);
        }
    }



    private void setClearIcoVisible(boolean visible) {
        mClearIsVisible = visible;
        setCompoundDrawables(mSearchDrawable, null, visible ? mClearDrawable : null, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Drawable drawable = mClearDrawable;
                if (mClearIsVisible && null != drawable && event.getX() >= (getWidth() - getPaddingLeft() - drawable.getBounds().width())
                        && event.getX() <= (getWidth() - getPaddingRight())) {
                    setText("");
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
