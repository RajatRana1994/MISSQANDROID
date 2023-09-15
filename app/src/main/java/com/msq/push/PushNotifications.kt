package com.msq.push

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.msq.BuildConfig
import com.msq.R
import com.msq.base.MSQApp
import com.msq.base.MSQApp.Companion.session
import com.msq.code.MainActivity
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import org.json.JSONObject


class PushNotifications : FirebaseMessagingService() {
    private val TAG = PushNotifications::class.java.simpleName

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.from)
// Check if message contains a data payload.
        Log.e("NOTIFICATION_DATA", "${remoteMessage.data}")
        if (remoteMessage.data != null) sendNotification(remoteMessage.data)
    }

    /* {data={"requestCount":1,"serviceProviderId":13,"distance":"10000","totalPrice":20,"created":1658247620,"latitude":"29.8371295","isFinding":1,
    "bookingStartTime":"1658679614","userId":12,"bookingEndTime":"1658683214","paymentType":"1","duration":"60","timeFindNextProvider":1658247680,"modified":1658247620,
    "bookingDate":"1658679614","serviceDetails":{"image":"","price":20,"created":0,"name":"Hair","modified":0,"id":1,"status":1},"id":9,"serviceId":1,"notificationCode":3,
    "longitude":"77.8902961"}, token=dfyDPDv7QBGt7jb0_owdmg:APA91bFOcbXrfIeVygdP6SLaBgloaRvNMo3hodMelCtaOXkS_Sl--HVQhCLzvW02IcAPalHm8RCQkCpP18uY-iJcfwmfK_WROAFrAhxAEuUbjf7Aam7z-PhuXwDWCtPtqOiugvIG8RPe, deviceType=1, message=You have new booking request!}*/
    private fun appInForeground(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        for (runningAppProcess in runningAppProcesses) {
            if (runningAppProcess.processName == packageName &&
                runningAppProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            ) {
                return true
            }
        }
        return false
    }

    private fun sendNotification(data: Map<String, String?>) {
        val pref = SessionManager(this)
        if (pref.getPrefBool(SessionManager.IS_LOGIN).not()) return // show notification
//        val isCustomer = pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_USER
//        if (pref.getBool(AppConstants.IS_LOGIN).not()) return
        var title: String = getString(R.string.app_name)
        var message: String = ""
        val type =
            if (data.containsKey("notificationCode")) data["notificationCode"] else if (data.containsKey(
                    "data")
            ) JSONObject(data["data"]).getString("notificationCode") else JSONObject(data["body"]).getString(
                "notificationCode")
        if (data.containsKey("title")) title =
            (if (data["title"] == "") getString(R.string.app_name) else data["title"]
                ?: "") else title = getString(R.string.app_name)
        if (data.containsKey("message")) message = data["message"] ?: ""
//        if (data.containsKey("body")) message = data["body"] ?: ""
        var senderId = ""
        /*API: acceptBooking
        notificationCode: 1
        message: Your Booking has been accepted

        API: acceptBooking
        notificationCode: 2
        message: Your Booking has been canceled by owner

        API: bookServices
        notificationCode: 3
        message: You have new booking request!

        API: sendMessage
        notificationCode: 8
        message: 'message content'*/
        /*{data={"bookingId":"42","notificationCode":2},
         token=fpU8BUOCRvylcjWys02_o3:APA91bEeiTpKxgjm3WMr87adHHcO-0bR7j3Iu4zRTFUgzBUaY0yBbH1Ja5C6fu24yiPh3_oQkYCL0Mt2NlUIXvK-nkGCNUI2rMwC55zoXpI9THYcwMKlog_Usu3sEUrbQ6hPMtfaUK8G, message=Your Booking has been canceled by owner}*/
/**/
//        {data={"serviceProviderId":8,"totalPrice":100,"created":1656044317,"bookingStartTime":"1658675912","userId":9,"bookingEndTime":"1658683112","paymentType":"1",
//        "duration":"120","modified":1656044317,"bookingDate":"1658636312","serviceDetails":{"image":"http:\/\/54.169.185.218\/uploads\/1654256494138.png","price":100,
//        "created":0,"name":"House Keeping ","modified":1654256773,"id":4,"status":1},"id":65,"serviceId":4,"notificationCode":3},
//        token=fpU8BUOCRvylcjWys02_o3:APA91bEeiTpKxgjm3WMr87adHHcO-0bR7j3Iu4zRTFUgzBUaY0yBbH1Ja5C6fu24yiPh3_oQkYCL0Mt2NlUIXvK-nkGCNUI2rMwC55zoXpI9THYcwMKlog_Usu3sEUrbQ6hPMtfaUK8G, message=You have new booking request!}
        var pendingIntent: PendingIntent? = null
        pendingIntent =
            when (type) {//
                "1", "2" -> {
                    val json = JSONObject(data["data"])
                    if (appInForeground()) {
                        val intent = Intent("ACTION_NOTIFY_POPUP")
                        intent.putExtra("type", type)
                        intent.putExtra("id", json.getString("bookingId"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        Intent("ACTION_LOCAL_NOTIFY").apply {
                            putExtras(intent)
                            LocalBroadcastManager.getInstance(this@PushNotifications).sendBroadcast(this)
                        }
                        PendingIntent.getBroadcast(this,
                            0,
                            intent, PendingIntent.FLAG_IMMUTABLE or
                                    PendingIntent.FLAG_UPDATE_CURRENT)
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("type", type)
                        intent.putExtra("id", json.getString("bookingId") ?: "")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getActivity(this,
                            0,
                            intent, PendingIntent.FLAG_IMMUTABLE or
                                    PendingIntent.FLAG_ONE_SHOT)

                    }
                }
                "3" -> {
                    val json = JSONObject(data["data"])
                    if (json.getString("serviceProviderId") != session(this).getPrefString(SessionManager.USER_ID)!!) return
                    if (appInForeground()) {
                        val intent = Intent("ACTION_NOTIFY_POPUP")
                        intent.putExtra("type", "3")
                        intent.putExtra("id", json.getString("id"))
                        intent.putExtra("service_image",
                            JSONObject(json.getString("serviceDetails")).getString("image"))
                        intent.putExtra("service_name",
                            JSONObject(json.getString("serviceDetails")).getString("name"))
                        intent.putExtra("service_price",
                            JSONObject(json.getString("serviceDetails")).getString("price"))
                        intent.putExtra("timeFindNextProvider",
                            json.getString("timeFindNextProvider"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        Intent("ACTION_LOCAL_NOTIFY").apply {
                            putExtras(intent)
                            LocalBroadcastManager.getInstance(this@PushNotifications)
                                .sendBroadcast(this)
                        }
//                        PendingIntent.getBroadcast(this,
//                            0,
//                            intent,PendingIntent.FLAG_IMMUTABLE or
//                            PendingIntent.FLAG_UPDATE_CURRENT)
                        null
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("type", "3")
                        intent.putExtra("id", json.getString("id"))
                        intent.putExtra("service_image",
                            JSONObject(json.getString("serviceDetails")).getString("image"))
                        intent.putExtra("service_name",
                            JSONObject(json.getString("serviceDetails")).getString("name"))
                        intent.putExtra("service_price",
                            JSONObject(json.getString("serviceDetails")).getString("price"))
                        intent.putExtra("timeFindNextProvider",
                            json.getString("timeFindNextProvider"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getActivity(this,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
                    }
                }
                "8" -> {
//{body={"videoLength":"0","receiver_id":"8","created":1656223966,"message_type":"0","message":"hi","bookingId":"9","sender_id":9,"friend_id":"8","thread_id":3,"user_id":9,
// "user_info":{"stripeAccountId":"","address":"Roorkee (Uttarakhand) Busstand, VV7Q+JGC, Dharun, Roorkee, Uttarakhand 247667, India","authorizationKey":"00bbd89445d3bcfd72ba140ea47cb2ffbbeb67c6",
// "latitude":29.8640501,"profile":"1650623392095.png","otp":1111,"password":"7c4a8d09ca3762af61e59520943dc26494f8941b","balance":0,"phone":"8791683688","dob":0,"stripe_connect":0,
// "stripeId":"","name":"Maheshwar Uniyal","isOnDuty":1,"id":9,"userType":0,"email":"maheshwaruniyal835@gmail.com","status":1,"longitude":77.8887761},"modified":1656223966,
// "id":27,"text":"hi"}, token=cJNLsGNsQ0WoDK3QjzVouT:APA91bFRaYnV43M3MQAJJLj8MowZDIgQkLRctSXDpxp9nnRGtmgP1CrnDTWCXIvkKugem27Z00EnVxGWn2Y5_2njiUpOeszZXTcd0crLjnpgBAjl_mjybueWIF2OF6gM4m1kLTOvJy2l,
// deviceType=1, message=hi, notificationCode=8}
                    val json =
                        if (data.containsKey("data")) JSONObject(data["data"]) else JSONObject(data["body"])
                    senderId = json.getString("sender_id").toString()
                    if (appInForeground()) {
                        //                showFragment(FragConst.CHAT_MSG, bundleOf("bookingId" to it.data[p].bookingId.toString()
                        //                ,"friend_id" to if (it.data[p].sender_id.toString()==pref.getPrefString(SessionManager.USER_ID)!!)
                        //                it.data[p].receiver_id.toString() else it.data[p].sender_id.toString()))
                        val intent = Intent("ACTION_NOTIFY_POPUP")
                        intent.putExtra("type", "8")
                        intent.putExtra("id", json.getString("bookingId"))
                        intent.putExtra("friend_id",
                            if (senderId == pref.getPrefString(SessionManager.USER_ID)!!)
                                json.getString("receiver_id").toString() else senderId)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getBroadcast(this,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("type", "8")
                        intent.putExtra("id", json.getString("bookingId"))
                        intent.putExtra("friend_id",
                            if (json.getString("sender_id").toString() == pref.getPrefString(
                                    SessionManager.USER_ID)!!
                            )
                                json.getString("receiver_id")
                                    .toString() else json.getString("sender_id").toString())
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getActivity(this,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
                    }
                }
                "23" -> {
                    /*{data={"isCredit":1,"amount":1,"transectionType":4,"text":"Payment on booking 1","paymentDetails":"{}","userId":6,"notificationCode":23}, token=eX2sE5T_QhS8s3aEyUI4ff:APA91bHXt3eFNh_GeRj6DhkOURJqg-kdgnyDby7kDFoODKNDEHHJisWzVNSe7iwsydcSzqO7O17UTMEtbWAK7yzTArFnGkSIxKwkQwUYIflxROgL0UzJ6BeY1paEVKLmMaRDoXVqK1_Z, deviceType=1, message=Your payment is done}*/
                    val json = JSONObject(data["data"])
                    if (appInForeground()) {
                        val intent = Intent("ACTION_NOTIFY_PAYMENT")
                        intent.putExtra("type","23")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getBroadcast(this,
                            0,
                            intent, PendingIntent.FLAG_IMMUTABLE or
                                    PendingIntent.FLAG_UPDATE_CURRENT)
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("type","23")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getActivity(this,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
                    }
                }
                /*{data={"isCredit":1,"transactionDetails":"{\"data\":{\"id\":\"evt_p7t3Lc76mn1wYpSV1yn5necw\",\"type\":\"event\",\"attributes\":{\"type\":\"link.payment.paid\",\"livemode\":false,\"data\":{\"id\":\"link_EzGicGCrRbb7Mciw9q4f762E\",\"type\":\"link\",\"attributes\":{\"amount\":10000,\"archived\":false,\"currency\":\"PHP\",\"description\":\"Amount added in the wallet\",\"livemode\":false,\"fee\":1850,\"remarks\":null,\"status\":\"paid\",\"tax_amount\":null,\"taxes\":[],\"checkout_url\":\"https:\/\/pm.link\/MsQEmploymentServices\/test\/GgW9Sa7\",\"reference_number\":\"GgW9Sa7\",\"created_at\":1663414327,\"updated_at\":1663414327,\"payments\":[{\"data\":{\"id\":\"pay_sYzYhGzrxcsjnYL9oq9Zuqin\",\"type\":\"payment\",\"attributes\":{\"access_url\":null,\"amount\":10000,\"balance_transaction_id\":\"bal_txn_BkbJxBtfwZ66qL6LMzMXsGL4\",\"billing\":{\"address\":{\"city\":\"Rke\",\"country\":\"IN\",\"line1\":\"Def\",\"line2\":\"Col\",\"postal_code\":\"244676\",\"state\":\"Uttarakhand\"},\"email\":\"deepakuniyal835@gmail.com\",\"name\":\"Deep\",\"phone\":\"8791683678 \"},\"currency\":\"PHP\",\"description\":\"Amount added in the wallet\",\"disputed\":false,\"external_reference_number\":\"GgW9Sa7\",\"fee\":1850,\"foreign_fee\":100,\"livemode\":false,\"net_amount\":8050,\"origin\":\"links\",\"payment_intent_id\":\"pi_pZR6bHRhamWMrmBJvr8k21oU\",\"payout\":null,\"source\":{\"id\":\"card_NdANTvpujzBCaQaQav391B1X\",\"type\":\"card\",\"brand\":\"visa\",\"country\":\"US\",\"last4\":\"4345\"},\"statement_descriptor\":\"Ms Q Employment Services\",\"status\":\"paid\",\"tax_amount\":null,\"metadata\":{\"pm_reference_number\":\"GgW9Sa7\"},\"refunds\":[],\"taxes\":[],\"available_at\":1663837200,\"created_at\":1663414430,\"paid_at\":1663414430,\"updated_at\":1663414430}}}]}},\"previous_data\":{},\"created_at\":1663414430,\"updated_at\":1663414430}}}","amount":100,"created":1663414465,"transectionType":1,"modified":1663414465,"text":"add topup 100 in wallet","userId":16,"notificationCode":24}, token=ev73mOE0TSSsSkiGxzzueV:APA91bEnkrLqSCob4IxFrdiVvufJYWpwXuurqiRDaBlPHJMmYlYab6ph_vMXN-0wW1r0L4xanBIkHg8wulAlCx-qTcs7hDNLmeq-0mvJP_IeFJ0LvWFA3-RhME2U0PHre0Xj9J63zBdJ, deviceType=1, message=Payment done,The amount 100 successfully added in your wallert.}*/
                "24" -> {
                    /*{data={"isCredit":1,"amount":1,"transectionType":4,"text":"Payment on booking 1","paymentDetails":"{}","userId":6,"notificationCode":23}, token=eX2sE5T_QhS8s3aEyUI4ff:APA91bHXt3eFNh_GeRj6DhkOURJqg-kdgnyDby7kDFoODKNDEHHJisWzVNSe7iwsydcSzqO7O17UTMEtbWAK7yzTArFnGkSIxKwkQwUYIflxROgL0UzJ6BeY1paEVKLmMaRDoXVqK1_Z, deviceType=1, message=Your payment is done}*/
                    val json = JSONObject(data["data"])
                    try {
                        session(this).savePrefFloat(SessionManager.BALANCE,
                            session(this).getPrefFloat(SessionManager.BALANCE)+(json.getInt("amount").toFloat()))
                    } catch (e: Exception) {
                    }
                    if (appInForeground()) {
                        val intent = Intent("ACTION_NOTIFY_PAYMENT")
                        intent.putExtra("type","24")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getBroadcast(this,
                            0,
                            intent, PendingIntent.FLAG_IMMUTABLE or
                                    PendingIntent.FLAG_UPDATE_CURRENT)
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("type","24")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        PendingIntent.getActivity(this,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
                    }
                }
                else -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    PendingIntent.getActivity(this,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
                }
            }
        if (MSQApp.session(this)
                .getPrefString(SessionManager.USER_TYPE) != ConstUtils.TYPE_PROVIDER && type == "3"
        ) return
        if (senderId == pref.getPrefString(SessionManager.USER_ID)!! && type == "8") return
        if (pendingIntent != null)
            showNotification(title, message, pendingIntent)
    }

    fun showNotification(title: String, message: String, pendingIntent: PendingIntent) {

        val channelId = BuildConfig.APPLICATION_ID
//val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
            .setColor(ContextCompat.getColor(this, R.color.white))
            .setContentTitle(title).setContentText(message)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)).setAutoCancel(true)
//.setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

// Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel${getString(R.string.app_name)}",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }
        notificationManager?.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
    }
}