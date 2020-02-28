package com.jil.paintf.repository

data class UserInfo(
    val code: Int,
    val `data`: UserData,
    val message: String,
    val ttl: Int
)

data class UserData(
    val birthday: String,
    val coins: Int,
    val face: String,
    val fans_badge: Boolean,
    val is_followed: Boolean,
    val jointime: Int,
    val level: Int,
    val mid: Int,
    val moral: Int,
    val name: String,
    val official: Official,
    val rank: Int,
    val sex: String,
    val sign: String,
    val silence: Int,
    val top_photo: String,
    val vip: VipX
)

data class Official(
    val desc: String,
    val role: Int,
    val title: String,
    val type: Int
)

data class VipX(
    val status: Int,
    val theme_type: Int,
    val type: Int
)