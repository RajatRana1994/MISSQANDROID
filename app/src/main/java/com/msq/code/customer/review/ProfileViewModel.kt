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

class ProfileViewModel(
    private val context: Activity,
    private val retrofitService: RestService,

    ) : AndroidViewModel(context.application) {

    val mGetReviewEntity = MutableLiveData<GetReviewEntity>()
    val mOrderPlacedEntity = MutableLiveData<OrderPlacedEntity>()



    fun getRating(bookingid:String,rating:String) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.getRating(bookingid,rating).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mGetReviewEntity.value = response
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
