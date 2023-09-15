package com.msq.rest.entity

data class BookingSetupDetailEntity(
    val `data`: BookingSetupDetail,
    val message: String,
    val status: Int,
    val success: Boolean,
)


data class BookingSetupDetail(val bookingDetails: BookingDetailData)
data class BookingDetailEntity(
    val `data`: BookingDetailData,
    val message: String,
    val status: Int,
    val success: Boolean,
)

data class BookingDetailData(
    val alreadyPaidAmount: Double?,
    val bookingDate: Long,
    val bookingEndTime: Long,
    val bookingStartTime: Long,
    val created: Long,
    val id: Int,
    val isRated: Int,
    val isAccept: Int,
    val isContactedClient: Int,
    val isPaymentDone: Int,
    val modified: Int,
    val orderReward: Double?,
    val paymentDetails: Any,
    val paymentType: Int,
    val serviceDetails: ServiceDetails,
    val serviceId: Int,
    val serviceProofPic1: String,
    val serviceProofPic2: String,
    val duration: Int,
    val serviceProviderId: Int,
    val serviceProviderLocation: ServiceProviderLocation?,
    val setup: Int,
    val status: Int,
    val totalPrice: Double,
    val userId: Int,
    val userInfo: UserInfo,
    val providerInfo: ProviderInfo,
)