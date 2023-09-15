package com.msq.rest.entity

data class GetReviewEntity(
    val `data`: GetReviewData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class GetReviewData(
    val pagination: Pagination,
    val ratings: List<GetReviewRating>
)

data class GetReviewRating(
    val bookingId: Int,
    val comment: String,
    val created: Int,
    val id: Int,
    val modified: Int,
    val providerInfo: ProviderInfo,
    val rating: Float,
    val serviceProviderId: Int,
    val type: Int,
    val userId: Int
)
