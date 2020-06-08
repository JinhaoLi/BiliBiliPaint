package com.jil.paintf.repository

data class CollectionResult(
    val code: Int,
    val `data`: DataXXXX,
    val message: String,
    val msg: String
)

data class DataXXXX(
    val list: List<CollectItem>,
    val pageinfo: Pageinfo
)

data class CollectItem(
    val biz_type: Int,
    val content: ContentX,
    val fav_id: Int,
    val utime: Int
)

data class ContentX(
    val item: ItemX,
    val user: User
)

data class ItemX(
    val already_collected: Int,
    val already_liked: Int,
    val category: String,
    val collect_count: Int,
    val comment_count: Int,
    val description: String,
    val doc_id: Int,
    val like_count: Int,
    val pictures: List<Picture>,
    val poster_uid: Int,
    val role: Any,
    val settings: Settings,
    val show_status: Int,
    val source: Any,
    val tags: List<Tag>,
    val title: String,
    val type: Int,
    val upload_time: String,
    val upload_time_text: String,
    val upload_timestamp: Int,
    val user_status: Int,
    val verify_status: Int,
    val view_count: Int
)


//data class Tag(
//    val category: String,
//    val name: String,
//    val tag: String,
//    val text: String,
//    val type: Int
//)

//data class User(
//    val head_url: String,
//    val name: String,
//    val uid: Int,
//    val upload_count: Int
//)

data class Pageinfo(
    val count: Int,
    val page: String,
    val pagesize: String,
    val totalpage: Int
)