package com.merpyzf.xmshare.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.observer.PersonalObservable;
import com.merpyzf.xmshare.ui.adapter.AvatarAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wangke on 18-1-31.
 * 头像和昵称设置
 *
 * @author wangke
 */
public class PersonalActivity extends BaseActivity {
    @BindView(R.id.civ_avatar)
    CircleImageView mCivAvatar;
    @BindView(R.id.rv_avatar_list)
    RecyclerView mRvAvatarList;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rl_header)
    RelativeLayout mRlHeader;
    @BindView(R.id.btn_save)
    Button mBtnSave;
    @BindView(R.id.edt_nickname)
    EditText mEdtNickname;
    private AvatarAdapter mAvatarAdapter;
    private Unbinder mUnbind;
    private int mAvatarPosition;

    public static void start(Context context) {
        context.startActivity(new Intent(context, PersonalActivity.class));
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        mUnbind = ButterKnife.bind(mContext, this);
        // 设置昵称
        mEdtNickname.setText(PersonalSettingUtils.getNickname(mContext));
        int avatarPosition = PersonalSettingUtils.getAvatar(mContext);
        Glide.with(mContext)
                .load(Const.AVATAR_LIST.get(avatarPosition))
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mCivAvatar);
        //setWidgetsBgColor(avatarPosition);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("头像和昵称");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRvAvatarList.setLayoutManager(new GridLayoutManager(mContext, 4));
        mAvatarAdapter = new AvatarAdapter(R.layout.item_rv_avatar, Const.AVATAR_LIST);
        mRvAvatarList.setAdapter(mAvatarAdapter);
    }

    @Override
    protected void doCreateEvent() {
        mAvatarAdapter.setOnItemClickListener((adapter, view, position) -> {
            mAvatarPosition = position;
            Glide.with(mContext)
                    .load(Const.AVATAR_LIST.get(position))
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mCivAvatar);
        });

        // 保存用户设置
        mBtnSave.setOnClickListener(v -> {
            PersonalSettingUtils.saveNickname(mContext, mEdtNickname.getText().toString().trim());
            PersonalSettingUtils.saveAvatar(mContext, mAvatarPosition);
            Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            PersonalObservable.getInstance().notifyAllObserver();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        mUnbind.unbind();
        super.onDestroy();
    }

}
