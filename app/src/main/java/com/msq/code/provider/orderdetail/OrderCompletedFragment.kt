package com.msq.code.provider.orderdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.BookingPROViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.ConstUtils
import com.msq.util.ViewModelFactory
import com.msq.util.ext.getFormatTime
import com.msq.util.ext.twoDecimal
import kotlinx.android.synthetic.main.fragment_order_completed.*


class OrderCompletedFragment : BaseFragment(), View.OnClickListener {
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    val user_id by lazy { requireArguments().getString("user_id") ?: "" }
    lateinit var viewModel: BookingPROViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_completed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingPROViewModel::class.java).also {
                    it.bookingDetail(booking_id)
                }
        onClicks()
        viewModel.mBookingDetailEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                (it.data).also {
                    tvOrderId.text = "${getString(R.string.order_id)} #${it.id}"
                    tvCashOrder.text = "₱ ${it.totalPrice.twoDecimal()}"
                    lblContact.text =
                        "${getString(R.string.order_contact)} ${it.userInfo.userPhone}"
                    tvDateTime.text = "${(it.bookingDate*1000).getFormatTime("MMMM dd")}, ${
                        (it.bookingStartTime*1000).getFormatTime("hh:mm a")
                    }"
                }
            }
        }
    }

    private fun onClicks() {
        btnGiveUserReview.setOnClickListener(this)
        ivBack.setOnClickListener(this)
//        btnDecline.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivBack -> requireActivity().onBackPressed()
            btnGiveUserReview -> {
                showFragment(FragConst.REVIEW_PRO,
                    bundleOf("booking_id" to booking_id, "user_id" to user_id, "rate_to" to ConstUtils.RATE_TO_USER,"frag_from_tag" to FragConst.BOOKING_PRO))
            }
//            btnDecline->{
//                requireActivity().onBackPressed()
//            }
        }
    }

}