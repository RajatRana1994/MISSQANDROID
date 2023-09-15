package com.msq.code.provider.wallet.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.WalletViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import com.msq.rest.entity.WalletTransection
import com.msq.util.ViewModelFactory
import com.msq.util.ext.commonIosPopup
import com.msq.util.ext.twoDecimal
import kotlinx.android.synthetic.main.fragment_transaction.*


class TransactionFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: WalletViewModel

    val mTransactions = arrayListOf<WalletTransection>()
    var page = 0
    var transactionType = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(),
            ViewModelFactory.getInstance(requireActivity())).get(WalletViewModel::class.java)
            .apply {
                transaction("${++page}", "$transactionType")
            }

        viewModel.mTransactionEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                tvAmount.text="₱ "+it.data.balance.twoDecimal()
                mTransactions.addAll(it.data.walletTransections)
                rvTransactions.adapter?.notifyDataSetChanged()
            }
        }

        rvTransactions.adapter = TransactionAdapter(mTransactions) { type, pos ->

        }

        onClick()

    }

    private fun onClick() {
        ivBack.setOnClickListener(this)
        tvLogout.setOnClickListener(this)
        lblTransfer.setOnClickListener(this)
        lblTopup.setOnClickListener(this)
        lblRequest.setOnClickListener(this)
//        tvSignupLink.setOnClickListener(this)

    }
    private fun LogoutAlert() {
        commonIosPopup(msg = "Are you sure Logout?",
            btNo = "No",
            btYes = "Yes",
            isSingleBtn = false) { b, dialog ->
            if (b) {
                dialog.dismiss()
                util.showToast(context, "User Logout Successfully")
                pref.clearPrefs()
                showFragment(FragConst.WELCOME)
            } else dialog.dismiss()
        }
    }

    override fun onClick(v: View?) {
        when (v!!) {
            tvLogout -> LogoutAlert()
            ivBack -> requireActivity().onBackPressed()
            lblTransfer -> {
                page=0
                mTransactions.clear()
                transactionType=4
                viewModel.transaction("${++page}", "$transactionType")
            }
            lblTopup -> {
                page=0
                mTransactions.clear()
                transactionType=1
                viewModel.transaction("${++page}", "$transactionType")
            }
            lblRequest -> {
                page=0
                mTransactions.clear()
                transactionType=4
                viewModel.transaction("${++page}", "$transactionType")
            }
//            tvSignupLink->showFragment(FragConst.HOME)
        }
    }


    override fun onResume() {
        super.onResume()
        page=0
    }
}