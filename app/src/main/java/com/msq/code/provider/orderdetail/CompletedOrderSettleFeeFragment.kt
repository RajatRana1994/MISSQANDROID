package com.msq.code.provider.orderdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.BookingPROViewModel
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.ViewModelFactory
import com.msq.util.ext.commonPopup
import com.msq.util.ext.getFormatTime
import com.msq.util.ext.toast
import com.msq.util.ext.twoDecimal
import kotlinx.android.synthetic.main.fragment_complete_order_settle_fee.*


class CompletedOrderSettleFeeFragment : BaseFragment(), View.OnClickListener {
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    lateinit var viewModel: BookingPROViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_order_settle_fee, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingPROViewModel::class.java).also {
                    it.bookingDetail(booking_id)
                }
        onClicks()
        observers()

    }

    private fun observers() {
        viewModel.mBookingStepEntity.observe(viewLifecycleOwner) {
            it?.let {
                toast(it.message)
                if (it.success && btnCompleted.isEnabled) {
                    btnCompleted.isEnabled = false
                    val id = it.data.bookingDetails.userId.toString()
                    viewModel.mBookingStepEntity.value = null
                    showFragment(FragConst.COMPLETED_ORDER_PRO,
                        bundleOf("booking_id" to booking_id,
                            "user_id" to id))
                }
            }
        }
        viewModel.mBookingDetailEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                (it.data).also {
                    lblContact.text =
                        "${getString(R.string.order_contact)} ${it.userInfo.userPhone}"
                    tvDateTime.text = "${(it.bookingDate * 1000).getFormatTime("MMMM dd")}, ${
                        (it.bookingStartTime * 1000).getFormatTime("hh:mm a")
                    }"
                    tvCustomerAddress.text = "${it.serviceProviderLocation?.address ?: ""}"
                    tvLocationName.text = "${it.serviceProviderLocation?.city ?: ""}"
                    tvCoutomerName.text = "${it.providerInfo?.providerName ?: ""}"
                    tvCustomerAddress2.text = "${it.userInfo?.userAddress ?: ""}"
                    tvLocationName2.text = "${it.userInfo?.userCity ?: ""}"
                    tvCoutomerName2.text = "${it.userInfo?.userName ?: ""}"
                    tvOrderId.text = "${getString(R.string.order_id)} #{it.id}"
                    tvServiceCharge.text = "₱ ${it.totalPrice.twoDecimal()}"
                }
            }
        }
    }

    private fun onClicks() {
        btnCompleted.setOnClickListener(this)
//        btnDecline.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            btnCompleted -> {
                (context as AppCompatActivity).commonPopup(R.layout.dailog_default) { v, d ->
                    v.findViewById<TextView>(R.id.btnYes).setOnClickListener {
                        d.dismiss()
                        viewModel.bookingSteps(booking_id, "4", JsonObject())
                    }
                    v.findViewById<TextView>(R.id.btnReturn).setOnClickListener {
                        d.dismiss()
                    }
                }
//                showFragment(FragConst.LOCATION_PRO)
            }
//            btnDecline->{
//                requireActivity().onBackPressed()
//            }
        }
    }

}