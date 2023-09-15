package com.msq.rest.entity

data class ServiceDetails(
    val created: Int,
    val id: Int,
    val image: String,
    val modified: Int,
    val name: String,
    val price: Double,
    val status: Int
)