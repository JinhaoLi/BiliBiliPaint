package com.jil.paintf.repository

data class BlackListRepository(
    val code: Int,
    val `data`: BlackListData,
    val message: String,
    val ttl: Int
)

data class BlackListData(
    val list: List<User2>,
    val re_version: Long,
    val total: Int
)

data class User2(
    val attribute: Int,
    val face: String,
    val mid: Int,
    val mtime: Int,
    val official_verify: OfficialVerify,
    val sign: String,
    val special: Int,
    val tag: Any,
    val uname: String,
    val vip: Vip
)
