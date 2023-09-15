package com.msq.code.terms

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.ProfilePROViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_terms_condition.*


class TermsFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: ProfilePROViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_condition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ProfilePROViewModel::class.java)
        onClicks()
        viewModel.getAppInfo()

        viewModel.mTermsAndPolicyEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                val termsData = it.data.filter { it.key_pair.contains("term", true) }
                if (termsData.isNotEmpty()) tvContent.text = Html.fromHtml(termsData[0].value)
                tvTerms.setOnClickListener {v->
                    if (termsData.isNotEmpty()) tvContent.text = Html.fromHtml(termsData[0].value)
                    scrollingView.fullScroll(ScrollView.FOCUS_UP)
                }
                tvPrivacy.setOnClickListener {v->
                    val privacyData = it.data.filter { it.key_pair.contains("privacy", true) }
                    if (privacyData.isNotEmpty()) tvContent.text = Html.fromHtml(privacyData[0].value)
                    scrollingView.fullScroll(ScrollView.FOCUS_UP)
                }
            } else {
                toast(getString(R.string.something_went_wrong))
                requireActivity().onBackPressed()
            }
        }
    }

    private fun onClicks() {
        ivBack.setOnClickListener(this)
        ivClose.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivBack, ivClose -> {
                requireActivity().onBackPressed()
            }
        }
    }

}