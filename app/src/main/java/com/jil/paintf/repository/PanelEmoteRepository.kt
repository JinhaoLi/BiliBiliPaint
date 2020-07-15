package com.jil.paintf.repository

/**==============================
 *===============================
 *== @Date: 2020/7/12 11:16
 *== @author JIL
 *===============================
 *===============================
 **/

data class EmoteData(
    val code: Int,
    val `data`: ListData,
    val message: String,
    val ttl: Int
)

data class ListData(
    val packages: List<Package>
)

data class Package(
    val attr: Int,
    val emote: List<Emote>,
    val flags: FlagsX,
    val id: Int,
    val meta: MetaX,
    val mtime: Int,
    val text: String,
    val type: Int,
    val url: String
)

data class Emote(
    val attr: Int,
    val flags: Flags,
    val id: Int,
    val meta: Meta,
    val mtime: Int,
    val package_id: Int,
    val text: String,
    val type: Int,
    val url: String
)

class Flags(
)

data class Meta(
    val size: Int
)

data class FlagsX(
    val added: Boolean
)

data class MetaX(
    val item_id: Int,
    val size: Int
)