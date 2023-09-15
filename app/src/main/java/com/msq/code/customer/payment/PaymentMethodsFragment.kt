package com.msq.code.customer.payment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.customer.service.valetcarwash.models.stripe.CardsListData
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.msq.BaseFragment
import com.msq.R
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.*
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.StripeIntent
import kotlinx.android.synthetic.main.fragment_payment_methods.*
import java.lang.ref.WeakReference
import java.util.*


class PaymentMethodsFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: AddCardViewModel
    val cardList = mutableListOf<CardsListData>()
    val client_secret by lazy { requireArguments().getString("client_secret") ?: "" }
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    val pay_amount by lazy { requireArguments().getString("pay_amount") ?: "" }
    private lateinit var stripe: Stripe


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_methods, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity())).get(AddCardViewModel::class.java).apply {
            if (isInternetAvailable()) {
                if (pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!.isNotEmpty()) {
                    if (pref.getPrefString(SessionManager.STRIPE_DEFAULT_CARD)!!.isEmpty()) {
                        loading(true)
                        getCustomer(pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!)
                    } else {
                        loading(true)
                        getCards(pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!)
                    }
                } else tvAddCard.onOffVisibility(true)
            }
        }
        setListeners()
        setAdapter()
        register()
    }

    private fun register() {
        viewModel.mErrorObserver.observe(viewLifecycleOwner) {
            loading(false)
            toast(it.message)
        }
        viewModel.mCreateCustomerEntity.observe(viewLifecycleOwner) {
            loading(false)
            if (it.default_source != null) {
                pref.savePrefString(SessionManager.STRIPE_DEFAULT_CARD, it.default_source)
                if (pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!
                        .isNotEmpty()
                ) viewModel.getCards(
                    pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!
                )
            }
        }
        viewModel.mUpdatedCustomerEntity.observe(viewLifecycleOwner) {
            loading(false)
            pref.savePrefString(SessionManager.STRIPE_DEFAULT_CARD, it.second.default_source)
            cardList.forEach {
                it.default = it.id == pref.getPrefString(SessionManager.STRIPE_DEFAULT_CARD)
            }
            rvPaymentMethods.adapter!!.notifyDataSetChanged()
            tvEmpty.onOffVisibility(cardList.isEmpty())
        }
        viewModel.mGetCardsListModel.observe(viewLifecycleOwner) {
            loading(false)
            cardList.clear()
            it.data.forEach {
                it.default = it.id == pref.getPrefString(SessionManager.STRIPE_DEFAULT_CARD)
            }
            cardList.addAll(it.data)
            setAdapter()
            tvEmpty.onOffVisibility(cardList.isEmpty())

        }
        viewModel.mDeleteCardsModel.observe(viewLifecycleOwner) {
            loading(false)
            if (it.second.deleted) {
                toast("Card deleted successfully!")
                cardList.removeAt(it.first)
                rvPaymentMethods.adapter!!.notifyDataSetChanged()
            }
            tvEmpty.onOffVisibility(cardList.isEmpty())
        }
        viewModel.mStripePaymentDone.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            loading(false)
            toast(it.message)
            if (it.success){
                showFragment(FragConst.BOOKINGS)
            }
        })

//        viewModel.order.observe(viewLifecycleOwner, {
//            loading(false)
//            startActivity(
//                Intent(requireActivity(), HomeActivity::class.java).putExtra(
//                    "type_extra",
//                    "booking"
//                ).putExtra("message", "Order Placed Successfully")
//            )
//            finishAffinity()
//        })
//        viewModel.mItemOrderPlacedEntity.observe(viewLifecycleOwner, androidx.lifecycle.{
//            loading(false)
//            startActivity(
//                Intent(
//                    requireActivity(),
//                    HomeActivity::class.java
//                ).putExtra("type_extra", "booking").putExtra("message", it.message)
//                    .putExtra("isItem", true))
//            finishAffinity()
//        })

    }

    fun setListeners() {
        tvAddCard.setOnClickListener(this)
        tvPayment.setOnClickListener(this)
        ivBack.setOnClickListener(this)
    }

    //adapter for payment methods
    fun setAdapter() {
        rvPaymentMethods.adapter = PaymentMethodAdapter(cardList) { type, it ->
            if (type == "update") {
                if (cardList[it].default.not()) {
                    if (isInternetAvailable()) {
                        loading(true)
                        viewModel.updateCustomer(
                            it,
                            pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!,
                            cardList[it].id
                        )
                    }
                }
            } else {
                showDeleteCard(it)
            }
        }
    }

    fun showDeleteCard(pos: Int) {
        (requireActivity() as AppCompatActivity).materialDialog("Delete Card",
            "Are you sure?",
            "Yes",
            "No",
            {
                if (isInternetAvailable()) {
                    loading(true)
                    viewModel.deleteCard(
                        pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!,
                        cardList[pos].id,
                        pos
                    )
                }
            }, { })
    }



    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvAddCard -> {
                showFragment(FragConst.ADD_CARDS, bundleOf("client_secret" to client_secret,"pay_amount" to pay_amount,"booking_id" to booking_id))
            }
            R.id.ivBack -> requireActivity().onBackPressed()
            R.id.tvPayment -> {
                val defaultCard = cardList.filter { it.default }
                if (defaultCard.isNotEmpty()) {
//                    val data = JsonParser().parse(client_secret).asJsonObject.apply {
//                        addProperty("stripeCustomerId",
//                            pref.getPrefString(SessionManager.STRIPE_CUS_ID))
//                        addProperty("stripeId", defaultCard[0].id)
//                        addProperty("cardId", defaultCard[0].id)
//                    }
//                    viewModel.placeOrder(data)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            receiver,
            IntentFilter("CARD_ADDED")
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
    }


    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CARD_ADDED") {
                if (isInternetAvailable()) {
                    loading(true)
                    viewModel.getCards(pref.getPrefString(SessionManager.STRIPE_CUS_ID)!!)
                }
            }
        }

    }


    private fun startCheckout(token: String) {
        val params = PaymentMethodCreateParams.create(PaymentMethodCreateParams.Card.create(token),
            PaymentMethod.BillingDetails.Builder().setName(pref.getUserData()?.name?:"").setEmail(pref.getUserData()?.email?:"").build())
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
    private class PaymentResultCallback(activity: PaymentMethodsFragment) :
        ApiResultCallback<PaymentIntentResult> {
        private val activityRef: WeakReference<PaymentMethodsFragment>

        override fun onSuccess(result: PaymentIntentResult) {
            val activity: PaymentMethodsFragment = activityRef.get() ?: return
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
            val activity: PaymentMethodsFragment = activityRef.get() ?: return

            // Payment request failed – allow retrying using the same payment method
            util.IOSDialog(activity.requireActivity(), "Error$e")
        }

        init {
            activityRef = WeakReference<PaymentMethodsFragment>(activity)
        }
    }

    private fun BOOKPRODUCT(json: String?, msg: String) {
        viewModel.stripePaymentDone(pay_amount, JsonParser().parse(json).asJsonObject,booking_id)
    }

}