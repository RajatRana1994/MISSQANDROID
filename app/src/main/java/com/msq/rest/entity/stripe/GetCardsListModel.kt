package com.customer.service.valetcarwash.models.stripe


data class GetCardsListModel(
        val objects: String,
        val data: List<CardsListData>,
        val has_more: Boolean,
        val url: String
)

data class CardsListData(
        val id: String,
        val objects: String,
        var default: Boolean=false,
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
        val cvc_check: String,
        val dynamic_last4: Any,
        val exp_month: Int,
        val exp_year: Int,
        val fingerprint: String,
        val funding: String,
        val last4: String,
        val name: Any,
        val tokenization_method: Any,
        var isCheck: Boolean

)


data class DeletedCardEntity(
    val deleted: Boolean,
    val id: String,
    val `object`: String
)