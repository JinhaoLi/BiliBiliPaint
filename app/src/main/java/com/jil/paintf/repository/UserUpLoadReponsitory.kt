package com.jil.paintf.repository

data class UserUpLoad(
    val code: Int,
    val `data`: upLoadCount,
    val message: String,
    val msg: String
)

data class upLoadCount(
    val all_count: Int,
    val daily_count: Int,
    val draw_count: Int,
    val photo_count: Int
)