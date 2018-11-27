package com.merpyzf.xmshare.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.merpyzf.xmshare.db.entity.FileCache;
import com.merpyzf.xmshare.db.dao.FileCacheDao;

/**
 * 2018/11/27
 * @author wangke
 */
@Database(entities = {FileCache.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "xmshare.db";
    private static volatile AppDatabase sInstance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = create(context);
        }
        return sInstance;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class, DB_NAME
        ).build();
    }

    public abstract FileCacheDao getFileCacheDao();
}
