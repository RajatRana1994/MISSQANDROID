package com.msq.code.customer.payment

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.msq.BaseFragment
import com.msq.R
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import com.msq.util.CardValidator
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.addWatcher
import com.msq.util.ext.isInternetAvailable
import com.msq.util.ext.loading
import com.msq.util.ext.toast
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.*
import kotlinx.android.synthetic.main.fragment_add_card.*
import java.lang.ref.WeakReference
import java.util.*

class AddCardFragment : BaseFragment(), View.OnClickListener {
    private lateinit var stripe: Stripe
    val for_topup by lazy { requireArguments().getBoolean("for_topup", false) }
    val client_secret by lazy { requireArguments().getString("client_secret") ?: "" }
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    val pay_amount by lazy { requireArguments().getString("pay_amount") ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_card, container, false)
    }

    val card_id_extra by lazy { requireArguments().getString("card_id_extra") ?: "" }
    val req_checkout by lazy { requireArguments().getString("req_checkout") ?: "" }

    lateinit var viewModel: AddCardViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
            .get(AddCardViewModel::class.java).apply {
            if (isInternetAvailable()) {
                if (pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!.isEmpty())
                    createCustomer(pref.getPrefString(SessionManager.USER_ID)!!)
            }
        }
        ivBack.setOnClickListener(this)
        tvSaveCard.setOnClickListener(this)
        if (req_checkout.isNotEmpty()) {
            textView.text = "Payment"
            tvSaveCard.text = "Pay"
        }
        tvExpiryDate.setOnClickListener(this)
        inputFormatter()
        addWatchers()
        registerObserver()
    }

    private fun inputFormatter() {
        tvExpiryDate.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            if (source.isNotEmpty()) {
                if (!Character.isDigit(source[0])) return@InputFilter "" else {
                    if (dstart == 2) return@InputFilter "/$source" else if (dstart > 4) return@InputFilter ""
                }
            }
            null
        })
        edtCardNumber.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            if (source.isNotEmpty()) {
                if (!Character.isDigit(source[0])) return@InputFilter "" else {
                    if (dstart == 4) return@InputFilter " $source"
                    else if (dstart == 9) return@InputFilter " $source"
                    else if (dstart == 14) return@InputFilter " $source"
                    else if (dstart == 19) return@InputFilter " $source"
                    else if (dstart > 21) return@InputFilter ""
                }
            }
            null
        })
    }

    private fun registerObserver() {
        viewModel.apply {
            mCreateCustomerEntity.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                pref.savePrefString(SessionManager.STRIPE_CUS_ID, it.id)
//                if (isInternetAvailable())
//                    viewModel.editCustomer(
//                        pref.getPrefString(SessionManager.USER_TOKEN)!!,
//                        hashMapOf<String, String>().apply {
//                            put("stripeCustomerId", it.id)
//                        })
            })
//            mUpdatedCusOnServer.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//                pref.savePrefString(SessionManager.STRIPE_CUS_ID, it.data.stripeCustomerId)
//                if (isDetached.not() && edtNameCard.text.isNotEmpty()) onAddCard()
//            })
            mStripePaymentDone.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                loading(false)
                toast(it.message)
                if (it.success){
                    showFragment(FragConst.BOOKINGS)
                }
            })
            mErrorObserver.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                loading(false)
                toast(it.message)
            })
            mCreateCardModel.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (req_checkout.isEmpty()) {
                    loading(false)
                    toast("Card Added successfully!")
                    LocalBroadcastManager.getInstance(requireActivity())
                        .sendBroadcast(Intent("CARD_ADDED"))
                    requireActivity().onBackPressed()
                } else {
                    val data = JsonParser().parse(req_checkout).asJsonObject.apply {
                        addProperty("stripeCustomerId",
                            pref.getPrefString(SessionManager.STRIPE_CUS_ID))
                        addProperty("cardId", it.id)
                    }

//                    if (item_order) viewModel.placeItemOrder(pref.getPrefString(SessionManager.USER_TOKEN)!!, data)
//                    else
//                        viewModel.placeOrder(pref.getPrefString(SessionManager.USER_TOKEN)!!, data)
                }
            })
//            viewModel.orderObserver.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//                loading(false)
//                showFragment(FragConst.HOME)
//            })
        }

    }

    private fun addWatchers() {
        edtCardNumber.addWatcher {
            val isValidCard = CardValidator.validateCardNumber(it.replace(" ", ""))
            edtCardNumber.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    if (isValidCard) R.color.black else R.color.red
                )
            )
        }
        tvExpiryDate.addWatcher {
            var isValid = true
            if (it.length == 5) {
                isValid = CardValidator.validateExpiryDate(
                    it.substringBefore("/"),
                    it.substringAfter("/")
                )
            }
            tvExpiryDate.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    if (isValid) R.color.black else R.color.red
                )
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> requireActivity().onBackPressed()
            R.id.tvSaveCard -> validateCard()
