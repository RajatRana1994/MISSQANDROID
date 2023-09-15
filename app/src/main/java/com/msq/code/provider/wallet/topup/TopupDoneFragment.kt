package com.msq.code.provider.wallet.topup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import kotlinx.android.synthetic.main.fragment_topup_done.*


class TopupDoneFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showFragment(FragConst.WALLET_PRO)
                }
            })
        return inflater.inflate(R.layout.fragment_topup_done, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentDone.text = "Top-up Payment Successful\\n"
        tvPaymentAmount.text = "${arguments?.getString("amount") ?: "0"}"
        onClick()

    }


    private fun onClick() {
        ivBack.setOnClickListener(this)
//        tvSignupLink.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivBack->showFragment(FragConst.WALLET_PRO)
//            tvSignupLink->showFragment(FragConst.HOME)
        }
    }

}