package com.merpyzf.xmshare.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.merpyzf.fileserver.FileServer;
import com.merpyzf.transfermanager.util.NetworkUtil;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.factory.FileInfoFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TestFileServerActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    @BindView(R.id.btn_startup)
    public Button mBtnStartup;
    @BindView(R.id.btn_shutdown)
    public Button mBtnShutdown;
    private FileServer mFileServer;
    private Context mContext;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TestFileServerActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_file_server);
        mUnbinder = ButterKnife.bind(this);
        mContext = this;

        mBtnStartup.setOnClickListener(v -> {
            String localIp = NetworkUtil.getLocalIp(this);
            mFileServer = FileServer.getInstance();
            mFileServer.startupFileShareServer(mContext, "192.168.43.1", FileInfoFactory.toFileServerType(App.getTransferFileList()));
        });
        mBtnShutdown.setOnClickListener(v -> mFileServer.stopRunning());
    }
}
