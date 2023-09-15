package com.msq.rest.entity

data class UserInfo(
    val latitude: Double,
    val longitude: Double,
    val userAddress: String,
    val userCity: String,
    val userEmail: String,
    val userName: String,
    val userPhone: String,
    val userPostCode: String,
    val userProfile: String,
    val userState: String
)