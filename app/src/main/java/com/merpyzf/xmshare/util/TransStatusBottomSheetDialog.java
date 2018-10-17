package com.merpyzf.xmshare.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.view.ViewGroup;
import android.view.Window;

import net.qiujuer.genius.ui.Ui;

/**
 * 自定义透明的dialog
 *
 * @author qiujuer
 */
public class TransStatusBottomSheetDialog extends BottomSheetDialog {

    public TransStatusBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    public TransStatusBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window window = getWindow();

        if (window == null) {
            return;
        }

        //获取屏幕的高度
        int screenHeight = getContext().getResources().getDisplayMetrics()
                .heightPixels;

        //默认设置状态栏的高度为25dp // TODO: 2017-07-19 自动获取当前设备的状态栏的高度
        int statusHeight = (int) Ui.dipToPx(getContext().getResources(), 25);

        //dialog真实的高度
        int dialogHeight = screenHeight - statusHeight;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight
        );
    }
}
