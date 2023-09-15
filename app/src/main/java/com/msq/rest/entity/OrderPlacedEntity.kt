package com.msq.rest.entity

data class OrderPlacedEntity(
    val `data`: OrderPlacedData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class OrderPlacedData(
    val bookingDate: String,
    val bookingEndTime: String,
    val bookingStartTime: String,
    val created: Int,
    val duration: String,
    val id: Int,
    val distance: String,
    val modified: Int,
    val paymentType: String,
    val serviceDetails: MSQService,
    val serviceId: Int,
    val serviceProviderId: Int,
    val totalPrice: Int,
    val userId: Int
)



data class FindProviderEntity(
    val `data`: FindProviderData?,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class FindProviderData(
    val bookingId: String,
    val distance: String,
    val id: String,
    val serviceDetails: FindProviderServiceDetails,
    val timeFindNextProvider: Int
)

data class FindProviderServiceDetails(
    val created: Int,
    val id: Int,
    val image: String,
    val modified: Int,
    val name: String,
    val price: Int,
    val status: Int
)