package com.jil.paintf.repository;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 2020/7/23 23:19
 *
 * @author JIL
 **/
@Entity
public class BlackUID {
    @PrimaryKey
    int uid;
//    @ColumnInfo(name = "")
//    long date;

    public BlackUID(int uid) {
        this.uid = uid;
    }
}
