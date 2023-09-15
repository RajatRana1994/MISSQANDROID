package com.msq.code.provider.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.ProfilePROViewModel
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.code.MainActivity
import com.msq.util.ViewModelFactory
import com.msq.util.ext.bgTint
import com.msq.util.ext.toast
import com.socialgalaxyApp.util.extension.loadWallRound
import kotlinx.android.synthetic.main.fragment_provider_home.*


class HomePROFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: ProfilePROViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_provider_home, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) context.updateHomeProTab()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ProfilePROViewModel::class.java)
        onClicks()
        pref.getUserData()?.let {
            if (it.services.isNullOrEmpty().not()){
                ivHome.loadWallRound(when(it.services[0].name.trim()){
                    "Child Tutor"->R.drawable.ten
                    "House Keeping"->R.drawable.two
                    "Nanny"->R.drawable.three
                    "Massage Therapist"->R.drawable.five
                    "Haircut"->R.drawable.eight
                    else->R.drawable.nine
                })
            }else
                ivHome.loadWallRound(R.drawable.nine)

            if ((it.isOnDuty?:0) == 1) {
                btnOnDuty.bgTint(R.color.sky_blue)
                btnAtRest.bgTint(R.color.hint_grey)
            } else {
                btnOnDuty.bgTint(R.color.hint_grey)
                btnAtRest.bgTint(R.color.sky_blue)
            }
        }
        viewModel.mUpdateProfileInfoEntity.observe(viewLifecycleOwner) {
            if (it.data.isOnDuty == 1) {
                btnOnDuty.bgTint(R.color.sky_blue)
                btnAtRest.bgTint(R.color.hint_grey)
                toast("You're online now!")
            } else {
                btnOnDuty.bgTint(R.color.hint_grey)
                btnAtRest.bgTint(R.color.sky_blue)
                toast("You're offline now!")
            }
        }

    }

    private fun onClicks() {
        btnAtRest.setOnClickListener(this)
        btnOnDuty.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            btnAtRest, btnOnDuty -> {
                viewModel.updateProfile(JsonObject().apply {
                    addProperty("isOnDuty", if (btnOnDuty == v) "1" else "0")
                })
            }
        }
    }

}