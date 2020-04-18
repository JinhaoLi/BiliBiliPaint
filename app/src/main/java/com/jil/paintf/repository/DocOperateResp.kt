package com.jil.paintf.repository

data class OperateResult(
    val code: Int,
    val `data`: Data2,
    val message: String,
    val msg: String
)

data class UserOperateResult(
    val code: Int,
    val message: String,
    val ttl: Int
)

data class Data2(
    val type: Int
)