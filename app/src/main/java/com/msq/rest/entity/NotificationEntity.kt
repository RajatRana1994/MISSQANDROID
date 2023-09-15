package com.msq.rest.entity

data class NotificationEntity(
    val `data`: NotificationData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class NotificationData(
    val notifications: ArrayList<Notification>,
    val pagination: Pagination
)

data class Notification(
    val bookingId: Int,
    val created: Int,
    val id: Int,
    val isRead: Int,
    val modified: Int,
    val serviceProviderId: Int,
    val notificationCode: Int,
    val text: String,
    val userId: Int
)