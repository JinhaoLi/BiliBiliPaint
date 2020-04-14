package com.jil.paintf.network;

import androidx.room.*;

import java.util.List;
@Dao
public interface NetCookieDao {
    //===============================================insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NetCookie cookie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NetCookie... cookie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<NetCookie> cookie);

    //===============================================update
    @Update
    void update(NetCookie cookie);

    @Update
    void update(NetCookie... illusive);

    //===============================================delete
    @Delete
    void delete(NetCookie cookie);

    @Delete
    void delete(NetCookie... cookie);

    @Query("delete from netCookie")
    void deleteAll();

    @Query("delete from netCookie where DedeUserID =:id")
    void deleteById(int id);

    //===============================================check
    @Query("select * from netCookie")
    List<NetCookie> loadAll();

    @Query("select * from netCookie where DedeUserID =:id")
    NetCookie loadById(int id);

    @Query("select * from netCookie where DedeUserID =:tag")
    NetCookie loadByTag(String tag);
}
