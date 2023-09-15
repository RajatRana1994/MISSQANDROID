package com.msq.rest.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class AuthEntity(
    val `data`: AuthData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class AuthData(
    val address: String,
    val authorizationKey: String,
    val balance: Float,
    val dob: Int,
    val isOnDuty: Int?,
    val email: String,
    val id: Int,
    val name: String,
    val nbi: String,
    val primaryId: String,
    val phone: String,
    val profile: String,
    val ratingStars: RatingStar,
    val rating: Float?,
    val services: List<MSQService>,
    val status: Int,
    var stripeId: String,
    var stripe_connect: Int,
    val userType: String
)

@Parcelize
data class MSQService(
    val created: Int,
    val id: Int,
    val image: String,
    val modified: Int,
    val name: String,
    val price: Double,
    val status: Int,
    var selected: Boolean = false,
):Parcelable
