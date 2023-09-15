package com.msq.code.provider.connect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.buuzconnect.uis.addevent.ProfilePROViewModel
import com.google.gson.Gson
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.loading
import com.msq.util.ext.materialDialog
import com.msq.util.ext.materialNotifyDialog
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_connect.*
import java.util.*


class ConnectFragment : BaseFragment() {
    lateinit var viewModel: ProfilePROViewModel

    var stripeUrl = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (requireActivity() as AppCompatActivity).materialDialog("Required",
                        "You have to complete Stripe Connect Account!",
                        "Continue",
                        "Cancel",
                        {

                        },
                        { requireActivity().finishAffinity() })
                }
            })
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ProfilePROViewModel::class.java).also {
                    it.stripeConnect()
                }
        observe()
        ivRefresh.setOnClickListener {
            viewModel.stripeConnect()
        }

    }

    private fun observe() {
        viewModel.mConnectEntity.observe(viewLifecycleOwner) {
            loading(false)
            webview.apply {
                settings.javaScriptEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.domStorageEnabled = true
                webViewClient = yourWebClient
                loadUrl(it.data.accountLink.url)
                addJavascriptInterface(
                    MyJavaScriptInterface(requireActivity()),
                    "HtmlViewer"
                )
            }
            ivRefresh.isVisible = it.success.not()
            if (it.success) toast("Please complete account setup.")
            else toast(it.message)
        }
    }

    var yourWebClient: WebViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            loading(true)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            loading(false)
            webview.loadUrl("javascript:HtmlViewer.showHTML" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
            if (url.toLowerCase(Locale.ENGLISH).contains("http://54.169.185.218", true)
                && url.toLowerCase(Locale.ENGLISH).contains("stripeAccountId", true)
            ) {
                stripeUrl = url
            }
            Log.e("TAG", "geturl$url")
        }
    }

    class MyJavaScriptInterface(private val ctx: Context) {
        private var noHTML: Spanned? = null

        @JavascriptInterface
        fun showHTML(html: String) {
            System.out.println(html)
            if (html.contains("success\":true", true) && html.contains("status\":200", true)) {
                LocalBroadcastManager.getInstance(ctx)
                    .sendBroadcast(Intent("ACCOUNT_CONNECTED"))
            }
        }
    }//end MyJavaScriptInterface


    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            receiver,
            IntentFilter("ACCOUNT_CONNECTED")
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
    }


    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ACCOUNT_CONNECTED") {
                val uri: Uri = Uri.parse(stripeUrl)
                val data = pref.getUserData()!!
                data.stripe_connect = 1
                data.stripeId = uri.getQueryParameter("stripeAccountId") ?: ""
                pref.savePrefString(SessionManager.USER_DATA, Gson().toJson(data))
                (requireActivity() as AppCompatActivity).materialNotifyDialog("Stripe account",
                    "Congrats, Stripe Connect Account created successfully!",
                    "Continue"
                ) {
                    showFragment(FragConst.HOME_PRO)
                }
            }
        }

    }
}