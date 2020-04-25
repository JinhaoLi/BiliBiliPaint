package com.jil.paintf.repository

data class UserInfo(
    val code: Int,
    val `data`: UserData,
    val message: String,
    val ttl: Int
)

data class MyInfo(
    val code: Int,
    val `data`: DataX,
    val message: String,
    val ttl: Int
)

data class DataX(
    val allowance_count: Int,
    val answer_status: Int,
    val email_verified: Int,
    val face: String,
    val has_shop: Boolean,
    val isLogin: Boolean,
    val level_info: LevelInfo,
    val mid: Int,
    val mobile_verified: Int,
    val money: Int,
    val moral: Int,
    val official: Official,
    val officialVerify: OfficialVerify,
    val pendant: Pendant,
    val scores: Int,
    val shop_url: String,
    val uname: String,
    val vipDueDate: Int,
    val vipStatus: Int,
    val vipType: Int,
    val vip_pay_type: Int,
    val vip_theme_type: Int,
    val wallet: Wallet
)

data class LevelInfo(
    val current_exp: Int,
    val current_level: Int,
    val current_min: Int,
    val next_exp: Int
)

data class Official(
    val desc: String,
    val role: Int,
    val title: String,
    val type: Int
)

data class OfficialVerify(
    val desc: String,
    val type: Int
)

data class Wallet(
    val bcoin_balance: Int,
    val coupon_balance: Int,
    val coupon_due_time: Int,
    val mid: Int
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

data class VipX(
    val status: Int,
    val theme_type: Int,
    val type: Int
)