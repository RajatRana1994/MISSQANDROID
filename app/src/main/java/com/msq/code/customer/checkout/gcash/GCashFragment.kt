package com.msq.code.customer.checkout.gcash

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.CheckoutViewModel
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.rest.entity.NearServiceResult
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import com.msq.util.ext.twoDecimal
import com.socialgalaxyApp.util.extension.loadWallRound
import kotlinx.android.synthetic.main.fragment_checkout.*
import java.util.*


class GCashFragment : BaseFragment(), View.OnClickListener {
    val msq_service by lazy { requireArguments().getParcelable<NearServiceResult>("msq_service") }
    lateinit var viewModel: CheckoutViewModel

    var bookingDate = 0L
    var startTime = 0L
    var endTime = 0L
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
        viewModel.mOrderPlacedEntity.observe(viewLifecycleOwner, {
            if (it.success) {
                toast(it.message)
                showFragment(FragConst.HOME)
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

    private fun initUI() {
        msq_service?.let {
            tvServiceName.text = it.name
            tvPrice.text = "₱ ${it.price.twoDecimal()}"
            tvOrderPrice.text = "₱ ${it.price.twoDecimal()}"
            if (it.image.isNullOrEmpty().not()) ivLogo.loadWallRound(it.image)
        }
    }

    override fun onClick(v: View?) {
        when (v!!) {
            tvDateTime -> showDatePicker()
            btnPlaceOrder -> {
                if (endTime == 0L) toast("Select Date & Time")
                else if (endTime - startTime <= 0L) toast("Select Valid Time")
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
        JsonObject().apply {
            addProperty("duration", "$duration")
            addProperty("serviceId", msq_service?.id)
            addProperty("bookingDate", "${bookingDate / 1000}")
            addProperty("bookingStartTime", "${startTime / 1000}")
            addProperty("bookingEndTime", "${endTime / 1000}")
            addProperty("serviceProviderId", msq_service?.providerId)
            addProperty("paymentType", getPayType())
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
                        set(Calendar.MONTH, (p2 + 1))
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
        TimePickerDialog(requireActivity(), object : TimePicker.OnTimeChangedListener,
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
                }
            }
        }, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE),
            true).apply {
            setTitle("${if (forStartTime) getString(R.string.start) else getString(R.string.end)} Time")
            show()
        }
    }


}