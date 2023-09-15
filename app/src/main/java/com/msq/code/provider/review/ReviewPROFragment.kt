package com.msq.code.provider.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.BookingPROViewModel
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.rest.entity.AuthData
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import com.socialgalaxyApp.util.extension.loadUserImage
import kotlinx.android.synthetic.main.fragment_review_pro.*


class ReviewPROFragment : BaseFragment(), View.OnClickListener {
    val rate_to by lazy { requireArguments().getString("rate_to") ?: "" }
    val frag_from_tag by lazy { requireArguments().getString("frag_from_tag") ?: "" }
    val user_id by lazy { requireArguments().getString("user_id") ?: "" }
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }


    lateinit var viewModel: BookingPROViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_pro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingPROViewModel::class.java).also {
                    it.getProfileInfo(user_id)
                }
        onClicks()
        observers()

    }

    private fun observers() {
        viewModel.mGiveRatingEntity.observe(viewLifecycleOwner) {
            toast(it.message)
            if (it.success) {
                if (frag_from_tag.isEmpty())
                    requireActivity().onBackPressed()
                else showFragment(frag_from_tag)
            }
        }
        viewModel.mGetProfileInfoEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                setUserData(it.data)
            } else {
                toast(it.message)
                requireActivity().onBackPressed()
            }
        }
    }

    private fun setUserData(data: AuthData) {
        tvRating.text = "${data.rating ?: 0f}"
        tvName.text = data.name ?: ""
        ivUser.loadUserImage(data.profile)
        tvPhone.text = data.phone ?: ""
        tvCustomerAddress.text = data.address ?: ""
        pb5.progress = data.ratingStars.rating5 ?: 0f
        pb4.progress = data.ratingStars.rating4 ?: 0f
        pb3.progress = data.ratingStars.rating3 ?: 0f
        pb2.progress = data.ratingStars.rating2 ?: 0f
        pb1.progress = data.ratingStars.rating1 ?: 0f
    }

    private fun onClicks() {
        btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            btnSubmit -> {
                if (edtReview.text.toString().isEmpty()) toast(getString(R.string.plz_enter_review))
                else if (ivRatingBar.rating == 0f) toast("Please give rating!")
                else JsonObject().also {
                    it.addProperty("bookingId", booking_id)
                    it.addProperty("comment", edtReview.text.toString())
                    it.addProperty("rating", ivRatingBar.rating.toString())
                    viewModel.giveRating(user_id, rate_to, it)
                }
            }
        }
    }

}