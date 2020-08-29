package com.jil.paintf.repository

/**
 * 2020/8/29 21:56
 * @author JIL
 **/
data class RankResult(
    val code: Int,
    val `data`: RankData,
    val message: String,
    val msg: String
)

data class RankData(
    val items: List<RankItem>,
    val total_count: Int
)

data class RankItem(
    val item: RankItemX,
    val user: User
)

data class RankItemX(
    val already_liked: Int,
    val already_voted: Int,
    val category: String,
    val collect_count: Int,
    val doc_id: Int,
    val like_count: Int,
    val pictures: List<Picture>,
    val poster_uid: Int,
    val title: String,
    val upload_time: Int,
    val view_count: Int
)
