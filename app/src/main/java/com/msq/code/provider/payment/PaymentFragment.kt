package com.msq.code.provider.payment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.buuzconnect.uis.addevent.ProfilePROViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.ViewModelFactory
import com.msq.util.ext.loading
import com.msq.util.ext.toastAction
import kotlinx.android.synthetic.main.fragment_connect.*


class PaymentFragment : BaseFragment() {
    lateinit var viewModel: ProfilePROViewModel
    val checkout_url by lazy { requireArguments().getString("checkout_url") }
    val booking_id by lazy { requireArguments().getString("booking_id") }
    val for_extra by lazy { requireArguments().getString("for_extra") }
    var paymentUrl = ""

    lateinit var handlerHtmlReader: Handler
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    lateinit var runnableHtmlReader: Runnable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
/*
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
//                    (requireActivity() as AppCompatActivity).materialDialog("Required",
//                        "You have to complete Stripe Connect Account!",
//                        "Continue",
//                        "Cancel",
//                        {
//
//                        },
//                        { requireActivity().finishAffinity() })
                }
            })
*/
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView.text = if (for_extra == "topup") "Topup" else "Payment"
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ProfilePROViewModel::class.java).also {
//                    it.stripeConnect()
                }
        handler = Handler()
        handlerHtmlReader = Handler()
        runnable = Runnable {
            viewModel.bookingDetail(booking_id!!)
        }
        runnableHtmlReader = Runnable {
            webview.loadUrl("javascript:HtmlViewer.showHTML" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
            handlerHtmlReader.removeCallbacks(runnableHtmlReader)
            handlerHtmlReader.postDelayed(runnableHtmlReader, 10000)
        }
        if (this@PaymentFragment.isVisible) loadCheckout()
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        ivRefresh.setOnClickListener {
            if (this@PaymentFragment.isVisible) loadCheckout()
        }


        viewModel.mBookingDetailEntity.observe(viewLifecycleOwner) {
            if (it.data.isPaymentDone == 0) placeBookingStatusApi() else requireActivity().onBackPressed()
        }
        placeBookingStatusApi()

    }

    private fun placeBookingStatusApi() {
        if (for_extra != "topup") {
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 5000)
        }
    }

    fun loadCheckout() {
        loading(false)
        webview.apply {
            settings.javaScriptEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.domStorageEnabled = true
            webViewClient = yourWebClient
            loadUrl(checkout_url!!)
            addJavascriptInterface(
                MyJavaScriptInterface(requireActivity()),
                "HtmlViewer"
            )
        }

    }


    override fun onDestroy() {
        if (::runnable.isInitialized) handler.removeCallbacks(runnable)
        if (::runnableHtmlReader.isInitialized) handlerHtmlReader.removeCallbacks(runnableHtmlReader)
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
        super.onDestroy()
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
            if (this@PaymentFragment.isVisible) {
                handlerHtmlReader.postDelayed(runnableHtmlReader, 10000)
                webview.loadUrl("javascript:HtmlViewer.showHTML" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
            }
//            if (url.toLowerCase(Locale.ENGLISH).contains("http://54.169.185.218", true)
//                && url.toLowerCase(Locale.ENGLISH).contains("stripeAccountId", true)
//            ) {
//                paymentUrl = url
//            }
            Log.e("TAG", "geturl$url")
        }
    }

    class MyJavaScriptInterface(private val ctx: Context) {
        private var noHTML: Spanned? = null

        @JavascriptInterface
        fun showHTML(html: String) {
            System.out.println("Payment>>> $html")
            if (html.contains("successfully paid", true)) {
                LocalBroadcastManager.getInstance(ctx)
                    .sendBroadcast(Intent("PAYMENT_DONE"))
            }
        }
    }//end MyJavaScriptInterface


    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            receiver,
            IntentFilter("PAYMENT_DONE")
        )
    }


    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "PAYMENT_DONE") {
//                (requireActivity() as AppCompatActivity).materialNotifyDialogR(if (for_extra == "topup") "Topup" else "Payment",
//                    if (for_extra == "topup") "Congrats, Topup process completed successfully!, Balance will be added in few minutes." else "Thanks to complete payment process successfully!, Booking payment status will be updated in few minutes.",
//                    "Great"
//                ) {
                toastAction(if (for_extra == "topup") "Congrats, Topup process completed successfully!, Balance will be added in few minutes." else "Thanks to complete payment process successfully!, Booking payment status will be updated in few minutes.",
                    "Great") {
                    if (for_extra == "topup")
                        showFragment(FragConst.WALLET_PRO)
                    else
                        requireActivity().onBackPressed()
                }
//                }
            }
        }

    }
}