package com.msq.code.auth

import android.app.Activity
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Handler(Looper.getMainLooper()).postDelayed({
            if (pref.getPrefIsLogin(SessionManager.IS_LOGIN)) {
                if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_PROVIDER) {
//                    if (pref.getUserData()?.stripeId.isNullOrEmpty())
//                        showFragment(FragConst.CONNECT_PRO)
//                    else
                        showFragment(FragConst.HOME_PRO)
                } else {
                    showFragment(FragConst.HOME)
                }
            } else {
                showFragment(FragConst.WELCOME)
            }
        }, 2000)


        // Handler().postDelayed({  },2000)


        printKeyHash(context as Activity)
    }

    fun printKeyHash(context: Activity): String? {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
            //getting application package name, as defined in manifest
            val packageName = context.applicationContext.packageName
            //Retriving package info
            packageInfo = context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            Log.e("Package Name=", context.applicationContext.packageName)
            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(Base64.encode(md.digest(), 0))
                Log.e("Key Hash=", key)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("Name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("No such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
        return key
    }


}