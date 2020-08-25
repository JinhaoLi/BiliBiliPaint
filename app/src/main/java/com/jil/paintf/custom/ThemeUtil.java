package com.jil.paintf.custom;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.jil.paintf.R;
import com.orhanobut.logger.Logger;

public class ThemeUtil {

    private static final int[] THEME={
            R.style.AppTheme,
            R.style.AppTheme_pink,
            R.style.AppTheme_green,
            R.style.AppTheme_purple,
            R.style.AppTheme_tea,
            R.style.AppTheme_walnut,
            R.style.AppTheme_seaPine,
            R.style.AppTheme_seedling,
    };

    public static void initTheme(AppCompatActivity activity){
        int themeIndex = PreferenceManager.getDefaultSharedPreferences(activity).getInt("THEME",0);
        if(themeIndex>THEME.length){
            themeIndex=0;
        }
        activity.setTheme(THEME[themeIndex]);
    }

    public static int themeResId(Context context){
        int themeIndex = PreferenceManager.getDefaultSharedPreferences(context).getInt("THEME",0);
        if(themeIndex>THEME.length){
            themeIndex=0;
        }
        return THEME[themeIndex];
    }

    public static int getColorAccent(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return 0xFFD81B60;
        }
        int themeIndex = PreferenceManager.getDefaultSharedPreferences(context).getInt("THEME",0);
        if(themeIndex>THEME.length){
            themeIndex=0;
        }
        switch(themeIndex){
            case 1:
                return context.getColor(R.color.pink_dark);

            case 2:
                return context.getColor(R.color.green_light);

            case 3:
                return context.getColor(R.color.purple_dark);

            case 4:
                return context.getColor(R.color.tea_dark);

            case 5:
                return context.getColor(R.color.walnut_light);

            case 6:
                return context.getColor(R.color.seaPine_light);

            case 7:
                return context.getColor(R.color.seedling);

            default:
                return context.getColor(R.color.colorAccent);
        }
    }

    public static int getColorPrimary(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return 0xFFD81B60;
        }
        int themeIndex = PreferenceManager.getDefaultSharedPreferences(context).getInt("THEME",0);
        if(themeIndex>THEME.length){
            themeIndex=0;
        }
        switch(themeIndex){
            case 1:
                return context.getColor(R.color.pink);

            case 2:
                return context.getColor(R.color.green);

            case 3:
                return context.getColor(R.color.purple);

            case 4:
                return context.getColor(R.color.tea);

            case 5:
                return context.getColor(R.color.walnut);

            case 6:
                return context.getColor(R.color.seaPine_light);

            case 7:
                return context.getColor(R.color.seedling);

            default:
                return context.getColor(R.color.colorPrimary);
        }
    }
}
