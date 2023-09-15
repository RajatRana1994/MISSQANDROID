package com.msq.code.provider.orderdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.BookingPROViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.rest.entity.BookingDetailData
import com.msq.util.ConstUtils
import com.msq.util.ConstUtils.Companion.paymentType
import com.msq.util.ViewModelFactory
import com.msq.util.ext.*
import kotlinx.android.synthetic.main.fragment_order_detail.*
import java.util.*


class OrderDetailFragment : BaseFragment(), View.OnClickListener {

    lateinit var viewModel: BookingPROViewModel
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    var setup = 0
    var isRated = 0
    var userId = ""
    var isPaymentDone = false

    //    val order_data by lazy { requireArguments().getParcelable<BookingListParseData>("order_data") }
    var isAccept = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
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
        viewModel.mAcceptRejectBookingEntity.observe(viewLifecycleOwner) {
            it?.let {
                toast(it.message)
                if (it.success) {
                    viewModel.mAcceptRejectBookingEntity.value = null
                    if (isAccept) {
                        if (isPaymentDone)
                            showFragment(FragConst.LOCATION_PRO,
                                bundleOf("booking_id" to booking_id))
                        else makeToast("Can't start service, Due to pending payment!")
                    } else
                        requireActivity().onBackPressed()
                }
            }
        }
        viewModel.mBookingDetailEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                showOrderData(it.data)
            }
        }
    }

    private fun showOrderData(data: BookingDetailData) {
        data?.let {
            setup = it.setup
            isRated = it.isRated
            isPaymentDone=it.paymentType==1 || (it.paymentType==2 && it.isPaymentDone==1)
            tvTitle.text = it.serviceDetails.name.capitalize(Locale.ENGLISH)
            "${(it.bookingDate * 1000).getFomatDayName("dd MMM")} ${
                (it.bookingStartTime * 1000).getFormatTime("hh:mm a").capitalize(Locale.ENGLISH)
            }".also { tvDayTime.text = it }
            tvTimeStartEnd.text = "${
                (it.bookingStartTime * 1000).getFormatTime("hh:mm a").uppercase(Locale.ENGLISH)
            }\n${(it.bookingEndTime * 1000).getFormatTime("hh:mm a").uppercase(Locale.ENGLISH)}"
            tvOrderId.text = "${getString(R.string.order_id)} #${it.id}"
            tvTotalQuoted.text = "₱ ${it.totalPrice.twoDecimal()}"
            lblService.text = it.serviceDetails.name.capitalize(Locale.ENGLISH)
            tvCustomerNum.text =
                "${it.userInfo.userName.capitalize(Locale.ENGLISH)}\n${it.userInfo.userPhone}"
            tvCustomerAddress.text = if(it.userInfo.userAddress.isEmpty()) "Not Provided" else it.userInfo.userAddress.capitalize(Locale.ENGLISH)
            tvCustomerLocation.text = if(it.userInfo.userAddress.isEmpty()) "Not Provided" else it.userInfo.userAddress.capitalize(Locale.ENGLISH)
            tvCustomerSitepoint.text = if(it.userInfo.userAddress.isEmpty()) "Not Provided" else it.userInfo.userAddress.capitalize(Locale.ENGLISH)
            tvPayablePaidRewards.text =
                "₱ ${it.totalPrice.twoDecimal()}\n\n₱ ${(it.alreadyPaidAmount ?: 0)}\n\n₱ ${it.orderReward ?: 0}"
            tvServiceCharge.text = "₱ ${it.serviceDetails.price.twoDecimal()}"
            tvBalance.text = "₱ ${it.totalPrice.minus(it.alreadyPaidAmount ?: 0.0)}"
            tvServiceSubtotal.text = "₱ ${it.totalPrice.twoDecimal()}"
            tvOrderTotal.text = "₱ ${it.totalPrice.twoDecimal()}"
            tvPaymentMethod.text = "${it.paymentType}".paymentType()
            with(tvReqApproved) {
                isVisible = it.status != 0
                "${
                    if (it.status == 1) getString(R.string.your_request_has_been_approved) else if (it.status != 3 || it.status != 0) getString(
                        R.string.your_request_has_been_declined) else getString(R.string.completed)
                }".also {
                    text = it
                }
            }
            userId = it.userId.toString()
            btnAccept.text = if (it.status == 1) {
                when (it.setup) {
                    0, 1, 2, 3 -> getString(R.string.complete_setup)
                    4 -> getString(R.string.completed)
                    else -> getString(R.string.accept)
                }
            } else if (it.status == 3) getString(R.string.completed)
            else {
//                btnAccept.isVisible = false
                getString(R.string.accept)
            }
            btnAccept.isVisible = it.status != 2
            btnDecline.isVisible = it.status == 0 || it.status == 2
            if (it.status == 2) {
                btnDecline.text = getString(R.string.rejected)
            }
        }
    }

    private fun onClicks() {
        ivBack.setOnClickListener(this)
        btnAccept.setOnClickListener(this)
        btnDecline.setOnClickListener(this)
        tvContactServiceManagement.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivBack -> requireActivity().onBackPressed()
            btnAccept -> {
                val data = viewModel.mBookingDetailEntity.value?.data!!
                if (btnAccept.text.toString() == getString(R.string.completed) && isRated == 0) {
                    showFragment(FragConst.REVIEW_PRO,
                        bundleOf("booking_id" to booking_id,
                            "user_id" to userId,
                            "rate_to" to ConstUtils.RATE_TO_USER))
                } else if (btnAccept.text.toString() == getString(R.string.complete_setup)) {
                    if (isPaymentDone) {
                        if (setup == 0)
                            showFragment(FragConst.LOCATION_PRO,
                                bundleOf("booking_id" to booking_id,"service_name" to tvTitle.text.toString()))
                        else if (setup == 1)
                            showFragment(FragConst.CONTACTED_ORDER_CLIENT_PRO,
                                bundleOf("booking_id" to booking_id,"service_name" to tvTitle.text.toString()))
                        else if (setup == 2)
                            showFragment(FragConst.ORDER_PROOF_PRO,
                                bundleOf("booking_id" to booking_id,"service_name" to tvTitle.text.toString()))
                        else if (setup == 3)
                            showFragment(FragConst.COMPLETE_ORDER_SETTLE_PRO,
                                bundleOf("booking_id" to booking_id,"service_name" to tvTitle.text.toString()))
                        else if (setup == 4 && isRated == 0)
                            showFragment(FragConst.REVIEW_PRO,
                                bundleOf("booking_id" to booking_id,
                                    "user_id" to userId,
                                    "rate_to" to ConstUtils.RATE_TO_USER,
                                    "frag_from_tag" to FragConst.BOOKING_PRO))
                    } else makeToast("Can't start service, Due to pending payment!")
                } else if (btnAccept.text.toString() == getString(R.string.accept)){
                    isAccept = true
                    viewModel.acceptReject(booking_id, "1")
                }
            }
            btnDecline -> {
                if (btnDecline.text.toString().equals(getString(R.string.rejected)).not()) {
                    isAccept = false
                    viewModel.acceptReject(booking_id, "2")
                }
            }
            tvContactServiceManagement -> {
                showFragment(FragConst.CHAT_BOOK_PRO,
                    bundleOf("booking_id" to booking_id, "user_id" to userId))
            }
        }
    }

}