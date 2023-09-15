package com.msq.code.customer.payment

import android.app.Activity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.customer.service.valetcarwash.models.stripe.CreateCardModel
import com.customer.service.valetcarwash.models.stripe.CreateCustomerEntity
import com.customer.service.valetcarwash.models.stripe.DeletedCardEntity
import com.customer.service.valetcarwash.models.stripe.GetCardsListModel
import com.google.gson.JsonObject
import com.msq.R
import com.msq.rest.RestClient
import com.msq.rest.RestService
import com.msq.rest.entity.TopupEntity
import com.msq.util.ErrorExtensions
import com.msq.util.ErrorExtensions.errorCode
import com.msq.util.ErrorExtensions.errorMessage
import com.msq.util.ProgressDialogs
import com.msq.util.ext.errorMessage
import com.msq.util.ext.isInternetAvailable
import com.msq.util.ext.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotifyData(var status: Int = 0, var message: String, var type: String = "")
class AddCardViewModel(
    private val context: Activity,
private val retrofitService: RestService,
) : AndroidViewModel(context.application) {
    var mCreateCardModel = MutableLiveData<CreateCardModel>()
    var mCreateCustomerEntity = MutableLiveData<CreateCustomerEntity>()
    var mUpdatedCustomerEntity = MutableLiveData<Pair<Int, CreateCustomerEntity>>()

    //    var mUpdatedCusOnServer = MutableLiveData<CustUpdatedProfileData>()
    var mGetCardsListModel = MutableLiveData<GetCardsListModel>()
    var mDeleteCardsModel = MutableLiveData<Pair<Int, DeletedCardEntity>>()
    var mErrorObserver = MutableLiveData<NotifyData>()
    var mStripePaymentDone = MutableLiveData<TopupEntity>()
//    val orderObserver = MutableLiveData<CheckoutModel>()
//    val mItemOrderPlacedEntity = MutableLiveData<ItemOrderPlacedEntity>()


    fun createCustomer(userid: String) {
        RestClient().stripeInstance().create(RestService::class.java)
            .createCustomer("created customer for $userid")
            .enqueue(object : Callback<CreateCustomerEntity> {
                override fun onFailure(call: Call<CreateCustomerEntity>, t: Throwable) {
                    mErrorObserver.value =
                        NotifyData(status = ErrorExtensions.errorCode(t),
                            message = ErrorExtensions.errorMessage(t))
                }

                override fun onResponse(
                    call: Call<CreateCustomerEntity>,
                    response: Response<CreateCustomerEntity>,
                ) {
                    if (response.isSuccessful) {
                        mCreateCustomerEntity.value = response.body()!!
                    } else {
                        mErrorObserver.value =
                            NotifyData(
                                response.code(),
                                ErrorExtensions.errorMessage1(response.errorBody()!!),
                                ""
                            )
                    }
                }

            })
    }

    fun editCustomer(user_token: String, data: HashMap<String, String>) {
//        RestClient().stripeInstance().create(RestService::class.java)
//            .updateCustomerProfile(data).enqueue(object : Callback<CustUpdatedProfileData> {
//                override fun onFailure(call: Call<CustUpdatedProfileData>, t: Throwable) {
//                    mErrorObserver.value = NotifyData(status = 500, message = t.message!!)
//                }
//
//                override fun onResponse(
//                    call: Call<CustUpdatedProfileData>,
//                    response: Response<CustUpdatedProfileData>
//                ) {
//                    if (response.isSuccessful)
//                        mUpdatedCusOnServer.value = response.body()!!
//                    else
//                        mErrorObserver.value =
//                            NotifyData(
//                                response.code(),
//                                ErrorExtensions.errorMessage1(response.errorBody()!!),
//                                ""
//                            )
//                }
//            })
    }

    fun createCard(stripeCustomer: String, token: String) {
        RestClient().stripeInstance().create(RestService::class.java)
            .createCard(stripeCustomer, token)
            .enqueue(object : Callback<CreateCardModel> {
                override fun onFailure(call: Call<CreateCardModel>, t: Throwable) {
                    mErrorObserver.value =
                        NotifyData(status = errorCode(t), message = errorMessage(t))
                }

                override fun onResponse(
                    call: Call<CreateCardModel>,
                    response: Response<CreateCardModel>,
                ) {
                    if (response.isSuccessful) {
                        mCreateCardModel.value = response.body()!!
                    } else {
                        mErrorObserver.value =
                            NotifyData(
                                response.code(),
                                ErrorExtensions.errorMessage1(response.errorBody()!!),
                                ""
                            )
                    }
                }

            })
    }

    fun getCards(stripeCustomer: String) {
        RestClient().stripeInstance().create(RestService::class.java).getCards(stripeCustomer)
            .enqueue(object : Callback<GetCardsListModel> {
                override fun onFailure(call: Call<GetCardsListModel>, t: Throwable) {
                    mErrorObserver.value =
                        NotifyData(status = errorCode(t), message = errorMessage(t))
                }

                override fun onResponse(
                    call: Call<GetCardsListModel>,
                    response: Response<GetCardsListModel>,
                ) {
                    if (response.isSuccessful) {
                        mGetCardsListModel.value = response.body()!!
                    } else {
                        mErrorObserver.value =
                            NotifyData(
                                response.code(),
                                ErrorExtensions.errorMessage1(response.errorBody()!!),
                                ""
                            )
                    }
                }

            })
    }

    fun getCustomer(stripeCustomer: String) {
        RestClient().stripeInstance().create(RestService::class.java)
            .getCustomerInfo(stripeCustomer)
            .enqueue(object : Callback<CreateCustomerEntity> {
                override fun onFailure(call: Call<CreateCustomerEntity>, t: Throwable) {
                    mErrorObserver.value =
                        NotifyData(status = errorCode(t), message = errorMessage(t))
                }

                override fun onResponse(
                    call: Call<CreateCustomerEntity>,
                    response: Response<CreateCustomerEntity>,
                ) {
                    if (response.isSuccessful) {
                        mCreateCustomerEntity.value = response.body()!!
                    } else {
                        mErrorObserver.value =
                            NotifyData(
                                response.code(),
                                ErrorExtensions.errorMessage1(response.errorBody()!!),
                                ""
                            )
                    }
                }

            })
    }

    fun updateCustomer(pos: Int, stripeCustomer: String, card_id: String) {
        RestClient().stripeInstance().create(RestService::class.java)
            .updateCustomer(stripeCustomer, card_id)
            .enqueue(object : Callback<CreateCustomerEntity> {
                override fun onFailure(call: Call<CreateCustomerEntity>, t: Throwable) {
                    mErrorObserver.value =
                        NotifyData(status = errorCode(t), message = errorMessage(t))
                }

                override fun onResponse(
                    call: Call<CreateCustomerEntity>,
                    response: Response<CreateCustomerEntity>,
                ) {
                    if (response.isSuccessful) {
                        mUpdatedCustomerEntity.value = Pair(pos, response.body()!!)
                    } else {
                        mErrorObserver.value =
                            NotifyData(
                                response.code(),
                                ErrorExtensions.errorMessage1(response.errorBody()!!),
                                ""
                            )
                    }
                }

            })
    }

    fun deleteCard(stripeCustomer: String, card_id: String, pos: Int) {
        RestClient().stripeInstance().create(RestService::class.java)
            .deleteCard(stripeCustomer, card_id)
            .enqueue(object : Callback<DeletedCardEntity> {
                override fun onFailure(call: Call<DeletedCardEntity>, t: Throwable) {
                    mErrorObserver.value =
                        NotifyData(status = errorCode(t), message = errorMessage(t))
                }

                override fun onResponse(
                    call: Call<DeletedCardEntity>,
                    response: Response<DeletedCardEntity>,
                ) {
                    if (response.isSuccessful) {
                        mDeleteCardsModel.value = Pair(pos, response.body()!!)
                    } else {
                        mErrorObserver.value =
                            NotifyData(
                                response.code(),
                                ErrorExtensions.errorMessage1(response.errorBody()!!),
                                ""
                            )
                    }
                }

            })
    }

    fun stripePaymentDone(amount: String,json: JsonObject,booking_id:String) {//D:\LiveProjects\1upPro\1UpProAppCode\1UpPro
        if (context.isInternetAvailable()) {
            ProgressDialogs.showLoading(context)
            retrofitService.stripePaymentDone(booking_id,JsonObject().also {
                it.addProperty("amount", amount.trim())
                it.add("paymentDetails", json)
            }).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ProgressDialogs.hideLoading()
                    if (response != null) {
                        mStripePaymentDone.value = response
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


    fun addNewCard(token: String, map: HashMap<String, String>) {
//        RestClient().stripeInstance().create(RestService::class.java).addNewCard(map)
//            .enqueue(object : Callback<WorkHourEntity> {
//                override fun onFailure(call: Call<WorkHourEntity>, t: Throwable) {
//                    mErrorObserver.value =
//                        NotifyData(status = errorCode(t), message = errorMessage(t))
//                }
//
//                override fun onResponse(
//                    call: Call<WorkHourEntity>,
//                    response: Response<WorkHourEntity>
//                ) {
//                    if (response.isSuccessful) {
////                        mAddedWorkHourEntity.value = Pair(day, response.body()!!)
//                    } else {
//                        mErrorObserver.value =
//                            NotifyData(
//                                response.code(),
//                                ErrorExtensions.errorMessage1(response.errorBody()!!),
//                                ""
//                            )
//                    }
//                }
//
//            })
    }

    fun paymentMethods(token: String) {
//        RetroInstance().withHeaders(token).create(HttpRequests::class.java).paymentMethods()
//            .enqueue(object : Callback<GetServiceEntity> {
//                override fun onFailure(call: Call<GetServiceEntity>, t: Throwable) {
//                    mErrorObserver.value =
//                        NotifyData(status = errorCode(t), message = errorMessage(t))
//                }
//
//                override fun onResponse(
//                    call: Call<GetServiceEntity>,
//                    response: Response<GetServiceEntity>
//                ) {
//                    if (response.isSuccessful) {
////                        mAddedWorkHourEntity.value = Pair(day, response.body()!!)
//                    } else {
//                        mErrorObserver.value =
//                            NotifyData(
//                                response.code(),
//                                ErrorExtensions.errorMessage1(response.errorBody()!!),
//                                ""
//                            )
//                    }
//                }
//
//            })
    }

//    fun placeOrder(data: JsonObject) {
//        retrofitService.doPayment(data).enqueue(
//            object : Callback<String> {
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    mErrorObserver.value = NotifyData(status = 500,message = t.message!!)
//                }
//
//                override fun onResponse(
//                    call: Call<String>,
//                    response: Response<String>
//                ) {
//                    if(response.isSuccessful)
//                    {
////                        orderObserver.value = response.body()
//                    }
//                    else
//                    {
//                        mErrorObserver.value =
//                            NotifyData(response.code(), ErrorExtensions.errorMessage1(response.errorBody()!!),"")
//                    }
//                }
//            })
//    }

    fun placeItemOrder(token: String, map: JsonObject) {
//        RetroInstance().withHeaders(token).create(HttpRequests::class.java).placeItemOrder(map)
//            .enqueue(object : Callback<ItemOrderPlacedEntity> {
//                override fun onFailure(call: Call<ItemOrderPlacedEntity>, t: Throwable) {
//                    mErrorObserver.value = NotifyData(status = 500, message = t.message!!)
//                }
//
//                override fun onResponse(
//                    call: Call<ItemOrderPlacedEntity>,
//                    response: Response<ItemOrderPlacedEntity>
//                ) {
//                    if (response.isSuccessful)
//                        mItemOrderPlacedEntity.value = response.body()
//                    else
//                        mErrorObserver.value = NotifyData(
//                            response.code(),
//                            ErrorExtensions.errorMessage1(response.errorBody()!!),
//                            ""
//                        )
//                }
//            })
    }
}

