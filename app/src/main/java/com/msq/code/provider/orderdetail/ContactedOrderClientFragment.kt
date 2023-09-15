package com.msq.code.provider.orderdetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.BookingPROViewModel
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import com.msq.util.ext.twoDecimal
import kotlinx.android.synthetic.main.fragment_contacted_order_client.*


class ContactedOrderClientFragment : BaseFragment(), View.OnClickListener {
    val service_name by lazy { requireArguments().getString("service_name") ?: "" }
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    var latitude=0.0
    var longitude=0.0
    var phoneNumber=""
    lateinit var viewModel: BookingPROViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacted_order_client, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingPROViewModel::class.java).also {
                    it.bookingDetail(booking_id)
                }
        onClicks()
        viewModel.mBookingStepEntity.observe(viewLifecycleOwner) {
            it?.let {
                if (it.success && btnCompleted.isEnabled) {
                    btnCompleted.isEnabled = false
                    viewModel.mBookingStepEntity.value = null
                    showFragment(FragConst.ORDER_PROOF_PRO,
                        bundleOf("service_name" to service_name, "booking_id" to booking_id))
                }
            }
        }
        viewModel.mBookingDetailEntity.observe(viewLifecycleOwner) {
            if (it.success) {
                (it.data).also {
                    tvTitle.text = "${getString(R.string.order_id)} #${it.id}"
                    tvCashCollect.text =
                        "₱ ${it.totalPrice.minus(it.alreadyPaidAmount ?: 0.0).twoDecimal()}"
                    tvTotalAMount.text = "₱ ${it.totalPrice.twoDecimal()}"
                    tvCoutomerName.text = it.userInfo.userName
                    tvCustomerNum.text = it.userInfo.userPhone
                    tvCustomerAddress.text = it.userInfo.userAddress
                    latitude=it.serviceProviderLocation?.latitude?.toDoubleOrNull()?:0.0
                    longitude=it.serviceProviderLocation?.longitude?.toDoubleOrNull()?:0.0
                    if(it.providerInfo.providerPhone!=null) phoneNumber=it.providerInfo.providerPhone
                }
            }
        }
    }


    private fun onClicks() {
//        btnAccept.setOnClickListener(this)
        btnCompleted.setOnClickListener(this)
        ivLocation.setOnClickListener(this)
        iv_telephone.setOnClickListener(this)
    }

    fun MovetoGoogleMap( uri: String?) {
        try {
            val gmmIntentUri = Uri.parse(uri)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            requireActivity().startActivity(mapIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun CheckPhoneCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
            ) requestPermissions(arrayOf(
                Manifest.permission.CALL_PHONE), 777) else MovetoPhoneApplication()
        } else MovetoPhoneApplication()
    }


    fun MovetoPhoneApplication() {
        try {
            if (!phoneNumber.isEmpty()) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")
                requireActivity().startActivity(intent)
            } else toast("Not Available")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivLocation -> {
                MovetoGoogleMap("geo:$latitude,$longitude")
            }
            iv_telephone -> {
                CheckPhoneCall()
            }
            btnCompleted -> {
                JsonObject().apply {
                    addProperty("isContactedClient",
                        if (switchContactedClicnt.isChecked) "1" else "0")
                    viewModel.bookingSteps(booking_id, "2", this)
                }
//                (context as AppCompatActivity).commonPopup(R.layout.dailog_default) { v, d ->
//                    v.findViewById<TextView>(R.id.btnYes).setOnClickListener {
//                        d.dismiss()
//                        showFragment(FragConst.COMPLETED_ORDER_PRO)
//                    }
//                    v.findViewById<TextView>(R.id.btnReturn).setOnClickListener {
//                        d.dismiss()
//                    }
//                }
            }
        }
    }

}