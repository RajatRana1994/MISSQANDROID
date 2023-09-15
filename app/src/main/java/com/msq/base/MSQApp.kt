package com.msq.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.msq.UtilFiles.ConnectivityReceiver
import com.msq.UtilFiles.ConnectivityReceiver.ConnectivityReceiverListener
import com.msq.UtilFiles.NetworkServices
import com.msq.push.NotificationBroadcast
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import com.stripe.android.PaymentConfiguration
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class MSQApp : Application(),Application.ActivityLifecycleCallbacks {

    val braodcast by lazy { NotificationBroadcast() }
    companion object {
        var isForegroundMSQ=false
        var ON_CLICK_DELAY = 1 * 2000
        var lastTimeClicked = 0L
        var sessionManager: SessionManager? = null
        fun session(context: Context): SessionManager {
            if (sessionManager == null)
                sessionManager = SessionManager(context)
            return sessionManager!!
        }

        private var mInstance: MSQApp? = null
        public fun getInstance(): MSQApp? {
            return mInstance
        }
    }


    fun setConnectivityListener(listener: ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    operator fun get(context: Context): MSQApp {
        return context.applicationContext as MSQApp
    }

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        session(this)
        mInstance = this
        FirebaseApp.initializeApp(this)
        val intent = Intent(mInstance, NetworkServices::class.java)
        mInstance!!.startService(intent)
        PaymentConfiguration.init(
            applicationContext,
            ConstUtils.PUBLISH_KEY
        )
        FirebaseCrashlytics.getInstance();
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            val refreshedToken = it.result
            sessionManager!!.savePrefString(SessionManager.DEVICE_TOKEN,refreshedToken?:"")
            Log.e("TOKENIZATION",refreshedToken?:"")
        }
        val filter = IntentFilter("ACTION_NOTIFY_POPUP")
        val filterPayment = IntentFilter("ACTION_NOTIFY_PAYMENT")
        registerReceiver(braodcast, filter)
        registerReceiver(braodcast, filterPayment)
    }


    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(braodcast)
    }
    fun loadJSONFromAsset(context: Context): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = context.assets.open("statesjson")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }



    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        isForegroundMSQ=true
    }

    override fun onActivityStarted(p0: Activity) {
        isForegroundMSQ=true
    }

    override fun onActivityResumed(p0: Activity) {
        isForegroundMSQ=true
    }

    override fun onActivityPaused(p0: Activity) {
        isForegroundMSQ=false
    }

    override fun onActivityStopped(p0: Activity) {
        isForegroundMSQ=false
    }

    override fun onActivityDestroyed(p0: Activity) {
        isForegroundMSQ=false
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) = Unit
}