package com.buuzconnect.uis.addevent

import android.app.Activity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.msq.R
import com.msq.rest.RestService
import com.msq.rest.entity.GetAllMessageEntity
import com.msq.rest.entity.SendMessageEntity
import com.msq.util.ProgressDialogs
import com.msq.util.ext.errorMessage
import com.msq.util.ext.isInternetAvailable
import com.msq.util.ext.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChatBookViewModel(
    private val context: Activity,
    private val retrofitService: RestService,

    ) : AndroidViewModel(context.application) {

    val mSendMessageEntity = MutableLiveData<SendMessageEntity>()
    val mGetAllMessageEntity = MutableLiveData<GetAllMessageEntity>()


    fun getAllMessage(id: String) {
        if (context.isInternetAvailable()) {
//            ProgressDialogs.showLoading(context)
            retrofitService.getMessages(id).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
//                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mGetAllMessageEntity.value = response
                    } else {
                        context!!.toast(context.getString(R.string.something_went_wrong), false)
                    }
                }, { error ->
//                    ProgressDialogs.hideLoading()
                    if (error.message.equals("HTTP 404 Not Found"))
                        context!!.toast(context.errorMessage(error), false)
                    else {
                        context!!.toast(context.errorMessage(error), false)
                    }
                })
        } else context.toast("No Internet Connection!", false)
    }

    fun sendMessage(obj: JsonObject) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.sendMessage(obj).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mSendMessageEntity.value = response
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
