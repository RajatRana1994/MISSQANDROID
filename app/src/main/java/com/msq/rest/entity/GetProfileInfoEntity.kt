package com.msq.rest.entity

data class GetProfileInfoEntity(
    val `data`: AuthData,
    val message: String,
    val status: Int,
    val success: Boolean
)


data class RatingStar(
    val rating1:Float?,
    val rating2:Float?,
    val rating3:Float?,
    val rating4:Float?,
    val rating5:Float?,
)