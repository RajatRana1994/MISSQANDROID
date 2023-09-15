package com.buuzconnect.uis.addevent

import android.app.Activity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.msq.R
import com.msq.rest.RestService
import com.msq.rest.entity.*
import com.msq.util.ProgressDialogs
import com.msq.util.ext.errorMessage
import com.msq.util.ext.isInternetAvailable
import com.msq.util.ext.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody

class BookingViewModel(
    private val context: Activity,
    private val retrofitService: RestService,

    ) : AndroidViewModel(context.application) {

    val mStripeSecretEntity = MutableLiveData<Pair<String,StripeSecretEntity>>()
    val mPaymentEntity = MutableLiveData<Pair<String,PaymentEntity>>()
    val mBookingListEntity = MutableLiveData<BookingListEntity>()
    val mBookingDetailEntity = MutableLiveData<BookingDetailEntity>()



    fun getBookings(obj:HashMap<String,String>) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.getBookings(obj).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mBookingListEntity.value = response
                    } else {
                        context!!.toast(context.getString(R.string.something_went_wrong), false)
                    }
                }, { error ->
                    ProgressDialogs.hideLoading()
                    if (error.message.equals("HTTP 404 Not Found"))
                        context!!.toast(context.errorMessage(error), false)
                    else {
                        context!!.toast(context.errorMessage(error), false)
                    }
                })
        } else context.toast("No Internet Connection!", false)
    }

    fun bookingDetail(id:String) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.bookingDetail(id).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mBookingDetailEntity.value = response
                    } else {
                        context!!.toast(context.getString(R.string.something_went_wrong), false)
                    }
                }, { error ->
                    ProgressDialogs.hideLoading()
                    if (error.message.equals("HTTP 404 Not Found"))
                        context!!.toast(context.errorMessage(error), false)
                    else {
                        context!!.toast(context.errorMessage(error), false)
                    }
                })
        } else context.toast("No Internet Connection!", false)
    }

    fun createStripeSecret(amount: String, providerid: String,bookingId: String) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.doPayment(JsonObject().also {
                it.addProperty("amount", amount.trim())
                it.addProperty("stripeId", providerid)
            }).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mStripeSecretEntity.value = Pair(bookingId,response)
                    } else {
                        context!!.toast(context.getString(R.string.something_went_wrong), false)
                    }
                }, { error ->
                    ProgressDialogs.hideLoading()
                    if (error.message.equals("HTTP 404 Not Found"))
                        context!!.toast(context.errorMessage(error), false)
                    else {
                        context!!.toast(context.errorMessage(error), false)
                    }
                })
        } else context.toast("No Internet Connection!", false)
    }

    fun paymentIntentPaymongo(amount: String, userId: String,bookingId: String) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.paymentIntentPaymongo(JsonObject().also {
                it.addProperty("amount", amount.trim())
                it.addProperty("userId", userId)
                it.addProperty("bookingId", bookingId)
            }).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mPaymentEntity.value = Pair(bookingId,response)
                    } else {
                        context!!.toast(context.getString(R.string.something_went_wrong), false)
                    }
                }, { error ->
                    ProgressDialogs.hideLoading()
                    if (error.message.equals("HTTP 404 Not Found"))
                        context!!.toast(context.errorMessage(error), false)
                    else {
                        context!!.toast(context.errorMessage(error), false)
                    }
                })
        } else context.toast("No Internet Connection!", false)
    }

}
