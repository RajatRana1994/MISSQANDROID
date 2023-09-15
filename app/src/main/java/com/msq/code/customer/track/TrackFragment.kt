package com.msq.code.customer.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.msq.BaseFragment
import com.msq.R
import com.msq.util.ext.drawableSmall
import com.msq.util.ext.getFormatTime
import kotlinx.android.synthetic.main.fragment_track.*
import java.util.*


class TrackFragment : BaseFragment(), View.OnClickListener {
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    val booking_status by lazy { requireArguments().getString("booking_status") ?: "" }
    val booking_setup by lazy { requireArguments().getInt("booking_setup") }
    val providerName by lazy { requireArguments().getString("providerName") ?: "" }
    val address by lazy { requireArguments().getString("address") ?: "" }
    val placed_date by lazy { requireArguments().getLong("placed_date") }
    val bookingDate by lazy { requireArguments().getLong("bookingDate") }
    val bookingStartTime by lazy { requireArguments().getLong("bookingStartTime") }
    val bookingEndTime by lazy { requireArguments().getLong("bookingEndTime") }
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        onClicks()
        initUI()
        tvCustomerName.drawableSmall(R.drawable.ic_user)
        tvCustomerLocation.drawableSmall(R.drawable.ic_location)
        tvCustomerName.text = providerName
        tvCustomerLocation.text = address
        tvTrackNum.text = booking_id
        tvCompletedDate.text = "${(bookingDate * 1000).getFormatTime("MMMM dd")}\n${
            (bookingEndTime * 1000).getFormatTime("hh:mm a")
        }"
        tvOnTheWayDate.text = "${(bookingDate * 1000).getFormatTime("MMMM dd")}\n${
            (bookingStartTime * 1000).getFormatTime("hh:mm a")
        }"
        tvTravellingDate.text = "${(bookingDate * 1000).getFormatTime("MMMM dd")}\n${
            (bookingStartTime * 1000).getFormatTime("hh:mm a")
        }"
        tvPlaceServiceDate.text = "${(placed_date * 1000).getFormatTime("MMMM dd")}\n${
            (bookingStartTime * 1000).getFormatTime("hh:mm a")
        }"
        if (booking_status == "2") applyBookingStatusRejected()
        else applyBookingStatus(booking_setup)
    }

    private fun applyBookingStatus(booking_setup: Int) {
        tvCompleted.isChecked = booking_setup == 4
        tvOnTheWay.isChecked =
            booking_setup >= 1 /*&& (bookingDate * 1000).getFormatTime("dd-MM-yyyy") == calendar.timeInMillis.getFormatTime(
                "dd-MM-yyyy")*/
        tvTravelling.isChecked =
            booking_setup >= 1 /*&& (bookingDate * 1000).getFormatTime("dd-MM-yyyy") == calendar.timeInMillis.getFormatTime(
                "dd-MM-yyyy")*/
        tvPlaceService.isChecked = true
    }

    private fun applyBookingStatusRejected() {
        tvCompleted.isChecked = false
        tvOnTheWay.isChecked = false
        tvTravelling.isChecked = false
        tvPlaceService.isChecked = false
    }

    private fun onClicks() {
//        btnGetStarted.setOnClickListener(this)
    }

    private fun initUI() {
//        tvServiceName.text = service_name
    }

    override fun onClick(v: View?) {
        when (v!!) {
//            btnGetStarted->{
//                showFragment(FragConst.GETSTARTED)
//            }
        }
    }

}