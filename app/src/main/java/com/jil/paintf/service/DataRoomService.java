package com.jil.paintf.service;

import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.jil.paintf.network.NetCookie;
import com.jil.paintf.network.NetCookieDao;
import com.jil.paintf.repository.HisItem;
import com.jil.paintf.repository.HisItemDao;

@Database(entities = {NetCookie.class, HisItem.class}, version = 1)
public abstract class DataRoomService extends RoomDatabase {
    public abstract NetCookieDao getCookieDao();
    public abstract HisItemDao getHisItemDao();

    //单例
    public static DataRoomService getDatabase(){
        return Holder.instance;
    }

    private static class Holder{
        private static final DataRoomService instance= Room.databaseBuilder(AppPaintF.instance, DataRoomService.class, "app_data")
                .allowMainThreadQueries()   //设置允许在主线程进行数据库操作，默认不允许，建议都设置为默认
                // .fallbackToDestructiveMigration()  //设置数据库升级的时候清除之前的所有数据
//                .addMigrations(update1_2)
                .build();
    }


    /**
     * SQLite 的 ALTER TABLE 命令不通过执行一个完整的转储和数据的重载来修改已有的表。
     * 您可以使用 ALTER TABLE 语句重命名表，使用 ALTER TABLE 语句还可以在已有的表中添加额外的列。
     * 在 SQLite 中，除了重命名表和在已有的表中添加列，ALTER TABLE 命令不支持其他操作。
     *
     * 语法
     *
     * 用来重命名已有的表的 ALTER TABLE 的基本语法如下：
     * ALTER TABLE database_name.table_name RENAME TO new_table_name;
     *
     * 用来在已有的表中添加一个新的列的 ALTER TABLE 的基本语法如下：
     * ALTER TABLE database_name.table_name ADD COLUMN column_def...;
     */
    static final Migration update1_2 =new Migration(1,2){

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE users " + " ADD COLUMN last_update INTEGER");
            database.execSQL("");
        }
    };

}
