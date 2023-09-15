package com.msq.rest.entity

data class ChatUserListEntity(
    val `data`: List<ChatUserListData>,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class ChatUserListData(
    val created: Int,
    val friendInfo: FriendInfo,
    val id: Int,
    val is_read: Int,
    val message: String,
    val message_type: Int,
    val modified: Int,
    val receiver_id: Int,
    val sender_id: Int,
    val thread_id: Int,
    val bookingId: Int,
    val un_read_message: Int,
    val videoLength: String
)
