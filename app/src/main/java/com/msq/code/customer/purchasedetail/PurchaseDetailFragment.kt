package com.msq.code.customer.purchasedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.BookingViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.rest.entity.BookingDetailData
import com.msq.util.ConstUtils
import com.msq.util.ViewModelFactory
import com.msq.util.ext.drawableSmall
import com.msq.util.ext.getFormatTime
import com.msq.util.ext.twoDecimal
import com.socialgalaxyApp.util.extension.loadUserImage
import kotlinx.android.synthetic.main.fragment_purchase_detail.*


class PurchaseDetailFragment : BaseFragment(), View.OnClickListener {
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    var userId=""
    lateinit var viewModel: BookingViewModel
    var booking_data: BookingDetailData? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchase_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClicks()
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingViewModel::class.java).also {
                    it.bookingDetail(booking_id)
                }
        initUI()
        tvPurchaseAddress.drawableSmall(R.drawable.ic_location)
        viewModel.mBookingDetailEntity.observe(viewLifecycleOwner) {
            userId=it.data.serviceProviderId.toString()
            tvPurchaseDate.text = (it.data.bookingStartTime * 1000).getFormatTime("MMMM dd,yyyy")
            tvPurchaseName.text = it.data.providerInfo.providerName
            tvPurchasePhone.text = it.data.providerInfo.providerPhone
            tvPurchaseAddress.text = it.data.serviceProviderLocation?.address ?: ""
            val price=if(it.data.duration==0)it.data.totalPrice.toInt().toDouble() else (it.data.totalPrice.toInt()* (it.data.duration/60)).toDouble()

            tvTotalOrder.text = "₱ ${price.twoDecimal()}"
            booking_data = it.data
            tvTrack.isVisible = true
            if (it.data.status == 1 || it.data.status == 2) {
                tvStatus.isVisible = true
                tvStatus.text =
                    "${if (it.data.status == 1) "Your request has been Approved" else "Your request has been Rejected"}"
            }
            if (it.data.serviceDetails.image.isNotEmpty()) ivService.loadUserImage(it.data.serviceDetails.image)
            tvServiceName.text =
                it.data.serviceDetails.name
            tvServiceDuration.text =
                "${((it.data.bookingEndTime * 1000) - (it.data.bookingStartTime * 1000)) / (1000 * 60)} Minutes"
            tvTimeStart.text = (it.data.bookingStartTime * 1000).getFormatTime("hh:mm a")
            tvTimeEnd.text = (it.data.bookingEndTime * 1000).getFormatTime("hh:mm a")
            tvServiceAmount.text = "₱ ${price.twoDecimal()}"
            btnGiveProviderReview.isVisible = it.data.status == 3 && it.data.setup == 4&& it.data.isRated==0
        }

    }

    private fun onClicks() {
        tvTrack.setOnClickListener(this)
        btnGiveProviderReview.setOnClickListener(this)
    }

    private fun initUI() {

    }

    override fun onClick(v: View?) {
        when (v!!) {
            btnGiveProviderReview -> {
                showFragment(FragConst.REVIEW_PRO,
                    bundleOf("booking_id" to booking_id, "user_id" to userId, "rate_to" to ConstUtils.RATE_TO_PROVIDER,"frag_from_tag" to FragConst.BOOKINGS))
            }
            tvTrack -> {
                showFragment(FragConst.TRACK_BOOKING,
                    bundleOf("booking_status" to booking_data?.status.toString(),
                        "booking_setup" to booking_data?.setup,
                        "bookingDate" to booking_data?.bookingDate,
                        "bookingStartTime" to booking_data?.bookingStartTime,
                        "bookingEndTime" to booking_data?.bookingEndTime,
                        "placed_date" to booking_data?.created,
                        "providerName" to booking_data?.providerInfo?.providerName,
                        "booking_id" to booking_id,
                        "address" to (booking_data?.serviceProviderLocation?.address ?: "")))
            }
        }
    }

}