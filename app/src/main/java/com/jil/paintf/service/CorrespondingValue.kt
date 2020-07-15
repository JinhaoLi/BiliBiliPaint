package com.jil.paintf.service
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.annotation.RequiresApi
import com.jil.paintf.R

object CorrespondingValue {
    fun getLvBg(level: Int): Int {
        return when(level){
            0->{
                R.drawable.level_bg_0_1
            }
            1->{
                R.drawable.level_bg_0_1
            }
            2->{
               R.drawable.level_bg_2
            }
            3->{
               R.drawable.level_bg_3
            }
            4->{
               R.drawable.level_bg_4
            }
            5->{
                R.drawable.level_bg_5
            }
            6->{
                R.drawable.level_bg_6
            }
            7 -> {
                R.drawable.level_bg_7
            }
            8-> {
                R.drawable.level_bg_8
            }
            9 -> {
                R.drawable.level_bg_9
            }
            else -> {
                R.drawable.level_bg_0_1
            }
        }
    }
    /**
     * awsl [喜欢] [喜欢]
     * return 喜欢 <img src ='[喜欢]'>
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getHtmlFormte(message: String): Spanned {
        var result =message
        result =result.replace("[","<img src ='[")
        result =result.replace("]","]'>")
        return Html.fromHtml(result,
            Html.FROM_HTML_MODE_LEGACY,object : Html.ImageGetter{
                override fun getDrawable(source: String): Drawable? {
                    return if(AppPaintF.instance.emoteMap.containsKey(source)){
                        AppPaintF.instance.emoteMap[source]
                    }else{
                        Log.d("PaintF","找不到 =$source")
                        AppPaintF.instance.getDrawable(R.drawable.ic_no_voted)
                    }
                }
            },null)
    }
}