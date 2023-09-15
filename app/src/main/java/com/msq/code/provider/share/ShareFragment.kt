package com.msq.code.provider.share

import android.R.attr
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.msq.BaseFragment
import com.msq.Parser.AllAPIS
import com.msq.R
import com.msq.UtilFiles.AESUtils
import com.msq.base.FragConst
import com.msq.util.SessionManager
import kotlinx.android.synthetic.main.fragment_share.*
import java.lang.Exception
import android.R.attr.label

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context.CLIPBOARD_SERVICE
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import com.msq.util.ext.toast
import android.content.Intent
import android.widget.Toast
import com.msq.util.ext.composeMail


class ShareFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLink.text="${AllAPIS.BASE_URL+AllAPIS.SHARING_PROFILE_CONTROLLER}${AESUtils.encrypt("${AllAPIS.SHARING_PROFILE_CONTROLLER}${pref.getPrefString(SessionManager.USER_ID)}")}"
        Log.e("Link sharable",AllAPIS.BASE_URL+AESUtils.decrypt(tvLink.text.toString().substringAfter(AllAPIS.SHARING_PROFILE_CONTROLLER)))
        onClick()
        mutableListOf<Pair<String,Int>>().apply {
            add(Pair("Twitter",R.drawable.ic_twitter))
            add(Pair("Instagram",R.drawable.ic_instragam_share))
            add(Pair("Messenger",R.drawable.ic_messenger_share))
            add(Pair("Facebook",R.drawable.ic_facebook_share))
            add(Pair("Youtube",R.drawable.ic_youtube))
            add(Pair("Email",R.drawable.ic_email_share))
            add(Pair("Chrome",R.drawable.ic_chrome_share))
            add(Pair("Bluetooth",R.drawable.ic_bluetooth_share))
            add(Pair("Drive",R.drawable.ic_drive_share))
            add(Pair("More",R.drawable.ic_more_share))
            rvShare.adapter=ShareAdapter(this){t,p->
                if (this[p].first.equals("More")) {
                    chooserIntent()
                }else createShareIntent(this[p].first)
            }
        }
    }

    private fun chooserIntent(){
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.type = "text/plain"
        val shareBody = tvLink.text.toString()
        val shareSub = "Profile Link"
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(myIntent, "Share using"))
    }
    private fun createShareIntent(type: String) {
        var isFound = false
        val shareIntent = Intent(Intent.ACTION_SEND)
        try {
            shareIntent.type = "text/plain";
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, tvLink.text.toString())
            val pm = requireActivity().packageManager;
            val activityList = pm.queryIntentActivities(shareIntent, 0)

            Log.e("AppList",activityList.filter { it.activityInfo.name.contains(type,true) }.map { it.activityInfo.name }.toString())
            var filtered=activityList.filter { it.activityInfo.name.contains(type,true) }
            if (type=="Facebook") filtered=activityList.filter { it.activityInfo.name.contains(type,true)&& !it.activityInfo.name.contains("Messenger",true)&& !it.activityInfo.name.contains("Groups",true)}
            for (app in filtered) {
                if ((app.activityInfo.name).contains(type,true) || (app.activityInfo.name).contains(
                        type,true
                    )
                ) {
                    isFound = true
                    val activity = app.activityInfo
                    val name = ComponentName(activity.applicationInfo.packageName, activity.name)
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    shareIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    shareIntent.component = name
                    startActivity(shareIntent)
                }
            }
            if (!isFound) {
                try {
                   chooserIntent()
                } catch (e: java.lang.Exception) {
                    Toast.makeText(requireActivity(), e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            try {
               chooserIntent()
            } catch (e: java.lang.Exception) {
                Toast.makeText(requireActivity(), e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onClick() {
        btnCopy.setOnClickListener(this)
        ivBack.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivBack-> {requireActivity().onBackPressed()}
            btnCopy-> {
                val cm = btnCopy.context.getSystemService(
                    CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Profile link", tvLink.text.toString())
                cm.setPrimaryClip(clip)
                toast("Copied!")
            }
//            tvSignupLink->showFragment(FragConst.HOME)
        }
    }

}