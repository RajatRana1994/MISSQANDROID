package com.msq.code.provider.booking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.BookingPROViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.DisplayAdapter
import com.msq.base.FragConst
import com.msq.util.ViewModelFactory
import com.msq.util.ext.getFormatTime
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.content_calender_with_header.*
import kotlinx.android.synthetic.main.fragment_booking_pro.*
import java.util.*


class BookingPROFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: BookingPROViewModel
    var page = 0
    var isPast=true
    val currentMonth = Calendar.getInstance(Locale.ENGLISH)
    val prevMonth = Calendar.getInstance(Locale.ENGLISH).apply { add(Calendar.MONTH, -1) }
    val NextMonth = Calendar.getInstance(Locale.ENGLISH).apply { add(Calendar.MONTH, +1) }
    var selectedDate = "NA"
    val weekList = arrayListOf<String>(
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_pro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingPROViewModel::class.java)
        selectedDate=Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        weekAdapter()
        showMonthViews()
//        getBookings()
        onClicks()

        observers()
    }

    private fun showMonthViews() {
        tvPrev.text = prevMonth.timeInMillis.getFormatTime("MMM")
        tvNext.text = NextMonth.timeInMillis.getFormatTime("MMM")
        currentDateLabel()
        generateCurrentData()
    }

    private fun currentDateLabel() {
        if (selectedDate.equals("NA").not()) currentMonth.set(Calendar.DAY_OF_MONTH, selectedDate.toInt())
        tvCurrent.text =
            currentMonth.timeInMillis.getFormatTime(if (selectedDate.equals("NA")) "MMM" else "dd, MMM yyyy")

        val today = Calendar.getInstance(Locale.ENGLISH)
        isPast=today.timeInMillis>currentMonth.timeInMillis
        if (today.timeInMillis.getFormatTime("dd-MM-yyyy")==currentMonth.timeInMillis.getFormatTime("dd-MM-yyyy")){
            isPast=true
            currentMonth.timeInMillis=currentMonth.apply {
                set(Calendar.HOUR_OF_DAY,23)
                set(Calendar.MINUTE,59)
            }.timeInMillis
        }
        getBookings()
    }

    private fun weekAdapter() {
        rvAdherenceCalWeek.adapter =
            DisplayAdapter(R.layout.item_cal_week_header, weekList.size) { adapter, holder, pos ->
                (holder as DisplayAdapter.DisplayHolder).view.findViewById<TextView>(R.id.tvWeekName).text =
                    weekList[pos]
            }
    }

    private fun generateCurrentData() {
        val dates = mutableListOf<String>().also {
            it.addAll(addEmptyDateSpaceIfNeeded())
            (1..currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)).forEach { dt -> it.add(if (dt < 10) "0$dt" else "$dt") }
        }
        rvAdherenceCalDay.adapter =
            DisplayAdapter(R.layout.item_cal_date, dates.size) { adapter, holder, pos ->
                (holder as DisplayAdapter.DisplayHolder).view.apply {
                    findViewById<CheckedTextView>(R.id.tvWeekName).also {
                        it.text = if (dates[pos].isNotEmpty()) "${dates[pos].toInt()}" else ""
                        it.isChecked = dates[pos] == selectedDate
                        it.setOnClickListener {
                            if (dates[pos].isEmpty()) return@setOnClickListener
                            if (selectedDate==dates[pos]){
                                return@setOnClickListener
//                                selectedDate="NA"
//                                getBookings()
//                                isPast=true
                            }else {
                                selectedDate = dates[pos]
                                page = 0
                                currentDateLabel()

                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }

    private fun addEmptyDateSpaceIfNeeded() = arrayListOf<String>().apply {
        val first =
            Calendar.getInstance(Locale.ENGLISH).apply { timeInMillis = currentMonth.timeInMillis }
                .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        repeat((1.until(getWeekNum(first!!))).count()) { add("") }
    }

    fun getWeekNum(week: String) = when (week.toUpperCase(Locale.ENGLISH)) {
        "SUNDAY" -> Calendar.SUNDAY
        "MONDAY" -> Calendar.MONDAY
        "TUESDAY" -> Calendar.TUESDAY
        "WEDNESDAY" -> Calendar.WEDNESDAY
        "THURSDAY" -> Calendar.THURSDAY
        "FRIDAY" -> Calendar.FRIDAY
        "SATURDAY" -> Calendar.SATURDAY
        else -> Calendar.SUNDAY
    }

    private fun observers() {
        viewModel.mAcceptRejectBookingEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                page=0
                getBookings()
            }
            toast(it.message)
        }
        viewModel.mBookingListEntity.observe(viewLifecycleOwner) {
            rvOrders.isVisible = it.data.bookings.isNotEmpty()
            tvNoBookings.isVisible = it.data.bookings.isEmpty()
            if (it.data.bookings.isEmpty()) return@observe
            rvOrders.adapter = BookingPROAdapter(it.data.bookings) { t, p ->
                when (t) {
                    "accept" -> viewModel.acceptReject(it.data.bookings[p].id.toString(), "1")
                    "reject" -> viewModel.acceptReject(it.data.bookings[p].id.toString(), "2")
                    "detail" -> {
                        showFragment(FragConst.ORDER_DETAIL_PRO,
                            bundleOf("booking_id" to it.data.bookings[p].id.toString()))
                    }
                }
            }
        }
    }


    private fun getBookings() {
        hashMapOf<String, String>().apply {
            put("limit", "200")
            put("page", "${++page}")
            put("isPast", if (selectedDate.equals("NA")) "false" else "$isPast")
            if (selectedDate.equals("NA").not()) put("date", (currentMonth.timeInMillis/1000).toString())
            viewModel.getBookings(this)
        }
    }

    private fun onClicks() {
        tvPrev.setOnClickListener(this)
        tvNext.setOnClickListener(this)
//        btnGetStarted.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            tvPrev -> updateMonths(false)
            tvNext -> updateMonths(true)
//            btnGetStarted -> {
//                showFragment(FragConst.GETSTARTED)
//            }
        }
    }

    private fun updateMonths(isNext: Boolean) {
        page = 0
        prevMonth.add(Calendar.MONTH, if (isNext) +1 else -1)
        currentMonth.add(Calendar.MONTH, if (isNext) +1 else -1)
        NextMonth.add(Calendar.MONTH, if (isNext) +1 else -1)
        showMonthViews()
    }

    override fun onResume() {
        super.onResume()
        page = 0
        getBookings()
    }

}