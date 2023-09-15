package com.customer.service.valetcarwash.models.stripe

data class CreateCustomerEntity(
    val address: Any,
    val balance: Int,
    val created: Int,
    val currency: String,
    val default_source: String,
    val delinquent: Boolean,
    val description: String,
    val discount: Any,
    val email: String,
    val id: String,
    val invoice_prefix: String,
    val invoice_settings: InvoiceSettings,
    val livemode: Boolean,
    val metadata: Metadata,
    val name: String,
    val next_invoice_sequence: Int,
    val `object`: String,
    val phone: Any,
    val preferred_locales: List<Any>,
    val shipping: Any,
    val tax_exempt: String
)

data class InvoiceSettings(
    val custom_fields: Any,
    val default_payment_method: Any,
    val footer: Any
)

class Metadata(
)


