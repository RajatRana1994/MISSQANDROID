package com.msq.code.provider.wallet.topup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.buuzconnect.uis.addevent.WalletViewModel
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.isInternetAvailable
import com.msq.util.ext.loading
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_topup.*


class TopupFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: WalletViewModel

    var topup = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(),
            ViewModelFactory.getInstance(requireActivity())).get(WalletViewModel::class.java)

        viewModel.mStripeSecretEntity.observe(viewLifecycleOwner){
            if (it.success){
//                showFragment(FragConst.ADD_CARDS, bundleOf("client_secret" to it.data.charge.client_secret,"pay_amount" to topup, "for_topup" to true))
//                viewModel.mStripeSecretEntity.value=null
            }else toast(it.message)
        }
        viewModel.mTopupEntity.observe(viewLifecycleOwner){
            if (it.success){
                pref.savePrefFloat(SessionManager.BALANCE, it.data.userBalance.toFloat())
                showFragment(FragConst.TOPUP_DONE_PRO, bundleOf("amount" to it.data.userBalance.toString()))
            }else toast(it.message)
        }
        viewModel.mTopupPaymentEntity.observe(viewLifecycleOwner){
            it?.let {
                if (it.success){
                    showFragment(FragConst.PAYMENT_PAYMONGO, bundleOf("amount" to topup,"checkout_url" to it.data.paymongo.data.attributes.checkout_url, "for_extra" to "topup"))
                    viewModel.mTopupPaymentEntity.value=null
                }else toast(it.message)
            }
        }
        topup100.setOnClickListener { showSelection(it!! as TextView, view, "100") }
        topup200.setOnClickListener { showSelection(it!! as TextView, view, "200") }
        topup300.setOnClickListener { showSelection(it!! as TextView, view, "300") }
        topup500.setOnClickListener { showSelection(it!! as TextView, view, "500") }
        topup1000.setOnClickListener { showSelection(it!! as TextView, view, "1000") }
        topup2000.setOnClickListener { showSelection(it!! as TextView, view, "2000") }
        onClick()

    }

    private fun showSelection(it: TextView, rootView: View, amount: String) {
        topup = amount
        topupSelected.isVisible = true
        val set = ConstraintSet()

        val layout: ConstraintLayout = rootView.findViewById(R.id.topupInfo) as ConstraintLayout
        set.clone(layout)/*
        set.clear(topupSelected.id, ConstraintSet.END);
        set.clear(topupSelected.id, ConstraintSet.TOP);
        set.clear(topupSelected.id, ConstraintSet.BOTTOM);
        set.clear(topupSelected.id, ConstraintSet.START);
        set.applyTo(layout)*/
        set.connect(topupSelected.id, ConstraintSet.END, it.id, ConstraintSet.END);
        set.connect(topupSelected.id, ConstraintSet.TOP, it.id, ConstraintSet.TOP);
        set.connect(topupSelected.id, ConstraintSet.BOTTOM, it.id, ConstraintSet.BOTTOM);
        set.connect(topupSelected.id, ConstraintSet.START, it.id, ConstraintSet.START);
        set.applyTo(layout)
    }

    private fun onClick() {
        lblSubmit.setOnClickListener(this)
        ivBack.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!) {
            lblSubmit -> {
                if (topup.isEmpty()) {
                    toast("Select Top-up amount")
                } else {
                    viewModel.topupPay("${topup.toInt()*100}")
//                    viewModel.topUp(topup)
                }
            }
            ivBack->requireActivity().onBackPressed()
        }
    }
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            receiver,
            IntentFilter("AMOUNT_DEDUCTED")
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
    }


    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "AMOUNT_DEDUCTED") {
                if (isInternetAvailable()&&(intent.getBooleanExtra("is_deducted",false))) {
                    loading(true)
                    viewModel.topUp(topup, JsonParser().parse(intent.getStringExtra("transactionDetail")).asJsonObject)
                }
            }
        }

    }

}