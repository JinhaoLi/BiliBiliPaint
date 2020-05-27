package com.jil.paintf.repository

data class DocRepository(
    val code: Int,
    val `data`: DocData,
    val message: String,
    val msg: String
)

data class DocData(
    val item: DocDetail,
    val user: DocUser
)

data class DocDetail(
    var already_collected: Int,
    val already_liked: Int,
    var already_voted: Int,
    val at_control: String,
    val biz: Int,
    val category: String,
    val collect_count: Int,
    val comment_count: Int,
    val description: String,
    val doc_id: Int,
    val extension: String,
    val like_count: Int,
    val pictures: List<Picture>,
    val poster_uid: Int,
    val role: Any,
    val settings: Settings,
    val source: Any,
    val tags: List<Tag>,
    val title: String,
    val type: Int,
    val upload_time: String,
    val upload_time_text: String,
    val upload_timestamp: Int,
    val user_status: Int,
    val verify_status: Int,
    val view_count: Int,
    val vote_count: Int
)


data class Settings(
    val copy_forbidden: Int
)

data class Tag(
    val category: String,
    val link: String,
    val name: String,
    val tag: String,
    val text: String,
    val type: Int
)

data class DocUser(
    val head_url: String,
    val name: String,
    val uid: Int,
    val upload_count: Int,
    val vip: Vip
)

data class Vip(
    val accessStatus: Int,
    val dueRemark: String,
    val label: Label,
    val themeType: Int,
    val vipDueDate: Long,
    val vipStatus: Int,
    val vipStatusWarn: String,
    val vipType: Int
)

data class Label(
    val path: String
)