//            R.id.tvExpiryDate -> showDatePicker()
        }
    }

    private fun validateCard() {
        if (edtNameCard.text.isEmpty()) toast("Enter card holder name")
        else if (card_id_extra.isEmpty() && CardValidator.validateCardNumber(
                edtCardNumber.text.toString().replace(" ", "")
            ).not()
        ) toast("Enter valid card number")
        else if (card_id_extra.isEmpty() && tvExpiryDate.text.length != 5) toast("Enter valid expiry date")
        else if (card_id_extra.isEmpty() && CardValidator.validateExpiryDate(
                tvExpiryDate.text.toString().substringBefore("/"),
                tvExpiryDate.text.toString().substringAfter("/")
            ).not()
        ) toast("Enter valid card number")
        else if (card_id_extra.isEmpty() && CardValidator.validateCVV(
                edtCVV.text.toString(),
                CardValidator.getCardType(edtCardNumber.text.toString().replace(" ", ""))
            ).not()
        ) toast("Enter valid cvv")
        else {
            if (pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!
                    .isEmpty()
            ) viewModel.createCustomer(
                pref.getPrefString(SessionManager.USER_ID)!!
            )
            else onAddCard()
        }
    }

    private fun onAddCard() {
//        loading(true)
//        Stripe(requireActivity(), ConstUtils.PUBLISH_KEY).createToken(
//            Card.create(
//                edtCardNumber.text.toString().replace(" ", ""),
//                tvExpiryDate.text.toString().substringBefore("/").toInt(),
//                tvExpiryDate.text.toString().substringAfter("/").toInt(),
//                edtCVV.text.toString()
//            ), null, object : ApiResultCallback<Token> {
//                override fun onError(e: Exception) {
//                    loading(false)
//                    toast(e.localizedMessage)
//                }
//
//                override fun onSuccess(result: Token) {
//                    if (isInternetAvailable()) {
//                        if (client_secret.isNullOrEmpty())
//                            viewModel.createCard(pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!,
//                                result.id)
//                        else startCheckout(result.id)
//                    }
//                }
//
//            })
    }
//    private fun onAddCard() {
//        loading(true)
//        Stripe(requireActivity(), ConstUtils.PUBLISH_KEY).createToken(
//            Card.create(
//                edtCardNumber.text.toString().replace(" ", ""),
//                tvExpiryDate.text.toString().substringBefore("/").toInt(),
//                tvExpiryDate.text.toString().substringAfter("/").toInt(),
//                edtCVV.text.toString()
//            ), null, object : ApiResultCallback<Token> {
//                override fun onError(e: Exception) {
//                    loading(false)
//                    toast(e.localizedMessage)
//                }
//
//                override fun onSuccess(result: Token) {
//                    if (isInternetAvailable()) {
//                        if (client_secret.isNullOrEmpty())
//                            viewModel.createCard(pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!,
//                                result.id)
//                        else
//                            startCheckout(result.id)
//                    }
//                }
//
//            })
//    }


    private fun startCheckout(token: String) {
        val params = PaymentMethodCreateParams.create(PaymentMethodCreateParams.Card.create(token),
            PaymentMethod.BillingDetails.Builder().setName(pref.getUserData()?.name ?: "")
                .setEmail(pref.getUserData()?.email ?: "").build())
        val confirmParams: ConfirmPaymentIntentParams = ConfirmPaymentIntentParams
            .createWithPaymentMethodCreateParams(params, client_secret)
        stripe = Stripe(
            requireActivity(),
            PaymentConfiguration.getInstance(requireActivity()).publishableKey
        )
        stripe.confirmPayment(this, confirmParams)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(
            requestCode,
            data, PaymentResultCallback(this)
        )
    }

    // ...
    private class PaymentResultCallback(activity: AddCardFragment) :
        ApiResultCallback<PaymentIntentResult> {
        private val activityRef: WeakReference<AddCardFragment>

        override fun onSuccess(result: PaymentIntentResult) {
            val activity: AddCardFragment = activityRef.get() ?: return
            val paymentIntent = result.intent
            val status: StripeIntent.Status? = paymentIntent.status
            if (status == StripeIntent.Status.Succeeded) {
                // Payment completed successfully
                val gson = GsonBuilder().setPrettyPrinting().create()

                //util.IOSDialog(context, "Payment completed" +gson.toJson(paymentIntent));
                Toast.makeText(activity.requireActivity(), "Payment completed", Toast.LENGTH_SHORT)
                    .show()
                Log.e("TRANSACTIONNO", gson.toString())
                activity.BOOKPRODUCT(gson.toJson(paymentIntent), "2")
            } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                // Payment failed
                util.IOSDialog(activity.requireActivity(), "Payment failed" +
                        Objects.requireNonNull(paymentIntent.lastPaymentError?.message))
                Log.e("errorrrrrr",
                    Objects.requireNonNull(paymentIntent.lastPaymentError?.message!!))
            }
        }

        override fun onError(e: java.lang.Exception) {
            val activity: AddCardFragment = activityRef.get() ?: return

            // Payment request failed – allow retrying using the same payment method
            util.IOSDialog(activity.requireActivity(), "Error$e")
        }

        init {
            activityRef = WeakReference<AddCardFragment>(activity)
        }
    }

    private fun BOOKPRODUCT(json: String?, msg: String) {
        if (for_topup) {
            LocalBroadcastManager.getInstance(requireActivity())
                .sendBroadcast(Intent("AMOUNT_DEDUCTED").putExtra("is_deducted", true).putExtra("transactionDetail", json))
            requireActivity().onBackPressed()
        }
        else
            viewModel.stripePaymentDone(pay_amount, JsonParser().parse(json).asJsonObject,booking_id)
    }

}