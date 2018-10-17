package com.merpyzf.xmshare.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.ui.adapter.AvatarAdapter;
import com.merpyzf.xmshare.ui.interfaces.PersonalObservable;
import com.merpyzf.xmshare.util.SharedPreUtils;

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
public class PersonalActivity extends AppCompatActivity {
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

    private Context mContext;
    private AvatarAdapter mAvatarAdapter;
    private Unbinder mUnbind;
    private int mAvatarPosition;
    private static final String TAG = PersonalActivity.class.getSimpleName();

    public static void start(Context context) {
        context.startActivity(new Intent(context, PersonalActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        mContext = this;
        initUI();
        initEvent();
    }

    private void initEvent() {

        mAvatarAdapter.setOnItemClickListener((adapter, view, position) -> {
            mAvatarPosition = position;
            Glide.with(mContext)
                    .load(Const.AVATAR_LIST.get(position))
                    .crossFade()
                    .centerCrop()
                    .into(mCivAvatar);

            setWidgetsBgColor(mAvatarPosition);


        });

        // 保存用户设置
        mBtnSave.setOnClickListener(v -> {
            SharedPreUtils.putString(mContext, Const.SP_USER, "nickName", mEdtNickname.getText().toString().trim());
            SharedPreUtils.putInteger(mContext, Const.SP_USER, "avatar", mAvatarPosition);
            Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            PersonalObservable.getInstance().notifyAllObserver();
        });
    }

    private void setWidgetsBgColor(int mAvatarPosition) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Const.AVATAR_LIST.get(mAvatarPosition));
        Palette.from(bitmap).generate(p -> {
            Palette.Swatch swatch = p.getVibrantSwatch();
            if (swatch != null) {
                int rgb = swatch.getRgb();
                mRlHeader.setBackgroundColor(rgb);
                mToolBar.setBackgroundColor(rgb);
                mBtnSave.setBackgroundColor(rgb);
            }
        });
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        mUnbind = ButterKnife.bind(mContext, this);
        // 设置昵称
        mEdtNickname.setText(SharedPreUtils.getNickName(mContext));
        int avatarPosition = SharedPreUtils.getAvatar(mContext);
        // 设置头像
        Glide.with(mContext)
                .load(Const.AVATAR_LIST.get(avatarPosition))
                .crossFade()
                .centerCrop()
                .into(mCivAvatar);
        setWidgetsBgColor(avatarPosition);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("头像和昵称");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRvAvatarList.setLayoutManager(new GridLayoutManager(mContext, 4));
        mAvatarAdapter = new AvatarAdapter(R.layout.item_rv_avatar, Const.AVATAR_LIST);
        mRvAvatarList.setAdapter(mAvatarAdapter);
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
