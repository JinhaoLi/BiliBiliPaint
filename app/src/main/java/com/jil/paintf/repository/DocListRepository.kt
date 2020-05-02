package com.jil.paintf.repository

import androidx.room.Entity

data class DocListRepository(
    val code: Int,
    val `data`: Data,
    val message: String,
    val msg: String
)

data class Data(
    val items: List<Item>,
    val total_count: Int
)


data class Item(
    val item: Doc,
    val user: User
)

data class Doc(
    val already_liked: Int,
    var already_voted: Int,
    val category: String,
    val doc_id: Int,
    val pictures: List<Picture>,
    val poster_uid: Int,
    val title: String,
    val upload_time: Int
)

data class User(
    val head_url: String,
    val name: String,
    val uid: Int
)