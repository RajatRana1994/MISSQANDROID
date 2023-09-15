package com.msq.rest.entity

data class PaymentEntity(
    val `data`: PaymentData,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class PaymentData(
    val paymongo: Paymongo
)

data class Paymongo(
    val `data`: PaymentPaymongoData
)

data class PaymentPaymongoData(
    val attributes: Attributes,
    val id: String,
    val type: String
)

data class Attributes(
    val amount: Int,
    val archived: Boolean,
    val checkout_url: String,
    val created_at: Int,
    val currency: String,
    val description: String,
    val fee: Int,
    val livemode: Boolean,
    val payments: List<Any>,
    val reference_number: String,
    val remarks: Any,
    val status: String,
    val tax_amount: Any,
    val taxes: List<Any>,
    val updated_at: Int
)