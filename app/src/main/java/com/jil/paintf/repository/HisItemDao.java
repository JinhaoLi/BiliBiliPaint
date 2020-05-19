package com.jil.paintf.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface HisItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HisItem... hisItems);

    @Query("delete from HisItem where date <:date")
    void deleteByDate(long date);

    @Query("select * from HisItem ORDER BY date DESC")
    List<HisItem> loadAll();
}
