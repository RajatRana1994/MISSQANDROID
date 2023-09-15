package com.msq.code.customer.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.ProfileViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_review.*


class ReviewFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(),
            ViewModelFactory.getInstance(requireActivity())).get(ProfileViewModel::class.java)
            .also {
                it.getRating(pref.getPrefString(SessionManager.USER_ID)!!,
                    ConstUtils.RATE_TO_USER)
            }
        onClicks()
        viewModel.mGetReviewEntity.observe(viewLifecycleOwner, {
            rbOverAll.rating = it.data.ratings.map { it.rating }.average().toFloat()
            if (it.success) {
                rvReview.adapter = ReviewAdapter(it.data.ratings) { t, p ->
                }
            }
        })

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

}