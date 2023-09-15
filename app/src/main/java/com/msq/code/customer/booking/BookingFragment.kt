package com.msq.code.customer.booking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buuzconnect.uis.addevent.BookingViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_booking.*


class BookingFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: BookingViewModel
    var page = 0
    var loadmore = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingViewModel::class.java)
        getBookings()
        onClicks()
        observers()
        rvOrders.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) =
                super.onScrollStateChanged(recyclerView, newState)

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (rvOrders.adapter == null) return
                val lastPos =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (loadmore && lastPos == (rvOrders.adapter?.itemCount!!.minus(1))) {
                    getBookings()
                }
            }
        })
    }

    private fun observers() {
        viewModel.mPaymentEntity.observe(viewLifecycleOwner) {
            it?.let {
                if (it.second.success) {
                    showFragment(FragConst.PAYMENT_PAYMONGO,
                        bundleOf("checkout_url" to it.second.data.paymongo.data.attributes.checkout_url, "booking_id" to it.first))
                    viewModel.mPaymentEntity.value=null
                }
            }
        }
        viewModel.mStripeSecretEntity.observe(viewLifecycleOwner) {
            it?.let {
                if (it.second.success) {
                    startActivity(Intent(requireActivity(),GPayActivity::class.java).putExtra("client_secret" , it.second.data.charge.client_secret))
//                    showFragment(FragConst.ADD_CARDS,
//                        bundleOf("client_secret" to it.second.data.charge.client_secret,
//                            "pay_amount" to it.second.data.charge.amount.toString(),
//                            "booking_id" to it.first))
                }
            }
        }
        viewModel.mBookingListEntity.observe(viewLifecycleOwner) {
            loadmore = it.data.pagination.currentPage < it.data.pagination.totalPage
            rvOrders.adapter = BookingAdapter(it.data.bookings) { t, p ->
                when (t) {
                    "payment" -> {
                        val duration=(it.data.bookings[p].bookingEndTime - it.data.bookings[p].bookingStartTime) / (60) // already time is in seconds
                        val price=if(duration==0L) it.data.bookings[p].totalPrice.toInt() else it.data.bookings[p].totalPrice.toInt()* (duration/60)
                        if (price.toInt()<100){
                            toast("Min payment amount should be 100")
                            return@BookingAdapter
                        }
//                        startActivity(Intent(requireActivity(),GPayActivity::class.java).putExtra("client_secret" , "it.second.data.charge.client_secret"))
                        doPayment((price.toInt() * 100).toString(),
                            it.data.bookings[p].providerInfo.providerStripeAccountId.toString(),
                            it.data.bookings[p].id.toString())
                    }
                    else -> {
                        showFragment(FragConst.PURCHASE_DETAIL,
                            bundleOf("booking_id" to it.data.bookings[p].id.toString()))
                    }
                }
            }
        }
    }

    private fun doPayment(
        totalPrice: String,
        providerStripeAccountId: String,
        bookingId: String,
    ) {
        viewModel.paymentIntentPaymongo(totalPrice, pref.getPrefString(SessionManager.USER_ID)!!, bookingId)
//        viewModel.createStripeSecret(totalPrice, providerStripeAccountId, bookingId)
    }

    private fun getBookings() {
        if (pref.getPrefIsLogin(SessionManager.IS_LOGIN).not()) return
        hashMapOf<String, String>().apply {
            put("limit", "200")
            put("page", "${++page}")
//            put("isPast", "false")
            viewModel.getBookings(this)
        }
    }

    private fun onClicks() {
//        btnGetStarted.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
//            btnGetStarted -> {
//                showFragment(FragConst.GETSTARTED)
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.mStripeSecretEntity.value = null
        page = 0
    }
}