package com.msq.code.provider.wallet

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.SessionManager
import kotlinx.android.synthetic.main.fragment_wallet.*


class WalletFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvAvailableBalance.text="₱ ${pref.getPrefFloat(SessionManager.BALANCE)}"
        onClick()
        ivBack.setOnClickListener { showFragment(FragConst.HOME_PRO) }

    }

    private fun onClick() {
        tvTopup.setOnClickListener(this)
        tvBalanceDetails.setOnClickListener(this)
        tvBankInfo.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        tvAvailableBalance.text="₱ ${pref.getPrefFloat(SessionManager.BALANCE)}"
    }
    override fun onClick(v: View?) {
        when (v!!) {
            tvTopup->showFragment(FragConst.TOPUP_PRO)
            tvBalanceDetails->showFragment(FragConst.TRANSACTION_PRO)
            tvBankInfo->showFragment(FragConst.PAYMENT_METHODS)
//            tvSignupLink->showFragment(FragConst.HOME)0
        }
    }

}