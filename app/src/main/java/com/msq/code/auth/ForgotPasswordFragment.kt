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
import com.msq.Parser.AllAPIS
import com.msq.Parser.Message
import com.msq.Parser.PostMethod
import com.msq.R
import com.msq.UtilFiles.ConnectivityReceiver
import com.msq.UtilFiles.Parameters
import com.msq.UtilFiles.SavePref
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ForgotPasswordFragment : Fragment(), View.OnClickListener {
    var mDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_forgot_password, container, false)

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

        btnLogin.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!){
            btnLogin->ForgotPSDProcess()
        }
    }

    private fun ForgotPSDProcess() {
        if (ConnectivityReceiver.isConnected()) {
            if (edtEmail.getText().toString().isEmpty()) {
                util.IOSDialog(requireActivity(), "Please Enter Email Address")
                btnLogin.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake))
            } else if (!util.isValidEmail(edtEmail.getText().toString())) {
                util.IOSDialog(requireActivity(), "Please Enter a Vaild Email Address")
                btnLogin.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake))
            } else {
                FORGOT_PASSWORD_API()
            }
        } else {
            util.IOSDialog(requireActivity(), util.internet_Connection_Error)
        }
    }

    private fun FORGOT_PASSWORD_API() {
        util.hideKeyboard(requireActivity())
        mDialog!!.show()
        val formBuilder = MultipartBody.Builder()
        formBuilder.setType(MultipartBody.FORM)
        formBuilder.addFormDataPart(Parameters.EMAIL, edtEmail.getText().toString())
        val formBody: RequestBody = formBuilder.build()
        val getAsyncNew = PostMethod(context, mDialog, AllAPIS.FORGOT_PASSWORD, formBody, "")
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
                if (jsonMainobject.getString("status").equals("200", ignoreCase = true)) {
                    util.hideKeyboard(requireActivity())
                    val body = jsonMainobject.getString("message")
                    IOSDialog.Builder(context)
                        .setTitle(requireActivity().resources.getString(R.string.app_name))
                        .setCancelable(false)
                        .setMessage(body?:"Please check your Email inbox").setPositiveButton(
                            R.string.ok
                        ) { dialog, which -> //method to go back to previews screen
                            childFragmentManager.popBackStack()
                        } /* .setNegativeButton("Cancel", null)*/
                        .show()


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