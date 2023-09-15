package com.msq.code.auth

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import com.ligl.android.widget.iosdialog.IOSDialog
import com.msq.BaseFragment
import com.msq.Parser.AllAPIS
import com.msq.Parser.Message
import com.msq.Parser.PostMethod
import com.msq.Parser.PutMethod
import com.msq.R
import com.msq.UtilFiles.ConnectivityReceiver
import com.msq.UtilFiles.Parameters
import com.msq.UtilFiles.SavePref
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import com.msq.util.SessionManager
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class ChangePasswordFragment : BaseFragment(), View.OnClickListener {
    var mDialog: ProgressDialog? = null
    var savePref: SavePref?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_change_password, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        updateDrawbleSize()
    }

    private fun updateDrawbleSize() {

    }

    private fun onClick() {

        mDialog = util.initializeProgress(context)
        savePref= SavePref(context)
        btnChange.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!){
            btnChange->ChangePasswordTask()
        }
    }

    private fun ChangePasswordTask() {
        if (ConnectivityReceiver.isConnected()) {
            if (edtPassword_c.getText().toString().isEmpty()) {
                util.IOSDialog(requireActivity(), getString(R.string.enter_current_pass))
                btnChange.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            } else if (edtPassword_n.getText().toString().isEmpty()) {
                util.IOSDialog(requireActivity(), getString(R.string.enter_new_pass))
                btnChange.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            } else if (edtPassword_nc.getText().toString().isEmpty()) {
                util.IOSDialog(requireActivity(), getString(R.string.enter_confirm_pass))
                btnChange.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            } else if (edtPassword_n.getText().toString() != edtPassword_nc.getText().toString()) {
                util.IOSDialog(requireActivity(), "Password not match")
                btnChange.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
            } else {
                CHANGE_PASSWORD_API()
            }
        } else {
            util.IOSDialog(requireActivity(), util.internet_Connection_Error)
        }
    }

    private fun CHANGE_PASSWORD_API() {
        util.hideKeyboard(requireActivity())
        mDialog!!.show()
        val formBuilder = MultipartBody.Builder()
        formBuilder.setType(MultipartBody.FORM)
        formBuilder.addFormDataPart(
            Parameters.OLD_PASSWORD,
            edtPassword_c.getText().toString().trim { it <= ' ' })
        formBuilder.addFormDataPart(
            Parameters.NEW_PASSWORD,
            edtPassword_n.getText().toString().trim { it <= ' ' })
        val formBody: RequestBody = formBuilder.build()
        val getAsyncNew =
            PostMethod(context,mDialog, AllAPIS.CHANGEPASSWORD, formBody, pref.getPrefString(SessionManager.USER_TOKEN))
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
                    IOSDialog.Builder(requireActivity())
                        .setTitle(requireActivity().getResources().getString(R.string.app_name))
                        .setCancelable(false)
                        .setMessage("Password Changed Successfully!").setPositiveButton(R.string.ok,
                            DialogInterface.OnClickListener { dialog, which -> //method to go back to previews screen
                                childFragmentManager.popBackStack()
                            }).show()
                } else {
                    if (jsonMainobject.has("message"))
                        util.IOSDialog(requireActivity(), jsonMainobject.getString("message"))
                    else
                        util.IOSDialog(requireActivity(), jsonMainobject.getString("error_message").capitalize(
                            Locale.ENGLISH))
                }
            } catch (ex: JSONException) {
                ex.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}