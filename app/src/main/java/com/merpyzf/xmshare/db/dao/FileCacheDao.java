package com.merpyzf.xmshare.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.merpyzf.xmshare.db.entity.FileCache;

import java.util.List;

/**
 * 2018/11/27
 *
 * @author wangke
 */
@Dao
public interface FileCacheDao {

    @Query("SELECT * FROM file_cache")
    List<FileCache> getAll();

    @Query("SELECT * FROM file_cache WHERE name LIKE '%'|| :queryKey ||'%' ")
    List<FileCache> queryLike(String queryKey);

    @Query("SELECT *FROM file_cache WHERE path = :path")
    FileCache queryByPath(String path);


    /**
     * 插入一条数据
     *
     * @param fileCache
     * @return 返回插入记录的主键值
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FileCache fileCache);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCollection(FileCache... fileCaches);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCollection(List<FileCache> fileCaches);


    /**
     * 更新已有数据，根据传入对象的uid匹配要更新的记录
     *
     * @param fileCache
     * @return 返回更新成功的条数
     */
    @Update
    int update(FileCache fileCache);

    @Update
    int updateCollection(FileCache... fileCaches);

    @Update
    int updateCollection(List<FileCache> fileCaches);

    /**
     * 删除一条记录，根据对象的uid匹配
     *
     * @param fileCache
     * @return 成功删除的条数
     */
    @Delete
    int delete(FileCache fileCache);

    @Delete
    int deleteCollection(FileCache... fileCaches);

    @Delete
    int deleteCollection(List<FileCache> fileCaches);
}
