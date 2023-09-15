package com.msq.code.provider.setting

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.msq.BaseFragment
import com.msq.BuildConfig
import com.msq.Parser.AllAPIS
import com.msq.Parser.Message
import com.msq.Parser.PostMethod
import com.msq.R
import com.msq.UtilFiles.Parameters
import com.msq.UtilFiles.SavePref
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import com.msq.util.SessionManager
import com.msq.util.ext.commonIosPopup
import kotlinx.android.synthetic.main.fragment_settings.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject


class SettingsFragment : BaseFragment(), View.OnClickListener {
    private var savePref: SavePref? = null
    var mDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()

    }

    private fun onClick() {
        mDialog = util.initializeProgress(context)
        savePref = SavePref(context)

        tvVersionInfo.text="${getString(R.string.logged_in_as)} \n${pref.getUserData()?.email}\n${getString(R.string.version)} ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        tvShare.setOnClickListener(this)
        tvNotificatios.setOnClickListener(this)
        tvSupport.setOnClickListener(this)
        tvTerms.setOnClickListener(this)
        tvLanguage.setOnClickListener(this)
        tvLogout.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!) {
            tvShare -> showFragment(FragConst.SHARE_PRO)
            tvNotificatios -> showFragment(FragConst.NOTIFICATION_PRO)
            tvSupport -> showFragment(FragConst.EMP_SURVEY_PRO)
            tvTerms -> showFragment(FragConst.TERMS)
            tvLanguage -> showFragment(FragConst.LANGUAGE_PRO)
            tvLogout -> LogoutAlert()


        }
    }

    private fun LogoutAlert() {
        commonIosPopup(msg = "Are you sure Logout?",
            btNo = "No",
            btYes = "Yes",
            isSingleBtn = false) { b, dialog ->
            if (b) {
                dialog.dismiss()
                LOGOUT_API()
            } else dialog.dismiss()
        }
    }

    private fun LOGOUT_API() {
        util.hideKeyboard(requireActivity())
        mDialog!!.show()
        val formBuilder = MultipartBody.Builder()
        formBuilder.setType(MultipartBody.FORM)
        formBuilder.addFormDataPart(Parameters.EMAIL, "")
        val formBody: RequestBody = formBuilder.build()
        val getAsyncNew = PostMethod(context, mDialog, AllAPIS.LOGOUT, formBody, pref.getPrefString(
            SessionManager.USER_TOKEN))
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
                    util.showToast(context, "User Logout Successfully")
                    savePref!!.clearPreferences()
                    showFragment(FragConst.WELCOME)
                } else {
                    util.IOSDialog(requireActivity(), jsonMainobject.getString("msg"))
                }
            } catch (ex: JSONException) {
                ex.printStackTrace()
                savePref!!.clearPreferences()
                pref.clearPrefs()
                showFragment(FragConst.WELCOME)
            } catch (ex: Exception) {
                ex.printStackTrace()
                savePref!!.clearPreferences()
                pref.clearPrefs()
                showFragment(FragConst.WELCOME)
            }
        }
    }

}