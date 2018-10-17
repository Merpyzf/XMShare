package com.merpyzf.xmshare.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * description: 圆角ImageView的实现
 * author: wangke
 * date: 2018/8/18.
 * version:1.0
 */
public class RoundAngleImageView extends android.support.v7.widget.AppCompatImageView {
    private float mWidth;
    private float mHeight;

    public RoundAngleImageView(Context context) {
        this(context, null);
    }

    public RoundAngleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundAngleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 获取ImageView的宽高
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mWidth > 12 && mHeight > 12) {
            Path path = new Path();
            path.moveTo(12, 0);
            path.lineTo(mWidth - 12, 0);
            path.quadTo(mWidth, 0, mWidth, 12);
            path.lineTo(mWidth, mHeight - 12);
            path.quadTo(mWidth, mHeight, mWidth - 12, mHeight);
            path.lineTo(12, mHeight);
            path.quadTo(0, mHeight, 0, mHeight - 12);
            path.lineTo(0, 12);
            path.quadTo(0, 0, 12, 0);
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }
}
