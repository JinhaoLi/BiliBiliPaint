package com.jil.paintf.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HisItem(@ColumnInfo val docId:Int,
              @ColumnInfo val image: String,
              @ColumnInfo val title: String,
              @ColumnInfo val pageCount:Int) {
    @PrimaryKey(autoGenerate = true)
    var id:Int=0
    @ColumnInfo
    var date:Long =System.currentTimeMillis()


}
