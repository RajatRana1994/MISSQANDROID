package com.msq

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.msq.base.FragConst
import com.msq.code.auth.*
import com.msq.code.customer.booking.BookingFragment
import com.msq.code.customer.chat.ChatMessageFragment
import com.msq.code.customer.chat.ChatUserFragment
import com.msq.code.customer.checkout.CheckoutFragment
import com.msq.code.customer.home.HomeFragment
import com.msq.code.customer.home.HomePurchaseDetailFragment
import com.msq.code.customer.payment.AddCardFragment
import com.msq.code.customer.payment.PaymentMethodsFragment
import com.msq.code.customer.profile.ProfileFragment
import com.msq.code.customer.purchasedetail.PurchaseDetailFragment
import com.msq.code.customer.review.ReviewFragment
import com.msq.code.customer.servicebook.ServiceBookFragment
import com.msq.code.customer.track.TrackFragment
import com.msq.code.provider.booking.BookingPROFragment
import com.msq.code.provider.changepass.ChangePassPROFragment
import com.msq.code.provider.chatbook.ChatBookFragment
import com.msq.code.provider.confirmation.ConfirmationFragment
import com.msq.code.provider.connect.ConnectFragment
import com.msq.code.provider.empsurvey.EmpSurveyPROFragment
import com.msq.code.provider.home.HomePROFragment
import com.msq.code.provider.language.LanguageFragment
import com.msq.code.provider.location.LocationFragment
import com.msq.code.provider.notification.NotificationFragment
import com.msq.code.provider.orderdetail.*
import com.msq.code.provider.payment.PaymentFragment
import com.msq.code.provider.profile.ProfilePROFragment
import com.msq.code.provider.review.ReviewPROFragment
import com.msq.code.provider.setting.SettingsFragment
import com.msq.code.provider.share.ShareFragment
import com.msq.code.provider.wallet.WalletFragment
import com.msq.code.provider.wallet.topup.TopupDoneFragment
import com.msq.code.provider.wallet.topup.TopupFragment
import com.msq.code.provider.wallet.transaction.TransactionFragment
import com.msq.code.provider.welcome_success.WelcomeSuccessFragment
import com.msq.code.terms.TermsFragment
import com.msq.util.ext.toast

open class BaseActivity : AppCompatActivity() {

    var locationDialog: AlertDialog? = null

    //    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var locationCallback: LocationCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS) // inside your activity (if you did not enable transitions in your theme)
//        window.enterTransition = Explode() // set an enter transition
//        window.exitTransition = Explode()// set an exit transition
        adjustFontScale(resources.configuration)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }


    //hide the keyboard on touch of other views except edittext
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith(
                "android.webkit."
            )
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.left - scrcoords[0]
            val y = ev.rawY + view.top - scrcoords[1]
            if (x < view.left || x > view.right || y < view.top || y > view.bottom)
            //  mAppUtils.hideSoftKeyboard(window.decorView.rootView)
                hideKeyboard(window.decorView.rootView)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    open fun adjustFontScale(configuration: Configuration) {
        Log.i("configuration.fontScale", configuration.fontScale.toString())
        configuration.fontScale = 1.0f
        val metrics: DisplayMetrics = resources.displayMetrics
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)

    }

    fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/html"
// emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@gmail.com"))
        val pm = packageManager
        val matches = pm.queryIntentActivities(emailIntent, 0)
        var className: String? = null
        for (info in matches) {
            if (info.activityInfo.packageName == "com.google.android.gm") {
                className = info.activityInfo.name
                if (className != null && !className.isEmpty()) {
                    break
                }
            }
        }
        if (className != null) {
            emailIntent.setClassName("com.google.android.gm", className)
            startActivity(emailIntent)
        } else {
            toast("Doesn't support!", false)
        }
    }

    fun toggleKeyboard(view: View) {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInputFromWindow(
            view.applicationWindowToken,
            InputMethodManager.SHOW_FORCED, TYPE_CLASS_NUMBER
        )
    }

    public fun fadeAnim() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun showFragment(tag: String, bundle: Bundle? = null) {
        try {
            supportFragmentManager.executePendingTransactions()
        } catch (e: java.lang.Exception) {
        }
        val transaction = supportFragmentManager.beginTransaction()
        val newFragment = createNewFragmentForTag(tag)
        if (!tag.contains("ROOT")) {
            transaction.addToBackStack(null)
//            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        newFragment?.let {
            if (bundle != null) newFragment.arguments = bundle else newFragment.arguments = Bundle()
            transaction.apply {
                replace(R.id.mainContainer, newFragment, tag).commitAllowingStateLoss()
            }
        }
    }

    private fun createNewFragmentForTag(tag: String): Fragment? {
        when (tag) {
            FragConst.SPLASH -> return SplashFragment()
            FragConst.WELCOME -> return WelcomeFragment()
            FragConst.GETSTARTED -> return GetStartedFragment()
            FragConst.SIGNUP -> return SignupFragment()
            FragConst.HOME -> return HomeFragment()
            FragConst.BOOKINGS -> return BookingFragment()
            FragConst.CHAT -> return ChatUserFragment()
            FragConst.PROFILE -> return ProfileFragment()
            FragConst.CHAT_MSG -> return ChatMessageFragment()
            FragConst.REVIEW -> return ReviewFragment()
            FragConst.CHECKOUT -> return CheckoutFragment()
            FragConst.SERVICE_BOOK -> return ServiceBookFragment()
            FragConst.PURCHASE_DETAIL -> return PurchaseDetailFragment()
            FragConst.HOME_PURCHASE_DETAIL -> return HomePurchaseDetailFragment()
            FragConst.TRACK_BOOKING -> return TrackFragment()
            FragConst.PAYMENT_METHODS -> return PaymentMethodsFragment()
            FragConst.ADD_CARDS -> return AddCardFragment()
            FragConst.PAYMENT_PAYMONGO -> return PaymentFragment()

            FragConst.SIGNUP_PRO -> return SignupProviderFragment()
            FragConst.CONNECT_PRO -> return ConnectFragment()
//            FragConst.CONNECT_PRO -> return HomePROFragment()
            FragConst.LOGIN -> return LoginFragment()
            FragConst.FORGOTPASSWORD -> return ForgotPasswordFragment()
            FragConst.TERMS -> return TermsFragment()
            FragConst.WELCOME_SUCCESS_PRO -> return WelcomeSuccessFragment()
            FragConst.CONFIRMATION_PRO -> return ConfirmationFragment()
            FragConst.HOME_PRO -> return HomePROFragment()
            FragConst.LOCATION_PRO -> return LocationFragment()
            FragConst.ORDER_DETAIL_PRO -> return OrderDetailFragment()
            FragConst.CONTACTED_ORDER_CLIENT_PRO -> return ContactedOrderClientFragment()
            FragConst.ORDER_PROOF_PRO -> return OrderProofFragment()
            FragConst.COMPLETE_ORDER_SETTLE_PRO -> return CompletedOrderSettleFeeFragment()
            FragConst.COMPLETED_ORDER_PRO -> return OrderCompletedFragment()
            FragConst.REVIEW_PRO -> return ReviewPROFragment()
            FragConst.WALLET_PRO -> return WalletFragment()
            FragConst.TOPUP_PRO -> return TopupFragment()
            FragConst.TRANSACTION_PRO -> return TransactionFragment()
            FragConst.TOPUP_DONE_PRO -> return TopupDoneFragment()
            FragConst.PROFILE_PRO -> return ProfilePROFragment()
            FragConst.CHANGE_PASS_PRO -> return ChangePassPROFragment()
            FragConst.BOOKING_PRO -> return BookingPROFragment()
            FragConst.SETTING_PRO -> return SettingsFragment()
            FragConst.CHAT_BOOK_PRO -> return ChatBookFragment()

            FragConst.EMP_SURVEY_PRO -> return EmpSurveyPROFragment()
            FragConst.SHARE_PRO -> return ShareFragment()
            FragConst.NOTIFICATION_PRO -> return NotificationFragment()
            FragConst.LANGUAGE_PRO -> return LanguageFragment()
        }
        return null
    }


    // fused location update functionality
//    @SuppressLint("MissingPermission")
//    fun startLocationUpdates(apiCall: ((lat: Double, lng: Double) -> Unit)) {
//        val locationRequest = LocationRequest.create()?.apply {
//            interval = 1000
//            fastestInterval = 5000
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
//        val client: SettingsClient = LocationServices.getSettingsClient(this)
//        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//        task.addOnSuccessListener { locationSettingsResponse ->
//            locationCallback = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult?) {
//                    locationResult ?: return
//                    if (locationResult.locations.size > 0) {
//                        val lat =
//                            locationResult.locations[locationResult.locations.size - 1].latitude
//                        val lng =
//                            locationResult.locations[locationResult.locations.size - 1].longitude
//                        apiCall(lat, lng)
//                    }
//                    stopLocationUpdates()
//                }
//            }
//            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
//        }
//        task.addOnFailureListener { exception ->
//            exception.printStackTrace()
//            if (exception is ResolvableApiException) {
//                try {
//                    exception.startResolutionForResult(this, 123)
//                    apiCall(0.0, 0.0)
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    Log.e("exception", sendEx.toString())
//                }
//            }
//        }
//    }

//    //stop location updates
//    private fun stopLocationUpdates() {
//        fusedLocationClient.removeLocationUpdates(locationCallback)
//    }

    fun checkLocation(): Boolean {
        var gps_enabled = false
        var network_enabled = false
        val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        return gps_enabled && network_enabled
    }

//    fun displayLocationSettingsRequest(context: Context) {
//        val mLocationRequest = LocationRequest.create()
//            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//            .setInterval(10 * 1000)
//            .setFastestInterval(2 * 1000)
//        val settingsBuilder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(mLocationRequest)
//        settingsBuilder.setAlwaysShow(true)
//        val result =
//            LocationServices.getSettingsClient(this)
//                .checkLocationSettings(settingsBuilder.build())
//        result.addOnCompleteListener { task ->
//            try {
//                val response = task.getResult<ApiException>(ApiException::class.java)
//            } catch (ex: ApiException) {
//                when (ex.getStatusCode()) {
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
//                        val resolvableApiException =
//                            ex as ResolvableApiException
//                        if (checkLocation().not())
//                            resolvableApiException.startResolutionForResult(this@BaseActivity, 999)
//                    } catch (e: IntentSender.SendIntentException) {
//                    }
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                    }
//                }
//            }
//        }
//    }

}