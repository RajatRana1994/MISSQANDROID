package com.msq.code.customer.booking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.msq.R
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import com.stripe.android.googlepaylauncher.GooglePayPaymentMethodLauncher
import kotlinx.android.synthetic.main.activity_gpay.*

class GPayActivity : AppCompatActivity() {
    val clientSecret by lazy { intent.getStringExtra("client_secret")?:"" }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpay)

        val googlePayLauncher = GooglePayPaymentMethodLauncher(
            activity = this,
            config = GooglePayPaymentMethodLauncher.Config(
                environment = GooglePayEnvironment.Test,
                merchantCountryCode = "IN",
                merchantName = "Widget Store"
            ),
            readyCallback = ::onGooglePayReady,
            resultCallback = ::onGooglePayResult
        )

        gPay.setOnClickListener {
            googlePayLauncher.present(
                currencyCode = "INR",
                amount = 1
            )
        }
    }

    private fun onGooglePayReady(isReady: Boolean) {
        gPay.isEnabled = isReady
    }

    private fun onGooglePayResult(
        result: GooglePayPaymentMethodLauncher.Result
    ) {
        when (result) {
            is GooglePayPaymentMethodLauncher.Result.Completed -> {
                // Payment details successfully captured.
                // Send the paymentMethodId to your server to finalize payment.
                val paymentMethodId = result.paymentMethod.id
            }
            GooglePayPaymentMethodLauncher.Result.Canceled -> {
                // User canceled the operation
            }
            is GooglePayPaymentMethodLauncher.Result.Failed -> {
                // Operation failed; inspect `result.error` for the exception
            }
        }
    }
}