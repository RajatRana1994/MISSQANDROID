package com.customer.service.valetcarwash.models.stripe


data class CreateCardModel(
    val id: String,
    val objects: String,
    val address_city: Any,
    val address_country: Any,
    val address_line1: Any,
    val address_line1_check: Any,
    val address_line2: Any,
    val address_state: Any,
    val address_zip: Any,
    val address_zip_check: Any,
    val brand: String,
    val country: String,
    val customer: String,
    val cvc_check: Any,
    val dynamic_last4: Any,
    val exp_month: Int,
    val exp_year: Int,
    val fingerprint: String,
    val funding: String,
    val last4: String,
    val name: Any,
    val tokenization_method: Any
)
