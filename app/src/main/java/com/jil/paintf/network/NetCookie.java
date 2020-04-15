package com.jil.paintf.network;

import androidx.annotation.NonNull;
import androidx.room.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class NetCookie {
    @PrimaryKey
    public int DedeUserID;
    @ColumnInfo
    public String _uuid;
    @ColumnInfo
    public String sid;
    @ColumnInfo
    public String buvid3;
    @ColumnInfo
    public String SESSDATA;
    @ColumnInfo
    public String bili_jct;

    public NetCookie() {
    }

    @Ignore
    public NetCookie(String cookieStr) {
        String[] sp =cookieStr.split(";");
        for(String s:sp){
            if(s.startsWith("_uuid=")){
                _uuid =s.split("=")[1];
                continue;
            }
            if(s.contains("sid=")){
                sid =s.split("=")[1];
                continue;
            }
            if(s.contains("buvid3=")){
                buvid3 =s.split("=")[1];
                continue;
            }
            if(s.contains("SESSDATA=")){
                SESSDATA =s.split("=")[1];
                continue;
            }
            if(s.contains("DedeUserID=")){
                Pattern p = Pattern.compile("\\d{1,9}");
                Matcher m = p.matcher(s);
                if(m.find()){
                    DedeUserID =Integer.parseInt(m.group());
                }else {
                    DedeUserID=-1;
                }
                continue;
            }

            if(s.contains("bili_jct=")){
                bili_jct =s.split("=")[1];
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "l=v; _uuid="+_uuid+";"+
                " sid="+sid+";"+
                " buvid3="+buvid3+";"+
                " DedeUserID="+DedeUserID+";"+
                " SESSDATA="+SESSDATA+";"+
                " bili_jct="+bili_jct;
    }
}

