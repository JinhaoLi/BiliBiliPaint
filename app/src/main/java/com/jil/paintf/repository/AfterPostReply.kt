package com.jil.paintf.repository

/**==============================
 *===============================
 *== @Date: 2020/7/8 20:41
 *== @author JIL
 *===============================
 *===============================
 **/

data class AfterReplyResult(
    val code: Int,
    val `data`: DataXXXXX,
    val message: String,
    val ttl: Int
)

data class DataXXXXX(
    val dialog: Long,
    val dialog_str: String,
    val need_captcha: Boolean,
    val parent: Long,
    val parent_str: String,
    val reply: Reply,
    val root: Long,
    val root_str: String,
    val rpid: Long,
    val rpid_str: String,
    val success_action: Int,
    val success_toast: String,
    val url: String
)
