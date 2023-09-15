package com.buuzconnect.uis.addevent

import android.app.Activity
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.msq.R
import com.msq.rest.RestClient
import com.msq.rest.RestService
import com.msq.rest.entity.*
import com.msq.util.ProgressDialogs
import com.msq.util.ext.errorMessage
import com.msq.util.ext.isInternetAvailable
import com.msq.util.ext.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody

class ProfilePROViewModel(
    private val context: Activity,
    private val retrofitService: RestService,

    ) : AndroidViewModel(context.application) {

    val mServiceListEntity = MutableLiveData<ServiceListEntity>()
    val mConnectEntity = MutableLiveData<ConnectEntity>()
    val mUpdateProfileInfoEntity = MutableLiveData<GetProfileInfoEntity>()
    val mPasswordChanged = MutableLiveData<CommonEntity>()
    val mEmpSurveyNotify = MutableLiveData<CommonEntity>()
    val mTermsAndPolicyEntity = MutableLiveData<TermsAndPolicyEntity>()
    val mNotificationEntity = MutableLiveData<MutableList<Notification>>()
    val mBookingDetailEntity = MutableLiveData<BookingDetailEntity>()

    fun stripeConnect() {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.stripeLinkAccount().subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mConnectEntity.value = response
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
    fun getServices() {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.services().subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mServiceListEntity.value = response
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

    fun notification(page:String) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.notification(page).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mNotificationEntity.value = response.data.notifications
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

    fun updateProfile(obj: JsonObject) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.updateProfile(obj).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mUpdateProfileInfoEntity.value = response
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
    fun updateProfileWithImg(json: HashMap<String, RequestBody>) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.updateProfileWithImg(json).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mUpdateProfileInfoEntity.value = response
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

    fun changePassword(obj: JsonObject) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.chnagePassword(obj).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mPasswordChanged.value = response
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

    fun employeeSurvey(obj: JsonObject) {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.employeeSurvey(obj).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mEmpSurveyNotify.value = response
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
    fun getAppInfo() {
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.getAppInfo().subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mTermsAndPolicyEntity.value = response
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
            retrofitService.bookingDetail(id).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response != null) {
                        mBookingDetailEntity.value = response
                    } else {
                        /*context!!.toast(context.getString(R.string.something_went_wrong), false)*/
                    }
                }, { error ->
                    Log.e("Error", context.errorMessage(error))
                    /*if (error.message.equals("HTTP 404 Not Found"))
                        context!!.toast(context.errorMessage(error), false)
                    else {
                        context!!.toast(context.errorMessage(error), false)
                    }*/
                })
        } else context.toast("No Internet Connection!", false)
    }


}
