package com.msq.code.provider.welcome_success

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst


class WelcomeSuccessFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        Handler().postDelayed({ showFragment(FragConst.CONFIRMATION_PRO) }, 2000)

    }

    private fun onClick() {
//        btnLogin.setOnClickListener(this)
//        tvSignupLink.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!) {
//            btnLogin->showFragment(FragConst.HOME)
//            tvSignupLink->showFragment(FragConst.HOME)
        }
    }

}