package com.jil.paintf.repository

data class ReplyNextRespository(
    val code: Int,
    val `data`: DataNext,
    val message: String,
    val ttl: Int
)

data class DataNext(
    val config: Config,
    val control: Control,
    val page: PageNext,
    val replies: List<Reply>,
    val root: Root,
    val show_bvid: Boolean,
    val upper: UpperX
)


data class PageNext(
    val count: Int,
    val num: Int,
    val size: Int
)

data class PendantX(
    val id: Int,
    val image: String,
    val jump_url: String,
    val name: String,
    val type: String
)

data class Root(
    val action: Int,
    val assist: Int,
    val attr: Int,
    val content: ContentXX,
    val count: Int,
    val ctime: Int,
    val dialog: Int,
    val fansgrade: Int,
    val folder: FolderX,
    val like: Int,
    val member: MemberX,
    val mid: Int,
    val oid: Int,
    val parent: Int,
    val parent_str: String,
    val rcount: Int,
    val replies: Any,
    val root: Int,
    val root_str: String,
    val rpid: Long,
    val rpid_str: String,
    val show_follow: Boolean,
    val state: Int,
    val type: Int,
    val up_action: UpActionX
)

data class ContentXX(
    val device: String,
    val jump_url: JumpUrlX,
    val max_line: Int,
    val members: List<Any>,
    val message: String,
    val plat: Int
)

class JumpUrlX(
)

data class FolderX(
    val has_folded: Boolean,
    val is_folded: Boolean,
    val rule: String
)

data class MemberX(
    val DisplayRank: String,
    val avatar: String,
    val fans_detail: Any,
    val following: Int,
    val is_followed: Int,
    val level_info: LevelInfoX,
    val mid: String,
    val nameplate: NameplateX,
    val official_verify: OfficialVerifyX,
    val pendant: PendantXX,
    val rank: String,
    val sex: String,
    val sign: String,
    val uname: String,
    val user_sailing: UserSailingX,
    val vip: VipXX
)

data class LevelInfoX(
    val current_exp: Int,
    val current_level: Int,
    val current_min: Int,
    val next_exp: Int
)

data class NameplateX(
    val condition: String,
    val image: String,
    val image_small: String,
    val level: String,
    val name: String,
    val nid: Int
)

data class OfficialVerifyX(
    val desc: String,
    val type: Int
)

data class PendantXX(
    val expire: Int,
    val image: String,
    val image_enhance: String,
    val name: String,
    val pid: Int
)

data class UserSailingX(
    val cardbg: Any,
    val cardbg_with_focus: Any,
    val pendant: Any
)

data class VipXX(
    val accessStatus: Int,
    val dueRemark: String,
    val label: LabelX,
    val themeType: Int,
    val vipDueDate: Long,
    val vipStatus: Int,
    val vipStatusWarn: String,
    val vipType: Int
)

data class LabelX(
    val label_theme: String,
    val path: String,
    val text: String
)

data class UpActionX(
    val like: Boolean,
    val reply: Boolean
)

data class UpperX(
    val mid: Int
)