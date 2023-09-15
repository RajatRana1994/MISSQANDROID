package com.msq.rest.entity

data class TopupEntity(
    val `data`: TopupData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class TopupData(
    val userBalance: Long
)



data class TransactionEntity(
    val `data`: TransactionData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class TransactionData(
    val balance: Double,
    val pagination: Pagination,
    val walletTransections: List<WalletTransection>
)

data class WalletTransection(
    val amount: Double,
    val bookDetails: Any,
    val bookingId: Int,
    val created: Int,
    val id: Int,
    val isCredit: Int,
    val modified: Int,
    val text: String,
    val transactionDetails: Any,
    val transectionType: Int,
    val userId: Int
)