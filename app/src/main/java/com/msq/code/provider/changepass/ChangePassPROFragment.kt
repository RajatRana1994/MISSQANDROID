package com.msq.code.provider.changepass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.ProfilePROViewModel
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.rest.entity.AuthData
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import com.socialgalaxyApp.util.extension.loadUserImage
import kotlinx.android.synthetic.main.fragment_change_pass_pro.*


class ChangePassPROFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: ProfilePROViewModel
    lateinit var userData: AuthData
    var latitude = ""
    var longitude = ""
    var city = ""
    var state = ""
    var postCode = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_pass_pro, container, false)
    }

    private val PLACE_PICKER_REQUEST = 787

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userData = pref.getUserData()!!
        updateData()
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ProfilePROViewModel::class.java)
        onClicks()
        observers()
    }

    private fun observers() {
        viewModel.mPasswordChanged.observe(viewLifecycleOwner, {
            toast(it.message)
            requireActivity().onBackPressed()
        })
    }

    fun updateData() {
        ivUser.loadUserImage(userData.profile)
        tvName.setText(userData.name)
        tvEmail.setText(userData.email)
        ivRatingBar.rating = userData.rating ?: 0f
        tvRating.setText("${userData.rating ?: 0f}")
    }

    private fun onClicks() {
        btnSubmit.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!) {
            btnSubmit -> {
                if (edtOldPassword.text.toString().isEmpty()) toast("Enter Old password")
                else if (edtNewPassword.text.toString().isEmpty()) toast("Enter New password")
                else if (edtConNewPassword.text.toString()
                        .isEmpty()
                ) toast("Enter Confirm password")
                else if (edtNewPassword.text.toString()!=edtConNewPassword.text.toString()
                ) toast("New & Confirm password are not matched!")
                else JsonObject().apply {
                    addProperty("old_password", edtOldPassword.text.toString().trim())
                    addProperty("new_password", edtNewPassword.text.toString().trim())
                    viewModel.changePassword(this)
                }
            }
        }
    }

}