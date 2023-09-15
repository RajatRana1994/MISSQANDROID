package com.msq.util

import android.content.Context
import com.google.gson.Gson
import com.msq.R
import com.msq.rest.entity.AuthData

class SessionManager(val context: Context) {
    companion object {
        val USER_ID = "USER_ID"
        val PAYOUT_ID = "PAYOUT_ID"
        val IS_LOGIN = "IS_LOGIN"
        val CUSTOMER_CODE = "CUSTOMER_CODE"
        val STYLIST_CODE = "STYLIST_CODE"
        val SAW_APPROVED = "SAW_APPROVED"
        val USER_TOKEN = "USER_TOKEN"
        val DEVICE_TOKEN = "DEVICE_TOKEN"
        val USER_NAME = "USER_NAME"
        val USER_TYPE = "USER_TYPE"
        val USER_PROFILE = "USER_PROFILE"
        val USER_DATA = "USER_DATA"
        val EVENTS_DRAFT = "EVENTS_DRAFT"
        val DISABLED = "DISABLED"
        val BALANCE = "BALANCE"
        val LAT = "LAT"
        val STRIPE_CUS_ID = "STRIPE_CUS_ID"
        val STRIPE_DEFAULT_CARD = "STRIPE_DEFAULT_CARD"
        val LNG = "LNG"
        val LANGUAGE = "LANGUAGE"

        val phone = "phone"
        val phoneVerified = "phoneVerified"
        val email = "email"
        val fname = "fname"
        val mname = "mname"
        val lname = "lname"
        val emailVerified = "emailVerified"
        val password = "password"
        val gender = "gender"
        val dob = "dob"
        val serviceCat = "serviceCat"
        val specialization = "specialization"
        val experience = "experience"
        val address1 = "address1"
        val address2 = "address2"
        val city = "city"
        val state = "state"
        val zip = "zip"
        val gps = "gps"
        val ssn = "ssn"
        val cosmetologyNum = "cosmetologyNum"
        val cosmetologyImage = "cosmetologyImage"
        val drivingNum = "drivingNum"
        val drivingImage = "drivingImage"
        val profileImage = "profileImage"
        val contractor = "contractor"
        val liability_waiver = "liability_waiver"
        val privacy = "privacy"
        val terms = "terms"


    }

    val pref =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun savePrefFloat(key: String, value: Float) {
        pref.edit().putFloat(key, value).apply()
    }

    fun getPrefFloat(key: String) = pref.getFloat(key, 0f)

    fun savePrefInt(key: String, value: Int) {
        pref.edit().putInt(key, value).apply()
    }

    fun getPrefInt(key: String) = pref.getInt(key, 0)


    fun savePrefString(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }

    fun getPrefString(key: String) = pref.getString(key, getdefaultValue(key))

    private fun getdefaultValue(key: String) = if (key == USER_DATA) "" else if (key == LAT||key == LNG) "0" else ""

    fun savePrefBool(key: String, value: Boolean) {
        pref.edit().putBoolean(key, value).apply()
    }

    fun getPrefBool(key: String) = pref.getBoolean(key, false)

    fun savePrefIsLogin(key: String, value: Boolean) {
        pref.edit().putBoolean(key, value).apply()
    }


    fun removeKey(key: String) {
        pref.edit().remove(key).apply()
    }


    fun getPrefIsLogin(key: String) = pref.getBoolean(key, false)

    fun clearPrefs() {
        val editor = pref.edit()
        editor.remove(USER_ID)
        editor.remove(PAYOUT_ID)
        editor.remove(SAW_APPROVED)
        editor.remove(DISABLED)
        editor.remove(USER_TYPE)
        editor.remove(IS_LOGIN)
        editor.remove(USER_TOKEN)
        editor.remove(CUSTOMER_CODE)
        editor.remove(STYLIST_CODE)
        editor.remove(USER_NAME)
        editor.remove(USER_PROFILE)
        editor.remove(EVENTS_DRAFT)
        editor.remove(USER_DATA)
        editor.apply()
    }

    fun getUserData(): AuthData? {
        try {
            val gson = Gson()
            return gson.fromJson(
                getPrefString(USER_DATA),
                AuthData::class.java
            )
        } catch (e: Exception) {
            return null
        }
    }


}