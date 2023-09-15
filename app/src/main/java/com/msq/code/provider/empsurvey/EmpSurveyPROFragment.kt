package com.msq.code.provider.empsurvey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.ProfilePROViewModel
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_emp_survey_pro.*


class EmpSurveyPROFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: ProfilePROViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emp_survey_pro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ProfilePROViewModel::class.java)
        onClicks()
        viewModel.mEmpSurveyNotify.observe(viewLifecycleOwner, {
            toast(it.message)
            requireActivity().onBackPressed()
        })

    }

    private fun onClicks() {
        cbInstagram.setOnClickListener(this)
        cbFacebook.setOnClickListener(this)
        cbTwitter.setOnClickListener(this)
        cbProfessionalAssociation.setOnClickListener(this)
        cbFriendColleague.setOnClickListener(this)
        cbOther.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            cbInstagram, cbFacebook, cbTwitter, cbProfessionalAssociation, cbFriendColleague, cbOther -> {
                updateCheckable(v)
            }
            ivBack -> requireActivity().onBackPressed()
            btnSubmit -> {
                if (edtAboutYou.text.isEmpty()) toast(getString(R.string.enter_something_intresting_about_you))
                else if (cbOther.isChecked && edtOther.text.isEmpty()) toast(getString(R.string.enter_specify_other_app_name))
                else {
                    JsonObject().also {
                        it.addProperty("text", edtAboutYou.text.toString())
                        it.addProperty("type", getAppType())
                        viewModel.employeeSurvey(it)
                    }

                }
            }
        }
    }

    private fun updateCheckable(v: View) {
        cbInstagram.isChecked = cbInstagram == v
        cbFacebook.isChecked = cbFacebook == v
        cbTwitter.isChecked = cbTwitter == v
        cbProfessionalAssociation.isChecked = cbProfessionalAssociation == v
        cbFriendColleague.isChecked = cbFriendColleague == v
        cbOther.isChecked = cbOther == v
        edtOther.isVisible = cbOther.isChecked
    }

    private fun getAppType(): String {
        when (true) {
            cbInstagram.isChecked -> return "1"
            cbFacebook.isChecked -> return "2"
            cbTwitter.isChecked -> return "3"
            cbProfessionalAssociation.isChecked -> return "4"
            cbFriendColleague.isChecked -> return "5"
            cbOther.isChecked -> return "6"
            else -> return "1"
        }
    }

}