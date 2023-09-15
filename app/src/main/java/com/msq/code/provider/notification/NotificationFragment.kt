package com.msq.code.provider.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buuzconnect.uis.addevent.ProfilePROViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.rest.entity.Notification
import com.msq.util.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_notification.*


class NotificationFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: ProfilePROViewModel
    var page = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ProfilePROViewModel::class.java).also {
                    it.notification("${++page}")
                }
        onClick()
        rvShare.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastPos =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
//                if (lastPos==)
            }
        })

        viewModel.mNotificationEntity.observe(viewLifecycleOwner) {
            rvShare.adapter = NotificationAdapter(it ?: arrayListOf<Notification>()) { t, p ->
                val id = it[p].bookingId.toString()
                if (it[p].notificationCode == 3) {
                    showFragment(FragConst.ORDER_DETAIL_PRO,
                        bundleOf("booking_id" to id))
                }
            }
            noNotifications.isVisible=(it ?: arrayListOf<Notification>()).isEmpty()
        }
    }

    private fun onClick() {
        ivBack.setOnClickListener(this)
//        tvSignupLink.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivBack-> {requireActivity().onBackPressed()}
//            tvSignupLink->showFragment(FragConst.HOME)
        }
    }

    override fun onResume() {
        super.onResume()
        page = 0
    }
}