package com.msq.rest.entity

data class ServiceListEntity(
    val `data`: Data,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class Data(
    val pagination: Pagination,
    val result: List<MSQService>
)

data class Pagination(
    val currentPage: Int,
    val limit: Int,
    val totalPage: Int,
    val totalRecord: Int
)


data class CommonEntity(
    val `data`: Any,
    val message: String,
    val status: Int,
    val success: Boolean
)
