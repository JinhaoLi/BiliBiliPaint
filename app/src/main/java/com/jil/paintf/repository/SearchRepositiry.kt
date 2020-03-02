package com.jil.paintf.repository

data class SearchRepository(
    val code: Int,
    val `data`: SearchData,
    val message: String,
    val ttl: Int
)

data class SearchData(
    val cost_time: CostTime,
    val egg_hit: Int,
    val exp_list: ExpList,
    val numPages: Int,
    val numResults: Int,
    val page: Int,
    val pagesize: Int,
    val result: List<Result>,
    val rqt_type: String,
    val seid: String,
    val show_column: Int,
    val suggest_keyword: String
)

data class CostTime(
    val as_request: String,
    val as_request_format: String,
    val as_response_format: String,
    val deserialize_response: String,
    val illegal_handler: String,
    val main_handler: String,
    val params_check: String,
    val save_cache: String,
    val total: String
)

data class ExpList(
    val `6620`: Boolean
)

data class Result(
    val count: Int,
    val cover: String,
    val hit_columns: List<String>,
    val id: Int,
    val like: Int,
    val mid: Int,
    val rank_index: Int,
    val rank_offset: Int,
    val rank_score: Int,
    val title: String,
    val type: String,
    val uname: String,
    val view: Int
)