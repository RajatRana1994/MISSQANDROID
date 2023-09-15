package com.msq.code.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.buuzconnect.uis.addevent.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.UtilFiles.Parameters
import com.msq.UtilFiles.SavePref
import com.msq.base.AuthType
import com.msq.base.FragConst
import com.msq.base.SocialAuth
import com.msq.rest.entity.MSQService
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.bottomSheetDialog
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.json.JSONObject


class WelcomeFragment : BaseFragment(), View.OnClickListener,
    SocialAuth.SocialAuthListener {
    val json: JsonObject by lazy { JsonObject() }
    private var services = arrayListOf<MSQService>()
    private var savePref: SavePref? = null
    lateinit var viewModel: AuthViewModel

    private lateinit var socialAuth: SocialAuth

    var selectedService = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        socialAuth = SocialAuth.getInstance(requireActivity(), this, loginButton)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity())).get(
                AuthViewModel::class.java).apply {
                this.getServices()
            }
        onClicks()
        observers()
    }

    private fun observers() {
        viewModel.mUserNotRegisteredEntity.observe(viewLifecycleOwner, Observer {
            if (it) askDetail()
        })
        viewModel.mServiceListEntity.observe(viewLifecycleOwner) {
            services = it.data.result as ArrayList<MSQService>
        }
        viewModel.mAuthEntity.observe(viewLifecycleOwner) {
            pref.savePrefString(SessionManager.USER_ID, it.data.id.toString())
            pref.savePrefString(SessionManager.USER_NAME, it.data.name)
            pref.savePrefString(SessionManager.USER_PROFILE, it.data.profile ?: "")
            pref.savePrefString(SessionManager.USER_TYPE, it.data.userType)
            pref.savePrefString(SessionManager.USER_TOKEN, it.data.authorizationKey)
            pref.savePrefFloat(SessionManager.BALANCE, it.data.balance ?: 0f)
            pref.savePrefString(SessionManager.USER_DATA, Gson().toJson(it.data))
            pref.savePrefBool(SessionManager.IS_LOGIN, true)
            toast(it.message)
            if (it.data.userType == ConstUtils.TYPE_PROVIDER) {
//                if (it.data.stripeId!!.isNullOrEmpty())
//                    showFragment(FragConst.CONNECT_PRO)
//                else
                    showFragment(FragConst.HOME_PRO)
            } else {
                showFragment(FragConst.HOME)
            }
        }
    }

    private fun onClicks() {
        savePref = SavePref(context)
        btnGetStarted.setOnClickListener(this)
        ivFb.setOnClickListener(this)
        ivGmail.setOnClickListener(this)

    }


    override fun onClick(v: View) {
        when (v) {
            btnGetStarted -> {
                showFragment(FragConst.GETSTARTED /*,bundleOf("user_type" to if (rbCustomer.isChecked) ConstUtils.TYPE_USER else ConstUtils.TYPE_PROVIDER)*/)
            }
            ivFb -> {
                socialAuth.setupAuth(AuthType.FACEBOOK)
            }
            ivGmail -> {
                socialAuth.setupAuth(AuthType.GOOGLE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        socialAuth.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun loginSocialUser(
        social_id: String,
        social_token: String,
        email: String,
        socialType: String,
        name: String,
    ) {
        json.apply {
            addProperty("socialId", social_id)
            addProperty("socialToken", social_token)
            addProperty("socialType", socialType)
            addProperty("email", email)
            addProperty("name", name)
            addProperty(Parameters.DEVICE_TYPE, "1")
            addProperty(Parameters.DEVICE_TOKEN, pref.getPrefString(SessionManager.DEVICE_TOKEN)!!)
            viewModel.socialLogin(this)
        }
    }
    private fun registerSocialUserwithUsertype(
        userType: String,
        selectedService: String,
    ) {
        json.apply {
            addProperty("userType", userType)
            addProperty("serviceIds", selectedService)
            viewModel.socialLogin(this)
        }
    }


    override fun onFacebook(`object`: JSONObject, token: String) {
        val profilePicUrl =
            `object`.getJSONObject("picture").getJSONObject("data").getString("url")
        loginSocialUser(`object`.getString("id"),
            token,
            `object`.getString("email"),
            "1",
            `object`.getString("name"))
    }

    override fun onGoogle(acct: GoogleSignInAccount?) {
        val socialid = acct!!.id!!
        val socialToken = acct.idToken!!
        val email = acct.email!!
        loginSocialUser(socialid,
            socialToken,
            email,
            "4",
            acct.displayName ?: acct.givenName ?: "")
    }

    fun askDetail(){
        var userTYpe = ""
        bottomSheetDialog(R.layout.layout_usertype) { d, v ->
            val tvSERVICES = v.findViewById<TextView>(R.id.tvSERVICES)
            val rvServices = v.findViewById<RecyclerView>(R.id.rvServices)
            val ivEmployee = v.findViewById<CheckBox>(R.id.ivEmployee)
            val ivCustomer = v.findViewById<CheckBox>(R.id.ivCustomer)
            ivCustomer.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    ivEmployee.isChecked = false
                    tvSERVICES.isVisible = false
                    rvServices.isVisible = false
                    userTYpe = ConstUtils.TYPE_USER
                }
            }
            ivEmployee.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    ivCustomer.isChecked = false
                    tvSERVICES.isVisible = true
                    rvServices.isVisible = true
                    userTYpe = ConstUtils.TYPE_PROVIDER
                }
            }
            rvServices.adapter =
                ServiceSelectAdapter(services) { id, pos ->
                    selectedService = id
                }
            v.findViewById<TextView>(R.id.btnLogin).setOnClickListener {
                if (userTYpe.isEmpty()) toast("Select User type")
                else if (userTYpe == ConstUtils.TYPE_PROVIDER && selectedService.isEmpty()) toast("Select service type")
                else {
                    d.dismiss()
                    if (userTYpe.isEmpty()) toast("Select User type")
                    else if (userTYpe == ConstUtils.TYPE_PROVIDER && selectedService.isEmpty()) toast("Select service type")
                    else registerSocialUserwithUsertype(userTYpe,selectedService);
                }
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        try {
////            CheckPermission(requireActivity()).locationPermission {
////                // Check if location settings are available
////                if (isDetached.not())
////                    LocationLiveData().init(requireActivity()).observe(this) {
////                        it?.let {
////                            pref.savePrefString(SessionManager.LAT, "${it.latitude}")
////                            pref.savePrefString(SessionManager.LNG, "${it.longitude}")
////                        }
////                    }
////            }
//        } catch (e: Exception) {
//        }
//    }
}