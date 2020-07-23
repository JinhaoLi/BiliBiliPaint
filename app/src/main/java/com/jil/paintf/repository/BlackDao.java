package com.jil.paintf.repository;

import androidx.room.*;

import java.util.List;

/**
 * 2020/7/23 23:21
 *
 * @author JIL
 **/
@Dao
public interface BlackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BlackUID... blackUIDS);

    @Query("delete from  BlackUID where uid =:uid")
    void deleteByUid(int uid);

    @Query("select uid from BlackUID")
    List<Integer> loadAll();
}
