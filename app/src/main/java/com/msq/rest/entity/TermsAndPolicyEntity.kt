package com.msq.rest.entity

data class TermsAndPolicyEntity(
    val `data`: List<TermsAndPolicyData>,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class TermsAndPolicyData(
    val created: Int,
    val id: Int,
    val key_pair: String,
    val modified: Int,
    val value: String
)