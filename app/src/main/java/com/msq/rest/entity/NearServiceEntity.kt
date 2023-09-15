package com.msq.rest.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class NearServiceEntity(
    val `data`: NearServiceData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class NearServiceData(
    val pagination: Pagination,
    val result: List<NearServiceResult>
)

@Parcelize
data class NearServiceResult(
    val created: Int,
    val id: Int,
    val image: String,
    val modified: Int,
    val name: String,
    val price: Double,
    val providerEmail: String,
    val providerId: Int,
    val providerImage: String,
    val providerLocation: String,
    val rating: Double?=0.0,
    val providerName: String,
    val providerPhone: String,
    val status: Int,
    val totalDistance: Int
):Parcelable