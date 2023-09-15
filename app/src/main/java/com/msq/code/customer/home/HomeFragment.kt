package com.msq.code.customer.home

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
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
import com.msq.rest.entity.BookingListParseData
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.addWatcher
import com.msq.util.ext.toast
import com.msq.util.ext.toastAction
import com.socialgalaxyApp.util.extension.loadUserImage
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: BookingViewModel
    var page = 0
    var loadmore = false

    var bookingsData= arrayListOf<BookingListParseData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingViewModel::class.java)
        pref.getUserData()?.let {
            tvUser.text="${getString(R.string.hi)}, ${it.name}"
            if (it.profile.isNotEmpty()) ivUser.loadUserImage(it.profile)
        }
        page=0
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

        edtBookService.addWatcher { search ->
            val bookings=if (search.isNullOrEmpty()) bookingsData else bookingsData.filter { it.serviceDetails.name.contains(search,true)||(it.providerInfo?.providerName?:"").contains(search,true) }
            rvOrders.adapter = HomeOrderAdapter(bookings) { t, p ->
                showFragment(FragConst.HOME_PURCHASE_DETAIL,
                    bundleOf("booking_id" to bookings[p].id.toString()))
            }
        }
    }

    private fun observers() {
        viewModel.mBookingListEntity.observe(viewLifecycleOwner) {
            loadmore = it.data.pagination.currentPage < it.data.pagination.totalPage
            bookingsData.clear()
            bookingsData.addAll(it.data.bookings)
            rvOrders.adapter = HomeOrderAdapter(it.data.bookings) { t, p ->
                showFragment(FragConst.HOME_PURCHASE_DETAIL,
                    bundleOf("booking_id" to it.data.bookings[p].id.toString()))
            }
        }
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

    override fun onResume() {
        super.onResume()
        page=0
        pref.getUserData()?.let {
            tvUser.text="${getString(R.string.hi)}, ${it.name}"
            if (it.profile.isNotEmpty()) ivUser.loadUserImage(it.profile)
        }
    }

    private fun onClicks() {
        ivUser.setOnClickListener(this)
        btnBook.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivUser -> showFragment(FragConst.PROFILE)
            btnBook -> {
                if (checkLocation())
                showFragment(FragConst.SERVICE_BOOK)
                else toastAction("Please enable location!","Enable"){
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }
        }
    }

    fun checkLocation(): Boolean {
        var gps_enabled = false
        var network_enabled = false
        val lm: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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


}