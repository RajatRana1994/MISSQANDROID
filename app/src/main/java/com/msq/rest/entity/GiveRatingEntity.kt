package com.msq.rest.entity

data class GiveRatingEntity(
    val `data`: GiveRatingData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class GiveRatingData(
    val bookingId: String,
    val comment: String,
    val created: Int,
    val id: Int,
    val modified: Int,
    val rating: String,
    val serviceProviderId: Int,
    val type: Int,
    val userId: String
)