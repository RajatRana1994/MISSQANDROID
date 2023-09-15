package com.msq.code.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.Parser.AllAPIS
import com.msq.Parser.Message
import com.msq.Parser.PostMethod
import com.msq.R
import com.msq.UtilFiles.ConnectivityReceiver
import com.msq.UtilFiles.Parameters
import com.msq.UtilFiles.SavePref
import com.msq.UtilFiles.util
import com.msq.base.AuthType
import com.msq.base.FragConst
import com.msq.base.SocialAuth
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class LoginFragment : BaseFragment(), View.OnClickListener, SocialAuth.SocialAuthListener {
    private var savePref: SavePref? = null
    var mDialog: ProgressDialog? = null
    lateinit var viewModel: AuthViewModel

    var api_type = "";
    var image = "";
    var isShowingPassword = false;

    private lateinit var socialAuth: SocialAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        socialAuth = SocialAuth.getInstance(requireActivity(), this, fbLoginButton)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity())).get(
                AuthViewModel::class.java)
        onClick()
        updateDrawbleSize()
        observers()
    }


    private fun EditText.showHidePassword(show: Boolean = false) {
        isShowingPassword = show
        inputType =
            if (show) InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT else InputType.TYPE_TEXT_VARIATION_PASSWORD;
        setSelection(text.length)
    }

    private fun observers() {
        viewModel.mUserNotRegisteredEntity.observe(viewLifecycleOwner, Observer {
            if (it) showFragment(FragConst.WELCOME)
        })
        viewModel.mAuthEntity.observe(viewLifecycleOwner, Observer {
            pref.savePrefString(SessionManager.USER_ID, it.data.id.toString())
            pref.savePrefString(SessionManager.USER_NAME, it.data.name)
            pref.savePrefString(SessionManager.USER_PROFILE, it.data.profile ?: "")
            pref.savePrefString(SessionManager.USER_TOKEN, it.data.authorizationKey)
            pref.savePrefString(SessionManager.USER_TYPE, it.data.userType)
            //pref.savePrefString(SessionManager.isOnDuty, it.data.userType)
            pref.savePrefString(SessionManager.USER_DATA, Gson().toJson(it.data))
            pref.savePrefFloat(SessionManager.BALANCE, it.data.balance ?: 0f)
            pref.savePrefBool(SessionManager.IS_LOGIN, true)
            if (it.data.userType == ConstUtils.TYPE_PROVIDER) {
//                if (it.data.stripeId.isNullOrEmpty())
//                    showFragment(FragConst.CONNECT_PRO)
//                else
                    showFragment(FragConst.HOME_PRO)
            } else {
                showFragment(FragConst.HOME)
            }
        })
    }

    private fun updateDrawbleSize() {

    }

    private fun onClick() {

        savePref = SavePref(context)
        mDialog = util.initializeProgress(context)

        btnLogin.setOnClickListener(this)
        viewPassword.setOnClickListener(this)
        tvSignupLink.setOnClickListener(this)
        tvForgotPass.setOnClickListener(this)
        btnMail.setOnClickListener(this)
        btnFaceBook.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!) {
            btnLogin -> SigninProcess()
            viewPassword -> edtPassword.showHidePassword(!isShowingPassword)
            tvSignupLink -> showFragment(FragConst.SIGNUP_PRO,
                bundleOf("signup_type" to "provider"))
            tvForgotPass -> showFragment(FragConst.FORGOTPASSWORD)
            btnMail -> socialAuth.setupAuth(AuthType.GOOGLE)
            btnFaceBook -> socialAuth.setupAuth(AuthType.FACEBOOK)

        }
    }

    private fun SigninProcess() {
        if (ConnectivityReceiver.isConnected()) {
            if (edtEmail.getText().toString().isEmpty()) {
                util.IOSDialog(requireActivity(), "Please Enter Email Address")
                btnLogin.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake))
            } else if (!util.isValidEmail(edtEmail.getText().toString())) {
                util.IOSDialog(requireActivity(), "Please Enter a Vaild Email Address")
                btnLogin.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake))
            } else if (edtPassword.getText().toString().isEmpty()) {
                util.IOSDialog(requireActivity(), "Please Enter Password")
                btnLogin.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake))
            } else {
                viewModel.userLogin(edtEmail.getText().toString(),
                    edtPassword.getText().toString(),
                    "1",
                    pref.getPrefString(SessionManager.DEVICE_TOKEN)!!)
//                LOGIN_API()
            }
        } else {
            util.IOSDialog(requireActivity(), util.internet_Connection_Error)
        }
    }

    private fun LOGIN_API() {
        api_type = "1"
        util.hideKeyboard(requireActivity())
        mDialog!!.show()
        val formBuilder = MultipartBody.Builder()
        formBuilder.setType(MultipartBody.FORM)
        formBuilder.addFormDataPart(Parameters.EMAIL, edtEmail.getText().toString())
        formBuilder.addFormDataPart(Parameters.PASSWORD, edtPassword.getText().toString())
        formBuilder.addFormDataPart(Parameters.DEVICE_TYPE, "1")
        formBuilder.addFormDataPart(Parameters.DEVICE_TOKEN, pref.getPrefString(SessionManager.DEVICE_TOKEN)!!)
        val formBody: RequestBody = formBuilder.build()
        val getAsyncNew = PostMethod(context, mDialog, AllAPIS.USERLOGIN, formBody, "")
        getAsyncNew.hitApi()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(event: Message) {
        mDialog!!.dismiss()
        val result: String = event.getMessage()
        if (result != null && !result.equals("", ignoreCase = true)) {
            try {
                val jsonMainobject = JSONObject(result)
                if (jsonMainobject.getString("code").equals("200", ignoreCase = true)) {
                    util.hideKeyboard(requireActivity())
                    val body = jsonMainobject.getJSONObject("body")

                    if (api_type.equals("1"))//login api
                    {
                        save_responce(body)

                    } else if (api_type.equals("2"))//social login api
                    {
                        if (body.getString("phone").equals("")) {


                        } else {
                            save_responce(body)
                        }
                    }


                } else {
                    if (jsonMainobject.has("message"))
                        util.IOSDialog(requireActivity(), jsonMainobject.getString("message"))
                    else
                        util.IOSDialog(requireActivity(),
                            jsonMainobject.getString("error_message").capitalize(
                                Locale.ENGLISH))
                }
            } catch (ex: JSONException) {
                ex.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun save_responce(body: JSONObject) {

        savePref!!.authorization_key = body.getString("token")
        savePref!!.id = body.getString(Parameters.ID)
        savePref!!.name = body.optString(Parameters.NAME)
        savePref!!.email = body.getString(Parameters.EMAIL)
        savePref!!.image = body.getString(Parameters.IMAGE)
        savePref!!.phone = body.getString(Parameters.PHONE)

        savePref!!.setStringLatest(Parameters.DOB, body.getString(Parameters.DOB))
        savePref!!.setStringLatest(
            Parameters.GENDER,
            body.getString(Parameters.GENDER)
        )
        savePref!!.setStringLatest(
            Parameters.HEIGHT,
            body.getString(Parameters.HEIGHT)
        )
        savePref!!.setStringLatest(Parameters.AGE, body.getString(Parameters.AGE))
        savePref!!.setStringLatest(
            Parameters.LOOKINGFOR,
            body.getString(Parameters.LOOKINGFOR)
        )
        savePref!!.setStringLatest(
            Parameters.INTEREST,
            body.getString(Parameters.INTEREST)
        )
        savePref!!.setStringLatest(
            Parameters.EDUCATION,
            body.getString(Parameters.EDUCATION)
        )
        savePref!!.setStringLatest(
            Parameters.ACHIEVEMENT,
            body.getString(Parameters.ACHIEVEMENT)
        )
        savePref!!.setStringLatest(Parameters.BIO, body.getString(Parameters.BIO))
        savePref!!.setStringLatest(
            Parameters.ISPRIVATE,
            body.getString(Parameters.ISPRIVATE)
        )
        util.showToast(context, "Login Successfully!!!")

        showFragment(FragConst.HOME)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        socialAuth.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onFacebook(`object`: JSONObject, token: String) {
        val profilePicUrl =
            `object`.getJSONObject("picture").getJSONObject("data").getString("url")
        checkSocialUser(`object`.getString("id"), token, `object`.getString("email"), "1")
    }

    override fun onGoogle(acct: GoogleSignInAccount?) {
        val socialid = acct!!.id!!
        val socialToken = acct.idToken!!
        val email = acct.email!!
        checkSocialUser(socialid, socialToken, email, "4")
    }

    private fun checkSocialUser(
        social_id: String,
        social_token: String,
        email: String,
        socialType: String,
    ) {
        JsonObject().apply {
            addProperty("socialId", social_id)
            addProperty("socialToken", social_token)
            addProperty("socialType", socialType)
            addProperty("email", email)
            addProperty(Parameters.DEVICE_TYPE, "1")
            addProperty(Parameters.DEVICE_TOKEN, pref.getPrefString(SessionManager.DEVICE_TOKEN)!!)
            viewModel.socialLogin(this)
        }
    }


}