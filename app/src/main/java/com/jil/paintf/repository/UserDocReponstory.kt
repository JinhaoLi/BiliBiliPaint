package com.jil.paintf.repository

data class UserDocListRep(
    val code: Int,
    val `data`: UserDoc,
    val message: String,
    val msg: String
)

data class UserDoc(
    val items: List<DocX>
)

data class DocX(
    val count: Int,
    val ctime: Int,
    val description: String,
    val doc_id: Int,
    val like: Int,
    val pictures: List<Picture>,
    val poster_uid: Int,
    val title: String,
    val view: Int
)

data class Picture(
    val img_height: Int,
    val img_size: Int,
    val img_src: String,
    val img_width: Int
)