package com.msq.code.customer.checkout

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.buuzconnect.uis.addevent.CheckoutViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.rest.entity.MSQService
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.makeToast
import com.msq.util.ext.toast
import com.msq.util.ext.twoDecimal
import com.socialgalaxyApp.util.extension.loadWallRound
import kotlinx.android.synthetic.main.fragment_checkout.*
import java.util.*


class CheckoutFragment : BaseFragment(), View.OnClickListener {
    val msq_service by lazy { requireArguments().getParcelable<MSQService>("msq_service") }
    lateinit var viewModel: CheckoutViewModel
    var findProviderPopup: AlertDialog? = null
    var bookingId = ""
    var distance = ""
    var findProviderCallCount = 0
    var bookingDate = 0L
    var startTime = 0L
    var endTime = 0L
    var canExit=false
    val handler by lazy { Handler(Looper.myLooper()!!) }
    val runnable by lazy {
        Runnable {
            viewModel.findProvider(bookingId,distance)
            findProviderCall()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(CheckoutViewModel::class.java)
        onClicks()
        initUI()
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!canExit) {
                        createFindProviderPopup()
                    }else requireActivity().onBackPressed()
                }
            })
    }

    private fun onClicks() {
        tvDateTime.setOnClickListener(this)
        btnPayAfterService.setOnClickListener(this)
        btnGCash.setOnClickListener(this)
        btnCard.setOnClickListener(this)
        btnPlaceOrder.setOnClickListener(this)
    }

    fun dismissDialog(){
        if (context==null) return
        if (findProviderPopup!=null)findProviderPopup?.let { if(it.isShowing) findProviderPopup?.dismiss() }
    }

    private fun createFindProviderPopup() {
        findProviderPopup = MaterialAlertDialogBuilder(requireActivity(),
            R.style.ThemeOverlay_MaterialComponents_Light).apply {
            setTitle("Searching Provider...")
            setMessage("Please wait for sometime we are looking for best service provider. We will update you one we will found good match for your service.")
            setCancelable(false)
            setPositiveButton("Okay") { dialog, which ->
                dialog.dismiss()
                if (findProviderCallCount==3) {
                    showFragment(FragConst.BOOKINGS)
                }else findProviderPopup!!.show()
            }
        }.create().apply {
//        window!!.setBackgroundDrawableResource(R.drawable.box_less_rounded_bg)
            show()
        }
    }

    private fun updatePrice(){
        msq_service?.let {
            if (startTime == 0L || endTime == 0L) {
                tvPrice.text = "₱ ${it.price.twoDecimal()}"
                tvOrderPrice.text = "₱ ${it.price.twoDecimal()}"
            } else {
                val hours = ((endTime - startTime) / (1000 * 60)) % 60
                tvPrice.text = "₱ ${(it.price * hours).twoDecimal()}"
                tvOrderPrice.text = "₱ ${(it.price * hours).twoDecimal()}"
            }
        }
    }
    private fun initUI() {
        msq_service?.let {
            tvServiceName.text = it.name
            updatePrice()
            if (it.image.isNullOrEmpty().not()) ivLogo.loadWallRound(it.image)
        }
        viewModel.mOrderPlacedEntity.value=null
        viewModel.mOrderPlacedEntity.observe(viewLifecycleOwner) {
            it?.let{
                if (it.success) {
                    bookingId = it.data.id.toString()
                    distance = /*it.data.distance*/"100"
                    findProviderCall()
                    createFindProviderPopup()
                }
            }
        }

        viewModel.mGetProfileInfoEntity.observe(viewLifecycleOwner) {
            if (it.success) {
//                viewModel.createStripeSecret(tvPrice.text.toString().replace("P", ""))
//                showFragment(FragConst.WALLET_PRO, bundleOf("amount" to tvPrice.text.toString().replace("P",""),
//                "stripeId" to it.data.id.toString()))
            }
        }
        viewModel.mFindProviderEntity.observe(viewLifecycleOwner) {
            it?.let {
                var msg="Great searching complete, we found good match for your service. You can see detail in booking list in Orders screen"
                if (it.success) {
                    msg="Great searching complete, we found good match for your service. You can see detail in booking list in Orders screen"
                }
                else{
                    canExit=true
                    msg="Sorry No provider available in this time, Please try later"
                }
                MaterialAlertDialogBuilder(requireActivity(),
                    R.style.ThemeOverlay_MaterialComponents_Light).apply {
                    setTitle("Searched Provider")
                    setMessage(msg)
                    setCancelable(false)
                    setPositiveButton("Okay") { dialog, which ->
                        dialog.dismiss()
                        if (findProviderCallCount==3) {
                            showFragment(FragConst.BOOKINGS)
                        }else findProviderPopup!!.show()
                    }
                }.create().apply {
//        window!!.setBackgroundDrawableResource(R.drawable.box_less_rounded_bg)
                    show()
                }
            }
        }
    }

    private fun findProviderCall() {
        findProviderCallCount += 1
        if (findProviderCallCount<3) {
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, (2 * 1000 * 60)+1)
        }
    }

    override fun onClick(v: View?) {
        when (v!!) {
            tvDateTime -> showDatePicker()
            btnPlaceOrder -> {
                if (endTime == 0L) toast(getString(R.string.plz_select_date))
                else if (endTime - startTime <= 0L) toast(getString(R.string.end_time_must_be_greater_then_start_time))
                else if (((endTime - startTime).mod (1000 * 60)) != 0) toast("Duration should be in multiple of 60 min.")
                else placeOrder()
            }
            btnPayAfterService, btnGCash, btnCard -> {
                selectedPayAfterService.isVisible = v == btnPayAfterService
                selectedGCash.isVisible = v == btnGCash
                selectedCard.isVisible = v == btnCard
            }
        }
    }

    private fun placeOrder() {
        val duration = (endTime - startTime) / (1000 * 60)
        val lat = pref.getPrefString(SessionManager.LAT) ?: "0"
        val long = pref.getPrefString(SessionManager.LNG) ?: "0"
        JsonObject().apply {
            addProperty("duration", duration)
            addProperty("serviceId", msq_service?.id)
            addProperty("bookingDate", "${bookingDate / 1000}")
            addProperty("bookingStartTime", "${startTime / 1000}")
            addProperty("bookingEndTime", "${endTime / 1000}")
//            addProperty("serviceProviderId", msq_service?.providerId)
            addProperty("paymentType", getPayType())
            addProperty("latitude", lat)
            addProperty("longitude", long)
//            addProperty("distance", "10000")
            viewModel.placeOrder(this)
        }

    }

    private fun getPayType() = when (View.VISIBLE) {
        selectedPayAfterService.visibility -> "1"
        selectedCard.visibility -> "2"
        selectedGCash.visibility -> "3"
        else -> "1"
    }

    fun showDatePicker() {
        val today = Calendar.getInstance()
        DatePickerDialog(requireActivity(),
            R.style.DialogTheme,
            object : DatePicker.OnDateChangedListener,
                DatePickerDialog.OnDateSetListener {
                override fun onDateChanged(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                    tvDateTime.text = "$p1-${if ((p2 + 1) < 10) "0${p2 + 1}" else "${p2 + 1}"}-$p3"
                    Calendar.getInstance().apply {
                        set(Calendar.YEAR, p1)
                        set(Calendar.MONTH, (p2))
                        set(Calendar.DAY_OF_MONTH, p3)
                        bookingDate = (this.timeInMillis)
                    }
                    showTimePicker(true)
                }
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun showTimePicker(forStartTime: Boolean = true) {
        val today = Calendar.getInstance()
        TimePickerDialog(requireActivity(),
            R.style.DialogTheme,
            object : TimePicker.OnTimeChangedListener,
                TimePickerDialog.OnTimeSetListener {
                override fun onTimeChanged(p0: TimePicker?, p1: Int, p2: Int) {

                }

                override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                    Calendar.getInstance().apply {
                        timeInMillis = bookingDate
                        set(Calendar.HOUR_OF_DAY, p1)
                        set(Calendar.MINUTE, (p2))
                        if (forStartTime) {
                            startTime = this.timeInMillis
                            showTimePicker(false)
                        } else
                            endTime = this.timeInMillis
                        updatePrice()
                    }
                }
            },
            today.get(Calendar.HOUR_OF_DAY),
            today.get(Calendar.MINUTE),
            true).apply {
            setTitle("${if (forStartTime) getString(R.string.start) else getString(R.string.end)} Time")
            show()
        }
    }

    override fun onDestroy() {
        dismissDialog()
        super.onDestroy()
    }
    override fun onDetach() {
        super.onDetach()
        handler.removeCallbacks(runnable)
    }


    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(receiver, IntentFilter("ACTION_LOCAL_NOTIFY"))
    }
    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
    }
    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "ACTION_LOCAL_NOTIFY") {
                if (intent.getStringExtra("type")!!
                        .equals("1") || intent.getStringExtra("type")!!
                        .equals("2")
                )
                {
                    dismissDialog()
                    MaterialAlertDialogBuilder(requireActivity(),
                        R.style.ThemeOverlay_MaterialComponents_Light).apply {
                        setMessage("Your booking request is accepted successfully. To see the bookings please click on Bookings.")
                        setCancelable(true)
                        setPositiveButton("Bookings") { dialog, which ->
                            findProviderPopup?.dismiss()
                            dismissDialog()
                            dialog.dismiss()
                        }.create().also {
                            it.setOnDismissListener {
                                showFragment(FragConst.BOOKINGS)
                            }
                        }.show()
                    }
                }            }
        }
    }

}