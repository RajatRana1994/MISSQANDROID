package com.msq.rest.entity

data class ConnectEntity(
    val `data`: ConnectData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class ConnectData(
    val accountLink: AccountLink
)

data class AccountLink(
    val created: Int,
    val expires_at: Int,
    val `object`: String,
    val url: String
)