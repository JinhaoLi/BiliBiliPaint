package com.jil.paintf.service
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
}