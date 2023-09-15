package com.msq.rest

import com.msq.Parser.AllAPIS
import com.msq.base.MSQApp.Companion.sessionManager
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RestClient {
    var stripeIns: Retrofit? = null

    fun instance(): Retrofit {
        val retrofitSerice = Retrofit.Builder()
            .baseUrl(AllAPIS.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(unsafeOkHttpClient)
            .build()
        return retrofitSerice
    }

    val unsafeOkHttpClient: OkHttpClient
        get() {
            try {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                val httpClient = OkHttpClient.Builder()
                httpClient.interceptors().add(getHeadersForApis())
                httpClient.interceptors().add(httpLoggingInterceptor)
                httpClient.readTimeout(90, TimeUnit.SECONDS)
                httpClient.connectTimeout(90, TimeUnit.SECONDS)
                return httpClient.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

    private fun getHeadersForApis(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (sessionManager!!.getPrefIsLogin(SessionManager.IS_LOGIN)) {
                request = request.newBuilder()
                    .addHeader(
                        "Authorization",
                        "${sessionManager!!.getPrefString(SessionManager.USER_TOKEN)}"
                    )
                    .build()
            }
            chain.proceed(request)
        }
    }

    fun stripeInstance(): Retrofit {
        if (stripeIns == null) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .addHeader("Authorization", "Bearer ${ConstUtils.SECRET_KEY}")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            httpClient.interceptors().add(httpLoggingInterceptor)
            httpClient.readTimeout(60, TimeUnit.SECONDS)
            httpClient.connectTimeout(60, TimeUnit.SECONDS)

            val client = httpClient.build()
            stripeIns = Retrofit.Builder()
                .baseUrl(AllAPIS.STRIPE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
            return stripeIns as Retrofit
        } else
            return stripeIns as Retrofit
    }



}