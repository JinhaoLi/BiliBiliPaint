package com.jil.paintf.service;

import androidx.room.*;
import com.jil.paintf.network.NetCookie;
import com.jil.paintf.network.NetCookieDao;

@Database(entities = {NetCookie.class}, version = 1)
public abstract class DataRoomService extends RoomDatabase {
    public abstract NetCookieDao getCookieDao();

    //单例
    public static DataRoomService getDatabase(){
        return Holder.instance;
    }

    private static class Holder{
        private static final DataRoomService instance= Room.databaseBuilder(AppPaintF.instance, DataRoomService.class, "cookieData")
                .allowMainThreadQueries()   //设置允许在主线程进行数据库操作，默认不允许，建议都设置为默认
                // .fallbackToDestructiveMigration()  //设置数据库升级的时候清除之前的所有数据
                .build();
    }

}
