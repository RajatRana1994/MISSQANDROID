package com.msq.rest

import com.customer.service.valetcarwash.models.stripe.CreateCardModel
import com.customer.service.valetcarwash.models.stripe.CreateCustomerEntity
import com.customer.service.valetcarwash.models.stripe.DeletedCardEntity
import com.customer.service.valetcarwash.models.stripe.GetCardsListModel
import com.google.gson.JsonObject
import com.msq.Parser.AllAPIS
import com.msq.rest.entity.*
import io.reactivex.Observable
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface
RestService {

    @FormUrlEncoded
    @POST(AllAPIS.USERLOGIN)
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("deviceType") deviceType: String,
        @Field("deviceToken") deviceToken: String,
    ): Observable<AuthEntity>

    @Multipart
    @POST(AllAPIS.USER_SIGNUP)
    fun signup(
        @PartMap map: HashMap<String, RequestBody>,
    ): Observable<AuthEntity>

    @GET("${AllAPIS.PROFILE}/{id}")
    fun getProfile(
        @Path("id") id: String,
    ): Observable<GetProfileInfoEntity>

    @POST(AllAPIS.PROFILE)
    fun updateProfile(@Body jsonObj: JsonObject): Observable<GetProfileInfoEntity>

    @Multipart
    @POST(AllAPIS.PROFILE)
    fun updateProfileWithImg(
        @PartMap map: HashMap<String, RequestBody>,
    ): Observable<GetProfileInfoEntity>

    @POST(AllAPIS.CHANGEPASSWORD)
    fun chnagePassword(@Body jsonObj: JsonObject): Observable<CommonEntity>

    @POST(AllAPIS.APP_SERVEY)
    fun employeeSurvey(@Body jsonObj: JsonObject): Observable<CommonEntity>

    @POST(AllAPIS.VERIFY_OTP)
    fun verifyOtp(@Body jsonObj: JsonObject): Observable<String>

    @POST(AllAPIS.SOCIALLOGIN)
    fun socialLogin(@Body jsonObj: JsonObject): Observable<AuthEntity>

    @GET(AllAPIS.PROVIDER_SERVICES)
    fun getNearServices(
        @Query("page") page: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("limit") limit: String = "200",
//        @Query("distance") distance: String = "10000",
    ): Observable<NearServiceEntity>

    @GET(AllAPIS.SERVICES)
    fun services(@Body jsonObj: JsonObject): Observable<ServiceListEntity>

    @GET(AllAPIS.SERVICES)
    fun services(): Observable<ServiceListEntity>

    @POST(AllAPIS.BOOK_SERVICES)
    fun bookServices(@Body jsonObj: JsonObject): Observable<OrderPlacedEntity>

    @PUT("${AllAPIS.ACCEPT_REJECT_BOOKING}/{id}")//1-> accept 2-> reject
    fun acceptRejectService(
        @Path("id") id: String,
        @Body jsonObj: JsonObject,
    ): Observable<CommonEntity>

    @PUT("${AllAPIS.BOOKING_SETUP}/{bookingid}/{step}")
    fun bookingSetup(
        @Path("bookingid") bookingid: String,
        @Path("step") step: String,
        @Body jsonObj: JsonObject,
    ): Observable<BookingSetupDetailEntity>

    @Multipart
    @PUT("${AllAPIS.BOOKING_SETUP}/{bookingid}/{step}")
    fun bookingSetup(
        @Path("bookingid") bookingid: String,
        @Path("step") step: String,
        @PartMap map: HashMap<String, RequestBody>,
    ): Observable<BookingSetupDetailEntity>


    @POST("${AllAPIS.GIVE_RATING}/{id}/{rating}")
    fun giveRating(
        @Path("id") bookingid: String,
        @Path("rating") rating: String,
        @Body jsonObj: JsonObject,
    ): Observable<GiveRatingEntity>

    @GET("${AllAPIS.GIVE_RATING}/{id}/{rating}")
    fun getRating(
        @Path("id") bookingid: String,
        @Path("rating") rating: String,
    ): Observable<GetReviewEntity>

    @GET(AllAPIS.NOTIFICATION)
    fun notification(
        @Query("page") page: String,
        @Query("limit") limit: String = "200",
    ): Observable<NotificationEntity>

    @GET(AllAPIS.GET_BOOKINGS)
    fun getBookings(@QueryMap jsonObj: HashMap<String, String>): Observable<BookingListEntity>

    @GET("${AllAPIS.BOOKING_DETAIL}/{id}")
    fun bookingDetail(@Path("id") id: String): Observable<BookingDetailEntity>

    @POST(AllAPIS.SEND_MESSAGE)
    fun sendMessage(@Body jsonObj: JsonObject): Observable<SendMessageEntity>

    @POST(AllAPIS.PAYMENT_INTENT_PAYMONGO)
    fun paymentIntentPaymongo(@Body jsonObj: JsonObject): Observable<PaymentEntity>

    @POST(AllAPIS.ADD_TOPUP_PAYMONGO)
    fun topupPaymongo(@Body jsonObj: JsonObject): Observable<PaymentEntity>

    @GET(AllAPIS.LAST_CHAT)
    fun lastChat(): Observable<ChatUserListEntity>

    @GET("${AllAPIS.GET_MESSAGES}/{id}")
    fun getMessages(
        @Path("id") id: String,
    ): Observable<GetAllMessageEntity>

    @PATCH("${AllAPIS.READ_MESSAGE}/{id}")
    fun readMessages(
        @Path("id") id: String,
    ): Observable<String>

    @DELETE(AllAPIS.DELETE_SINGLE_MESSAGE)
    fun deleteSingleMessages(
        @Path("chat_id") chat_id: String,
    ): Observable<String>

    @DELETE(AllAPIS.DELETE_MESSAGES)
    fun deleteMessages(
        @Path("thread_id") thread_id: String,
    ): Observable<String>

    @POST(AllAPIS.DO_PAYMENT)
    fun doPayment(
        @Body jsonObj: JsonObject,
    ): Observable<StripeSecretEntity>

    @POST(AllAPIS.CREATE_PAYMENT_CHARGE)
    fun createPaymentCharge(
        @Body jsonObj: JsonObject,
    ): Observable<StripeSecretEntity>

    @GET(AllAPIS.STRIPE_CONNECT)
    fun stripeConnect(): Observable<String>

    @POST(AllAPIS.RELEASE_PAYMENT)
    fun releasePayment(
        @Body jsonObj: JsonObject,
    ): Observable<String>

    @POST("${AllAPIS.STRIPE_PAYMENT_DONE}/{id}")
    fun stripePaymentDone(
        @Path("id") id: String,   @Body jsonObj: JsonObject,
    ): Observable<TopupEntity>

    @GET(AllAPIS.APP_INFO)
    fun getAppInfo(): Observable<TermsAndPolicyEntity>

    @GET("${AllAPIS.FIND_PROVIDER}/{ID}")
    fun findProvider(@Path ("ID") id: String,@Query("distance") distance:String): Observable<FindProviderEntity>

    @POST(AllAPIS.ADD_TOPUP)
    fun addTopup(
        @Body jsonObj: JsonObject,
    ): Observable<TopupEntity>

    @GET(AllAPIS.WALLET_TRANSACTION)
    fun walletTransaction(@QueryMap jsonObj: HashMap<String,String>): Observable<TransactionEntity>

    @GET(AllAPIS.STRIPE_LINK_ACCOUNT)
    fun stripeLinkAccount(): Observable<ConnectEntity>

    @POST(AllAPIS.STRIPE_CONNECT_ACCOUNT_LINK)
    fun connectAccountLink(@Body jsonObj: JsonObject): Observable<String>


    /**/
    //stripe methods

    @FormUrlEncoded
    @POST("v1/customers")
    fun createCustomer(@Field("description") description: String): Call<CreateCustomerEntity>

    @FormUrlEncoded
    @POST("/v1/customers/{CUSTOMER_ID}/sources")
    fun createCard(
        @Path("CUSTOMER_ID") cutsomer_id: String,
        @Field("source") sources: String
    ): Call<CreateCardModel>

    @FormUrlEncoded
    @POST("/v1/customers/{CUSTOMER_ID}")
    fun updateCustomer(
        @Path("CUSTOMER_ID") cutsomer_id: String,
        @Field("default_source") default_source: String
    ): Call<CreateCustomerEntity>

    @GET("/v1/customers/{CUSTOMER_ID}")
    fun getCustomerInfo(@Path("CUSTOMER_ID") cutsomer_id: String): Call<CreateCustomerEntity>

    @GET("/v1/customers/{ID}/sources")
    fun getCards(
        @Path("ID") ID: String,
        @Query("object") sources: String = "card"
    ): Call<GetCardsListModel>

    @DELETE("/v1/customers/{CUSTOMER_ID}/sources/{CARD_ID}")
    fun deleteCard(
        @Path("CUSTOMER_ID") cutsomer_id: String,
        @Path("CARD_ID") card_id: String
    ): Call<DeletedCardEntity>

}