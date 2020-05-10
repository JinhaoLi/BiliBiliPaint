package com.jil.paintf.repository

data class UpLoadResult(
    val code: Int,
    val `data`: DataXX,
    val message: String
)

data class DataXX(
    val image_height: Int,
    val image_url: String,
    val image_width: Int
)