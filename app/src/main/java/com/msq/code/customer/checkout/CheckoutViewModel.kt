package com.buuzconnect.uis.addevent

import android.app.Activity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.msq.R
import com.msq.rest.RestService
import com.msq.rest.entity.FindProviderEntity
import com.msq.rest.entity.GetProfileInfoEntity
import com.msq.rest.entity.OrderPlacedEntity
import com.msq.util.ProgressDialogs
import com.msq.util.ext.errorMessage
import com.msq.util.ext.isInternetAvailable
import com.msq.util.ext.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CheckoutViewModel(
    private val context: Activity,
    private val retrofitService: RestService,

    ) : AndroidViewModel(context.application) {

    val mGetProfileInfoEntity = MutableLiveData<GetProfileInfoEntity>()
    val mOrderPlacedEntity = MutableLiveData<OrderPlacedEntity>()
    val mFindProviderEntity = MutableLiveData<FindProviderEntity>()


    fun placeOrder(order: JsonObject) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.bookServices(order).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mOrderPlacedEntity.value = response
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

    fun createStripeSecret(amount: String, providerid: String = "acct_1KaclZ4NDGC303V6") {
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
//                        mOrderPlacedEntity.value = response
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


    fun getProfileInfo(id: String) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.getProfile(id).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mGetProfileInfoEntity.value = response
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
    fun findProvider(id: String,distance:String) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.findProvider(id,distance).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mFindProviderEntity.value = response
                    } else {
                        context!!.toast(context.getString(R.string.something_went_wrong), false)
                    }
                }, { error ->
                    ProgressDialogs.hideLoading()
                    if (error.message.equals("HTTP 404 Not Found"))
                        mFindProviderEntity.value = FindProviderEntity(null,"No provider found",404,false,)
//                        context!!.toast(context.errorMessage(error), false)
                    else {
                        context!!.toast(context.errorMessage(error), false)
                    }
                })
        } else context.toast("No Internet Connection!", false)
    }


}
