package com.merpyzf.xmshare.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.observer.FilesStatusObservable;
import com.merpyzf.xmshare.observer.FilesStatusObserver;
import com.merpyzf.xmshare.ui.adapter.FileSelectAdapter;
import com.merpyzf.xmshare.ui.adapter.FilesFrgPagerAdapter;
import com.merpyzf.xmshare.ui.fragment.FileListFragment;
import com.merpyzf.xmshare.ui.fragment.MainFragment;
import com.merpyzf.xmshare.ui.fragment.PhotoFragment;
import com.merpyzf.xmshare.observer.PersonalObservable;
import com.merpyzf.xmshare.observer.PersonalObserver;
import com.merpyzf.xmshare.util.AnimationUtils;
import com.merpyzf.xmshare.util.FileUtils;
import com.merpyzf.xmshare.util.SharedPreUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.merpyzf.xmshare.common.Const.HOME_OBSERVER_NAME;

/**
 * 应用首页界面
 *
 * @author wangke
 */
public class SelectFilesActivity extends BaseActivity implements PersonalObserver {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.bottom_sheet)
    View mBottomSheet;
    @BindView(R.id.tool_bar)
    android.support.v7.widget.Toolbar mToolBar;
    @BindView(R.id.tv_bottom_title)
    TextView mTvBottomTitle;
    @BindView(R.id.rv_selected)
    RecyclerView mRvSelectedFiles;
    @BindView(R.id.fab_send)
    FloatingActionButton mFabBtn;
    @BindView(R.id.linear_menu)
    LinearLayout mLinearMenu;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.civ_avatar)
    CircleImageView mCivAvatar;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    CircleImageView mNavCivAvatar;
    TextView mNavTvNickname;

    private List<Fragment> mFragmentList;
    private String[] mTabTitles;
    private BottomSheetBehavior<View> mSheetBehavior;
    private FileSelectAdapter<FileInfo> mFileSelectAdapter;
    private static int sFabState = Const.FAB_STATE_SEND;
    private static final String TAG = SelectFilesActivity.class.getSimpleName();
    private FragmentManager mFragmentManager;
    private FilesStatusObserver mFilesStatusObserver;

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_select_file;
    }

    @Override
    public void initRecyclerView() {
        mRvSelectedFiles.setLayoutManager(new LinearLayoutManager(mContext));
        mFileSelectAdapter = new FileSelectAdapter<>(mContext, R.layout.item_rv_select, App.getSendFileList());
        mRvSelectedFiles.setAdapter(mFileSelectAdapter);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initWidget(Bundle savedInstanceState) {
        View headerView = mNavigationView.getHeaderView(0);
        mNavCivAvatar = headerView.findViewById(R.id.civ_avatar);
        mNavTvNickname = headerView.findViewById(R.id.tv_nickname);

        updateUserInfo();
        updateBottomTitle();
        // 初始并显示SheetBottom中的Recycler
        initRecyclerView();
        mSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mFragmentManager = getSupportFragmentManager();
    }


    @Override
    protected void initData() {
        initMainPageAndSetListener();
    }

    /**
     * 初始化类别页面，并为文件选择设置监听
     */
    private void initMainPageAndSetListener() {
        mFilesStatusObserver = new FilesStatusObserver() {
            @Override
            public void onSelected(FileInfo fileInfo) {
                App.addSendFile(fileInfo);
                mFileSelectAdapter.notifyDataSetChanged();
                updateBottomTitle();
            }

            @Override
            public void onCancelSelected(FileInfo fileInfo) {
                App.removeSendFile(fileInfo);
                mFileSelectAdapter.notifyDataSetChanged();
                updateBottomTitle();
            }

            @Override
            public void onSelectedAll(List<FileInfo> fileInfoList) {
                App.addSendFiles(fileInfoList);
                mFileSelectAdapter.notifyDataSetChanged();
                updateBottomTitle();
            }

            @Override
            public void onCancelSelectedAll(List<FileInfo> fileInfoList) {
                App.removeSendFiles(fileInfoList);
                mFileSelectAdapter.notifyDataSetChanged();
                updateBottomTitle();
            }
        };
        FilesStatusObservable.getInstance().register(HOME_OBSERVER_NAME, mFilesStatusObserver);
        mFragmentList = new ArrayList<>();
        mTabTitles = new String[5];
        mTabTitles[0] = Const.PAGE_MAIN_TITLE;
        mTabTitles[1] = Const.PAGE_APP_TITLE;
        mTabTitles[2] = Const.PAGE_IMAGE_TITLE;
        mTabTitles[3] = Const.PAGE_MUSIC_TITLE;
        mTabTitles[4] = Const.PAGE_VIDEO_TITLE;
        mFragmentList.add(new MainFragment());
        FileListFragment appFragment = FileListFragment.newInstance(FileInfo.FILE_TYPE_APP);
        mFragmentList.add(appFragment);
        Fragment picFragment = new PhotoFragment();
        mFragmentList.add(picFragment);
        FileListFragment musicFragment = FileListFragment.newInstance(FileInfo.FILE_TYPE_MUSIC);
        mFragmentList.add(musicFragment);
        FileListFragment videoFragment = FileListFragment.newInstance(FileInfo.FILE_TYPE_VIDEO);
        mFragmentList.add(videoFragment);
    }

    @Override
    public void initEvents() {
        // 申请文件读写权限
        requestPermission();
        // 注册一个用于检查用户信息变化的观察者对象
        PersonalObservable.getInstance().register(this);
        // BottomSheet的滑动中的回调事件
        mSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // 如果未选择文件则禁止BottomSheet滑动
                if (App.getSendFileList().size() == 0) {
                    mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (sFabState == Const.FAB_STATE_CLEAR) {
                        AnimationUtils.showFabSend(mContext, mFabBtn);
                        sFabState = Const.FAB_STATE_SEND;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        mFileSelectAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            List data = adapter.getData();
            FileInfo fileInfo = (FileInfo) adapter.getData().get(position);
            mFileSelectAdapter.notifyItemRemoved(position);
            App.removeSendFile(fileInfo);

            FilesStatusObservable.getInstance().notifyObservers(fileInfo, HOME_OBSERVER_NAME,
                    FilesStatusObservable.FILE_CANCEL_SELECTED);

            updateBottomTitle();
        });
        mFileSelectAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (sFabState == Const.FAB_STATE_SEND) {
                AnimationUtils.showFabClearAll(mContext, mFabBtn, R.color.red_700);
                sFabState = Const.FAB_STATE_CLEAR;
            } else {
                AnimationUtils.showFabSend(mContext, mFabBtn);
                sFabState = Const.FAB_STATE_SEND;
            }
            return false;
        });
        mNavCivAvatar.setOnClickListener(v -> PersonalActivity.start(mContext));
        mFabBtn.setOnClickListener(v -> {
            if (sFabState == Const.FAB_STATE_SEND) {
                if (App.getSendFileList().size() > 0) {
                    markLastFile();
                    SendActivity.start(mContext);
                } else {
                    Toast.makeText(mContext, "请选择文件", Toast.LENGTH_SHORT).show();
                }
            } else {
                App.getSendFileList().clear();
                // 发送文件选择状态改变的应用内广播
                updateBottomTitle();
                mFileSelectAdapter.notifyDataSetChanged();
                FilesStatusObservable.getInstance().notifyObservers(App.getSendFileList(), HOME_OBSERVER_NAME,
                        FilesStatusObservable.FILE_CANCEL_SELECTED_ALL);
            }
        });
        // 顶部menu按钮
        mLinearMenu.setOnClickListener(v -> {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        // 导航栏菜单的点击事件
        mNavigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                // 接收文件
                case R.id.nav_receive:
                    ReceiveActivity.start(mContext);
                    break;
                // 电脑传
                case R.id.nav_transfer2pc:
                    TestFileServerActivity.start(mContext);
                    break;
                // 邀请安装
                case R.id.nav_invite:
                    InviteActivity.start(mContext);
                    break;
                // 设置
                case R.id.nav_setting:
                    SettingActivity.start(mContext, SettingActivity.class);
                    break;
                // 分享
                case R.id.nav_share:
                    break;
                // 检查新版本
                case R.id.nav_update:
                    break;
                // 反馈
                case R.id.nav_feedback:
                    break;
                default:
                    break;
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void markLastFile() {
        FileInfo fileInfo = App.getSendFileList().get(App.getSendFileList().size() - 1);
        fileInfo.setIsLast(com.merpyzf.transfermanager.common.Const.IS_LAST);
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 关闭热点，考虑用户没有进行后续的文件传输直接退回到当前界面的情况
        App.closeHotspotOnAndroidO();
    }

    /**
     * 申请文件读写权限
     */
    private void requestPermission() {
        new RxPermissions(SelectFilesActivity.this)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    // 用户已经同意给与该权限
                    if (permission.granted) {
                        // 加载ViewPager
                        FilesFrgPagerAdapter frgPagerAdapter = new FilesFrgPagerAdapter(mFragmentManager, mFragmentList, mTabTitles);
                        mViewPager.setAdapter(frgPagerAdapter);
                        // 将顶部tab与ViewPager绑定
                        mTabs.setupWithViewPager(mViewPager);
                        mViewPager.setOffscreenPageLimit(4);
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        Log.d(TAG, permission.name + " is denied. More info should be provided.");
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        Log.d(TAG, permission.name + " is denied.");
                    }
                });
    }

    /**
     * 当用户信息发生变化时的界面更新
     */
    @Override
    public void updateUserInfo() {
        mNavTvNickname.setText(SharedPreUtils.getNickName(mContext));
        setAvatar(mNavCivAvatar, Const.AVATAR_LIST.get(SharedPreUtils.getAvatar(mContext)));
        setAvatar(mCivAvatar, Const.AVATAR_LIST.get(SharedPreUtils.getAvatar(mContext)));
    }

    /**
     * 更新底部BottomSheet的标题
     */
    public void updateBottomTitle() {
        if (mTvBottomTitle == null) {
            return;
        }
        if (App.getSendFileList().size() == 0) {
            if (mTvBottomTitle == null) {
                return;
            }
            mTvBottomTitle.setText("请选择要传输的文件");
            return;
        }
        mTvBottomTitle.setText("已选文件个数: " + App.getSendFileList().size());
    }

    /**
     * 设置头像
     *
     * @param view   圆形头像View
     * @param avatar 头像资源索引
     */
    private void setAvatar(CircleImageView view, int avatar) {
        // 设置头像
        Glide.with(mContext)
                .load(avatar)
                .crossFade()
                .centerCrop()
                .into(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int currentItem = mViewPager.getCurrentItem();
            if (currentItem == Const.PAGE_IMAGE) {
                popBackStack(getSupportFragmentManager());
                popIndicator();
            } else {
                finish();
            }
        }
    }

    /**
     * 移除指示图片所在目录的标签
     */
    private void popIndicator() {
        PhotoFragment imageFragment = null;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof PhotoFragment) {
                imageFragment = (PhotoFragment) fragment;
            }
        }
        if (imageFragment == null) {
            return;
        }
        imageFragment.getFileSelectIndicator().back();
    }

    public void popBackStack(FragmentManager fragmentManager) {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        // 取消注册，从观察者集合中移除
        PersonalObservable.getInstance().unRegister(this);
        App.getSendFileList().clear();
        super.onDestroy();
    }


}
