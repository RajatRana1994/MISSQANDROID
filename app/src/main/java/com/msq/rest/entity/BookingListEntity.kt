package com.msq.rest.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class BookingListEntity(
    val `data`: BookingListData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class BookingListData(
    val bookings: List<BookingListParseData>,
    val pagination: Pagination
)

data class BookingListParseData(
    val alreadyPaidAmount: Int,
    val bookingDate: Long,
    val bookingEndTime: Long,
    val bookingStartTime: Long,
    val created: Int,
    val id: Int,
    val isAccept: Int,
    val isContactedClient: Int,
    val isPaymentDone: Int,
    val modified: Int,
    val orderReward: Int,
    val paymentDetails: Any,
    val paymentType: Int,
    val serviceDetails: ServiceDetails,
    val serviceId: Int,
    val serviceProofPic1: String,
    val serviceProofPic2: String,
    val serviceProviderId: Int,
    val serviceProviderLocation: ServiceProviderLocation?,
    val setup: Int,
    val status: Int,
    val totalPrice: Double,
    val userId: Int,
    val userInfo: UserInfo,
    val providerInfo: ProviderInfo
)

data class ProviderInfo(
    val providerName: String?,
    val providerEmail: String,
    val providerStripeAccountId: String,
    val providerPhone: String,
    val providerProfile: String,
)

data class ServiceProviderLocation(
    val address: String?,
    val city: String,
    val latitude: String,
    val longitude: String,
    val postCode: String,
    val state: String
)
