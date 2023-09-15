package com.msq.util

import android.app.Activity
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buuzconnect.uis.addevent.*
import com.msq.code.customer.payment.AddCardViewModel
import com.msq.rest.RestClient
import com.msq.rest.RestService

class ViewModelFactory(
    @param:NonNull @field:NonNull
    private var context: Activity, private val retrofitService: RestService
) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(context, retrofitService)
                isAssignableFrom(ServiceBookViewModel::class.java) -> ServiceBookViewModel(context, retrofitService)
                isAssignableFrom(CheckoutViewModel::class.java) -> CheckoutViewModel(context, retrofitService)
                isAssignableFrom(BookingViewModel::class.java) -> BookingViewModel(context, retrofitService)
                isAssignableFrom(BookingPROViewModel::class.java) -> BookingPROViewModel(context, retrofitService)
                isAssignableFrom(ChatBookViewModel::class.java) -> ChatBookViewModel(context, retrofitService)
                isAssignableFrom(ProfilePROViewModel::class.java) -> ProfilePROViewModel(context, retrofitService)
                isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(context, retrofitService)
                isAssignableFrom(ChatUserViewModel::class.java) -> ChatUserViewModel(context, retrofitService)
                isAssignableFrom(WalletViewModel::class.java) -> WalletViewModel(context, retrofitService)
                isAssignableFrom(AddCardViewModel::class.java) -> AddCardViewModel(context, retrofitService)
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T


    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(activity: Activity) =
            if (INSTANCE != null) {
                INSTANCE!!.context = activity

                INSTANCE
            } else synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(
                    activity,
                    RestClient().instance().create(RestService::class.java)
                ).also { INSTANCE = it }
            }
    }
}