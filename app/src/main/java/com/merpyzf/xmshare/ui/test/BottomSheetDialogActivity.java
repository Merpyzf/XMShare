package com.merpyzf.xmshare.ui.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.merpyzf.xmshare.R;

public class BottomSheetDialogActivity extends AppCompatActivity {

    private BottomSheetBehavior<View> mBehavior;
    private Button mBtnBs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectedfiles_bottom_sheet_dialog);

        View viewBottomSheet = findViewById(R.id.bottom_sheet);
        mBtnBs = findViewById(R.id.btn_bottomsheet);

        mBehavior = BottomSheetBehavior.from(viewBottomSheet);

        mBtnBs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }


            }
        });

        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                // BottomSheet状态的改变
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                // 这里是BottomSheet拖拽的回调，可以根据slideOffset设置动画

            }
        });
    }
}
