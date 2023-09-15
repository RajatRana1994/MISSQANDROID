package com.msq.util

import com.google.gson.Gson
import com.msq.code.customer.payment.NotifyData
import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.HttpException
import java.nio.charset.Charset

object ErrorExtensions {
    fun errorMessage(error: Any): String {
        try {
            if (error is Throwable) {
                val source = (error as HttpException).response()!!.errorBody()!!.source()
                source.request(Long.MAX_VALUE) // request the entire body.
                val buffer = source.buffer()
// clone buffer before reading from
                val responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"))
                val gson = Gson()
                val err = gson.fromJson<NotifyData>(responseBodyString, NotifyData::class.java)
                return err.message.replace("_", " ")
            } else if (error is BufferedSource) {
                error.request(Long.MAX_VALUE) // request the entire body.
                val buffer = error.buffer()
// clone buffer before reading from
                val responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"))
                val gson = Gson()
                val err = gson.fromJson<NotifyData>(responseBodyString, NotifyData::class.java)
//                if(err.status == 419 || err.status == 401)
//                {
//                    ProviderHomeActivity.blockedUserObserver.value = err
//                }
//                if(err.status == 401 || err.status == 420)
//                {
//                    if(SharedPref(this.getapp).getBool(AppConstants.IS_CUSTOMER))
//                    HomeActivity.blockedUserObserver.value = err
//                }
                return err.message.replace("_", " ")
            } else {
                return "Something went wrong"
            }
        } catch (e: Exception) {
            return "Something went wrong"
        }
    }
    fun errorCode(error: Any): Int {
        try {
            if (error is Throwable) {
                val source = (error as HttpException).response()!!.errorBody()!!.source()
                source.request(Long.MAX_VALUE) // request the entire body.
                val buffer = source.buffer()
// clone buffer before reading from
                val responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"))
                val gson = Gson()
                val err = gson.fromJson<NotifyData>(responseBodyString, NotifyData::class.java)
                return err.status
            } else if (error is BufferedSource) {
                error.request(Long.MAX_VALUE) // request the entire body.
                val buffer = error.buffer()
// clone buffer before reading from
                val responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"))
                val gson = Gson()
                val err = gson.fromJson<NotifyData>(responseBodyString, NotifyData::class.java)
                return err.status
            } else {
                return 500
            }
        } catch (e: Exception) {
            return 500
        }
    }
    fun errorMessage1(response: ResponseBody): String {
        try {
            val source = response.source()
            source.request(Long.MAX_VALUE)// request the entire body.
            val buffer = source.buffer()
// clone buffer before reading from
            val responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"))
            val gson = Gson()
            val err = gson.fromJson<NotifyData>(responseBodyString, NotifyData::class.java)
//            if(err.status == 419 || err.status == 401)
//            {
//                ProviderHomeActivity.blockedUserObserver.value = err
//            }
//            if(err.status == 401 || err.status == 420)
//            {
//                //if(SharedPref(this.getapp).getBool(AppConstants.IS_CUSTOMER))
//                HomeActivity.blockedUserObserver.value = err
//            }
            return err.message.replace("_", " ")
        } catch (e: Exception) {
            return("Server Error")
        }
    }

}
