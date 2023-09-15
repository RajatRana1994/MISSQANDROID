package com.msq.rest.entity

data class SendMessageEntity(
    val `data`: MessageData,
    val message: String,
    val status: Int,
    val success: Boolean
)
data class GetAllMessageEntity(
    val `data`: ArrayList<MessageData>,
    val message: String,
    val status: Int,
    val success: Boolean
)
data class MessageData(
    val bookingId: String,
    val created: Int,
    val friend_id: String,
    val id: Int,
    val message: String,
    val message_type: String,
    val modified: Int,
    val receiver_id: String,
    val sender_id: Int,
    val text: String,
    val thread_id: Int,
    val user_id: Int,
    val friendInfo: FriendInfo,
    val user_info: FriendInfo,
    val videoLength: String
)


data class FriendInfo(
    val address: String,
    val authorizationKey: String,
    val balance: Int,
    val dob: Int,
    val email: String,
    val id: Int,
    val isOnDuty: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val otp: Int,
    val password: String,
    val phone: String,
    val profile: String,
    val status: Int,
    val stripeId: String,
    val stripe_connect: Int,
    val userType: Int
)
