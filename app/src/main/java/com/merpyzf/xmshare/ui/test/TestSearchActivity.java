package com.merpyzf.xmshare.ui.test;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.merpyzf.xmshare.R;

/**
 * 从Android系统内置的媒体数据库中快速的查找文件
 */
public class TestSearchActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_lite_orm);

        LoaderManager loaderManager = this.getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("name", "1");
        loaderManager.initLoader(1, bundle, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Files.getContentUri("external");
        String name = (String) args.getCharSequence("name");
        Log.i("wk", "name=> " + name);
//        需要对查询到的文件进行文件分类上的限制
        return new CursorLoader(TestSearchActivity.this, uri, null, MediaStore.Files.FileColumns.TITLE+" like "+"\"%"+name+"%\"", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null!=data) {
            int count = data.getCount();
            data.moveToFirst();
            String columnName = data.getString(data.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
            String columnPath = data.getString(data.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            Log.i("wk", "columnName==> "+columnName);
            Log.i("wk", "columnPath==> "+columnPath);
            Log.i("wk", "查询到的文件数量-> "+count);
        }else {
            Log.i("wk", "course为null");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
