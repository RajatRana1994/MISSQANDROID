package com.msq.code.provider.confirmation

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import kotlinx.android.synthetic.main.fragment_confirmation.*


class ConfirmationFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClicks()
        Handler().postDelayed({ showFragment(FragConst.HOME_PRO) }, 2000)

    }

    private fun onClicks() {
//        btnGetStarted.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
         when (v!!) {
//            btnGetStarted->{
//                showFragment(FragConst.GETSTARTED)
//            }
        }
    }

}