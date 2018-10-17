package com.merpyzf.xmshare.ui.test;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.merpyzf.xmshare.R;

public class ScanVideoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static int LOAD_FILE_VIDEO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_video);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager = loaderManager;
        loaderManager.initLoader(LOAD_FILE_VIDEO, null, this);



    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.i("w2k", "onCreateLoader执行了");

       Uri mUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] mProjections = new String[]
                {
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.DATA, // 文件路径
                        MediaStore.Video.Media.DATE_ADDED // 文件添加/修改时间
                };



        return new CursorLoader(this, mUri, mProjections, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.i("w2k", "onLoadFinished执行了"+ data.getCount());
        while (data.moveToNext()){

            String title = data.getString(data.getColumnIndex( MediaStore.Video.Media.TITLE));
            Log.i("w2k", title);


        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i("w2k", "onLoaderReset执行了");
    }
}
