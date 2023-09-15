package com.msq.code

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.buuzconnect.uis.addevent.BookingPROViewModel
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.makeramen.roundedimageview.RoundedImageView
import com.msq.BaseActivity
import com.msq.Parser.AllAPIS
import com.msq.R
import com.msq.UtilFiles.AESUtils
import com.msq.UtilFiles.LocationLiveData
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import com.msq.base.MSQApp
import com.msq.code.customer.booking.BookingFragment
import com.msq.code.customer.chat.ChatUserFragment
import com.msq.code.customer.home.HomeFragment
import com.msq.code.customer.home.HomePurchaseDetailFragment
import com.msq.code.customer.review.ReviewFragment
import com.msq.code.provider.booking.BookingPROFragment
import com.msq.code.provider.home.HomePROFragment
import com.msq.code.provider.profile.ProfilePROFragment
import com.msq.code.provider.setting.SettingsFragment
import com.msq.code.provider.wallet.WalletFragment
import com.msq.util.*
import com.msq.util.ext.bgTint
import com.msq.util.ext.commonPopup
import com.msq.util.ext.onOffVisibility
import com.msq.util.ext.toast
import com.socialgalaxyApp.util.extension.loadWallRound
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity(), View.OnClickListener {
    val pref by lazy { MSQApp.session(this) }
    lateinit var viewBookingModel: BookingPROViewModel
    lateinit var countDownTimer: CountDownTimer
    val options by lazy {
        Options.init()
            .setRequestCode(100) //Request code for activity results
            .setCount(3) //Number of images to restict selection count
            .setFrontfacing(false) //Front Facing camera on start
            .setPreSelectedUrls(arrayListOf()) //Pre selected Image Urls
            .setSpanCount(4) //Span count for gallery min 1 & max 5
            .setMode(Options.Mode.All) //Option to select only pictures or videos or both
            .setVideoDurationLimitinSeconds(30) //Duration for video recording
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images") //Custom Path For media Storage

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addObserverForFragmentLifecycle()
        viewBookingModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(this))
                .get(BookingPROViewModel::class.java)
        viewBookingModel.mAcceptRejectBookingEntity.observe(this) {
            toast(it.message)
        }
        CheckPermission(this).locationPermission {
            // Check if location settings are available
            LocationLiveData().init(this).observe(this) {
                it?.let {
                    pref.savePrefString(SessionManager.LAT, "${it.latitude}")
                    pref.savePrefString(SessionManager.LNG, "${it.longitude}")
                    Log.e("LATLNG","${it.latitude}, ${it.longitude}")
                }
            }
        }
        onClicks()
        checkDeeplinkingAndNavigation()


    }

    private fun checkDeeplinkingAndNavigation() {
        val action: String = intent?.action ?: ""
        val data: Uri? = intent?.data
        if (action == "android.intent.action.VIEW" && data != null) {
            val encryptUri = data.path?.substringAfterLast(AllAPIS.SHARING_PROFILE_CONTROLLER)
            val decrypt = AESUtils.decrypt(encryptUri)
            Log.e("PROFILE PATH", "${AllAPIS.BASE_URL}$decrypt")
            if (pref.getPrefIsLogin(SessionManager.IS_LOGIN)) {
                navigateTo()
                if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_USER) {

                } else {
//                    showFragment(FragConst.PROFILE_PRO)
                }
            } else navigateTo()
        } else navigateTo()
    }

    private fun navigateTo() {
        var foundNavigation = false
        /* {data={"requestCount":1,"serviceProviderId":16,"distance":50,"totalPrice":290,"created":1684473167,"latitude":"29.8374038","isFinding":1,"bookingStartTime":"1684491159","userId":50,"bookingEndTime":"1684494759","paymentType":"1","duration":"60","timeFindNextProvider":1684473287,"modified":1684473167,"bookingDate":"1684473159","serviceDetails":{"image":"https:\/\/app.msqassociates.com\/uploads\/1663473118389.png","price":290,"created":0,"name":"House  Keeper","modified":1683679133,"id":4,"status":1},"id":185,"serviceId":4,"notificationCode":3,"longitude":"77.8901015"}, deviceType=1, message=You have new booking request!}*/
        if (pref.getPrefIsLogin(SessionManager.IS_LOGIN)) {
            intent?.extras?.let {
                val type = it.getString("type") ?: ""
                val id = it.getString("id") ?: ""
                if (type.equals("3")) {
                    val service_image = it.getString("service_image") ?: ""
                    val service_name = it.getString("service_name") ?: ""
                    val service_price = it.getString("service_price") ?: ""
                    val timeFindNextProvider =
                        intent.extras?.getString("timeFindNextProvider") ?: ""
                    showNewBookingReq(id,
                        service_name,
                        service_price,
                        service_image,
                        timeFindNextProvider)
                } else if (type.equals("1") || type.equals("2")) {
                    foundNavigation = true
                    showFragment(FragConst.BOOKINGS)
                } else if (type.equals("8")) {
                    if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_USER)
                        showFragment(FragConst.CHAT_MSG,
                            bundleOf("bookingId" to id,
                                "friend_id" to (intent.extras?.getString("friend_id") ?: "")))
                    else
                        showFragment(FragConst.CHAT_BOOK_PRO,
                            bundleOf("booking_id" to id,
                                "user_id" to (intent.extras?.getString("friend_id") ?: "")))
                }
            }
        }

        if (foundNavigation.not())
            showFragment(FragConst.SPLASH)

    }

    private fun onClicks() {
        tabHome.setOnClickListener(this)
        tabBookings.setOnClickListener(this)
        tabWallet.setOnClickListener(this)
        tabProfile.setOnClickListener(this)
        tabSettings.setOnClickListener(this)
    }

    private fun addObserverForFragmentLifecycle() {
        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                if (f is SupportRequestManagerFragment) return
                bottomNav.onOffVisibility(f is HomeFragment || f is BookingFragment || f is ChatUserFragment || f is ReviewFragment || f is HomePurchaseDetailFragment)
                bottomNavProvider.onOffVisibility(f is HomePROFragment || f is WalletFragment || f is ProfilePROFragment || f is BookingPROFragment || f is SettingsFragment)
            }
        }, false)
    }


    override fun onStart() {
        super.onStart()
        val menuView =
            findViewById<BottomNavigationView>(R.id.bottomNav).getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val iconView = menuView.getChildAt(i).findViewById<ImageView>(R.id.navigation_bar_item_icon_view)
            iconView.scaleType = ImageView.ScaleType.FIT_XY
            iconView.layoutParams = iconView.layoutParams.also {
                it.height = 70
                it.width = if (i == 1) 55 else 70
            }
        }
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> showFragment(FragConst.HOME)
                R.id.item_booking -> showFragment(FragConst.BOOKINGS)
                R.id.item_chat -> showFragment(FragConst.CHAT)
                R.id.item_reviews -> showFragment(FragConst.REVIEW)
            }
            true
        }


    }

    fun updateHomeProTab() {
        updateBgTint(0)
    }

    fun onBack(){
        onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v!!) {
            tabHome -> {
                showFragment(FragConst.HOME_PRO)
                updateBgTint(0)
            }
            tabBookings -> {
                showFragment(FragConst.BOOKING_PRO)
                updateBgTint(1)
            }
            tabWallet -> {
                showFragment(FragConst.WALLET_PRO)
                updateBgTint(2)
            }
            tabProfile -> {
                showFragment(FragConst.PROFILE_PRO)
                updateBgTint(3)
            }
            tabSettings -> {
                showFragment(FragConst.SETTING_PRO)
                updateBgTint(4)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun updateBgTint(type: Int) {
        tabHome.bgTint(if (type == 0) R.color.blue else android.R.color.transparent)
        tabBookings.bgTint(if (type == 1) R.color.blue else android.R.color.transparent)
        tabWallet.bgTint(if (type == 2) R.color.blue else android.R.color.transparent)
        tabProfile.bgTint(if (type == 3) R.color.blue else android.R.color.transparent)
        tabSettings.bgTint(if (type == 4) R.color.blue else android.R.color.transparent)
    }


    fun capturing(fragment: Fragment, selectedPic: String? = null, onCapture: (Uri) -> Unit) {
        selectedPic?.let {
            options.apply {
                preSelectedUrls = arrayListOf(selectedPic)
            }
        }
        Pix.start(fragment, options);
    }


    fun logout() {
        if (pref.getPrefIsLogin(SessionManager.IS_LOGIN)!!) {
            util.showToast(this, "Session Expired!")
            pref.clearPrefs()
            showFragment(FragConst.WELCOME)
        }
    }


    fun showNewBookingReq(
        booking_id: String,
        service_name: String,
        service_price: String,
        service_image: String,
        timeFindNextProvider: String,
    ) {
        if (pref.getPrefIsLogin(SessionManager.IS_LOGIN) && booking_id.isNullOrEmpty()
                .not() && service_name.isNullOrEmpty().not()
        ) {
            var showReq = true
            var inMillis = 0L
            if (timeFindNextProvider.isNullOrEmpty()) {
                showReq = false
            } else {
                try {
                    inMillis = (timeFindNextProvider.toLong() * 1000)
                    showReq = Calendar.getInstance().timeInMillis < inMillis
                } catch (e: Exception) {
                    showReq = false
                }
            }
            if (showReq) {
                showRequest(booking_id,
                    service_name,
                    service_price,
                    service_image,
                    inMillis - Calendar.getInstance().timeInMillis)
            } else toast("Request Timeout, Better luck next time!.")

        }
    }

    fun showRequest(
        booking_id: String,
        service_name: String,
        service_price: String,
        service_image: String,
        millis: Long,
    ) {
        if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_USER) return
        commonPopup(R.layout.dailog_booking_req) { v, d ->
            d.setCancelable(false)
            v.findViewById<RoundedImageView>(R.id.ivUser).loadWallRound(service_image)
            v.findViewById<TextView>(R.id.tvCustomer).text = service_name
            v.findViewById<TextView>(R.id.tvTimer).apply {
                countDownTimer = object : CountDownTimer(millis, 1000) {
                    override fun onTick(p0: Long) {
                        var sec = p0 / 1000
                        if (sec > 60) {
                            val min = sec / 60
                            sec -= (min * 60)
                            "Timeout: 0$min:${if (sec > 9) sec else "0$sec"}".also {
                                this@apply.text = it
                            }
                        } else {
                            "Timeout: 00:${if (sec > 9) sec else "0$sec"}".also {
                                this@apply.text = it
                            }
                        }
                    }

                    override fun onFinish() {
                        toast("Request Timeout, Better luck next time!.")
                        d.dismiss()
                    }

                }.start()
            }
            v.findViewById<TextView>(R.id.tvPrice).text = "Price: P${service_price}"
            v.findViewById<TextView>(R.id.btnViewDetail).setOnClickListener {
                d.dismiss()
                showFragment(FragConst.ORDER_DETAIL_PRO,
                    bundleOf("booking_id" to booking_id))
            }
            v.findViewById<TextView>(R.id.btnAccept).setOnClickListener {
                d.dismiss()
                viewBookingModel.acceptReject(booking_id, "1")
            }
            v.findViewById<TextView>(R.id.btnReject).setOnClickListener {
                d.dismiss()
                viewBookingModel.acceptReject(booking_id, "2")
            }
            d.setOnDismissListener {
                if (countDownTimer != null) countDownTimer.cancel()
            }
        }

    }


    override fun onResume() {
        super.onResume()
        if (pref.getPrefString(SessionManager.LANGUAGE)!!.isNullOrEmpty()) {
            LocaleHelper.setLocale(this, "en");
        } else LocaleHelper.setLocale(this, pref.getPrefString(SessionManager.LANGUAGE));
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("ACTION_LOCAL_NOTIFY"))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("ACTION_LOCAL_NOTIFY_PAYMENT"))
    }


    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }


    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "ACTION_LOCAL_NOTIFY_PAYMENT") {
                if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_PROVIDER)
                    if (intent.getStringExtra("type")!!.equals("23")) {
                        showFragment(FragConst.BOOKING_PRO)
                        updateBgTint(1)
                    } else if (intent.getStringExtra("type")!!.equals("24")){
                        showFragment(FragConst.WALLET_PRO)
                        updateBgTint(2)
                    }
            } else if (intent.action == "ACTION_LOCAL_NOTIFY") {
                val id = intent.extras?.getString("id") ?: ""
                if (intent.getStringExtra("type")!!.equals("3")) {
                    val service_image = intent.extras?.getString("service_image") ?: ""
                    val service_name = intent.extras?.getString("service_name") ?: ""
                    val service_price = intent.extras?.getString("service_price") ?: ""
                    val timeFindNextProvider =
                        intent.extras?.getString("timeFindNextProvider") ?: ""
                    showNewBookingReq(id,
                        service_name,
                        service_price,
                        service_image,
                        timeFindNextProvider)
                } else {
                    if (intent.getStringExtra("type")!!
                            .equals("1") || intent.getStringExtra("type")!!
                            .equals("2")
                    )
                    {
//                        showFragment(FragConst.BOOKINGS) // impl in checkout screen
                    }
                    else if (intent.getStringExtra("type")!!.equals("8")) {
                        if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_USER)
                            showFragment(FragConst.CHAT_MSG,
                                bundleOf("bookingId" to id,
                                    "friend_id" to (intent.extras?.getString("friend_id") ?: "")))
                        else
                            showFragment(FragConst.CHAT_BOOK_PRO,
                                bundleOf("booking_id" to id,
                                    "user_id" to (intent.extras?.getString("friend_id") ?: "")))
                    }
                }
            }
        }
    }

}