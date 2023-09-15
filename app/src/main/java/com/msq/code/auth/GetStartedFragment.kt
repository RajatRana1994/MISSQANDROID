package com.msq.code.auth

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.inSpans
import androidx.core.text.italic
import com.msq.BaseFragment
import com.msq.R
import com.msq.UtilFiles.LocationLiveData
import com.msq.base.FragConst
import com.msq.util.CheckPermission
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import kotlinx.android.synthetic.main.fragment_get_started.*


class GetStartedFragment : BaseFragment(), View.OnClickListener {

//    val user_type by lazy { requireArguments().getString("user_type") ?: ConstUtils.TYPE_USER }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_started, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*btnCrateAccount.text=SpannableStringBuilder(btnCrateAccount.text.toString()).inSpans(AbsoluteSizeSpan(12,true)){
            italic { append(" Customer") }
        }
        btnCrateAccountPro.text=SpannableStringBuilder(btnCrateAccountPro.text.toString()).inSpans(AbsoluteSizeSpan(12,true)){
            italic { append(" Provider") }
        }*/
        onClick()
    }

    private fun onClick() {
        ivEmployee.setOnClickListener(this)
        tvCustomer.setOnClickListener(this)
        tvArtisans.setOnClickListener(this)
        ivCustomer.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            tvCustomer,tvArtisans,ivCustomer,ivEmployee -> {
                if (ivCustomer == v||tvCustomer == v)
                    showFragment(FragConst.SIGNUP)
                else
                    showFragment(FragConst.SIGNUP_PRO)
            }
            btnLogin -> showFragment(FragConst.LOGIN/*, bundleOf("user_type" to user_type)*/)
        }
    }

//    override fun onResume() {
//        super.onResume()
//        try {
//            CheckPermission(requireActivity()).locationPermission {
//                // Check if location settings are available
//                if (isDetached.not())
//                    LocationLiveData().init(requireActivity()).observe(this) {
//                        it?.let {
//                            pref.savePrefString(SessionManager.LAT, "${it.latitude}")
//                            pref.savePrefString(SessionManager.LNG, "${it.longitude}")
//                        }
//                    }
//            }
//        } catch (e: Exception) {
//        }
//    }

}