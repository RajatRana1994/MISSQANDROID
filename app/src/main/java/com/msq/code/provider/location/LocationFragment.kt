package com.msq.code.provider.location

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.BookingPROViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_location.*
import java.util.*


class LocationFragment : BaseFragment(), View.OnClickListener {
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    val service_name by lazy { requireArguments().getString("service_name") ?: "" }
    lateinit var viewModel: BookingPROViewModel
    val PLACE_PICKER_REQUEST = 1010
    var latitude = ""
    var longitude = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingPROViewModel::class.java)
        onClicks()

        observers()

    }

    private fun locationget() {
        // Initialize Places.
        Places.initialize(requireActivity(), "AIzaSyAA-k3KpN8PbS5u4_9qLFGIFZ_fIm52iM4")
        // Create a new Places client instance.
        val placesClient = Places.createClient(requireActivity())
        // Set the fields to specify which types of place data to return.
        val fields = Arrays.asList(
            Place.Field.ADDRESS,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ID,
        )
        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(
            requireActivity()
        )
        startActivityForResult(intent, PLACE_PICKER_REQUEST)
    }


    private fun observers() {
        viewModel.mBookingStepEntity.observe(viewLifecycleOwner) {
            it?.let {
                if (it.success && btnStartServiceNow.isEnabled) {
                    btnStartServiceNow.isEnabled = false
                    edtSearch.isEnabled = false
                    viewModel.mBookingStepEntity.value = null
                    showFragment(FragConst.CONTACTED_ORDER_CLIENT_PRO,
                        bundleOf("booking_id" to booking_id,"service_name" to service_name))
                }
            }
        }
    }

    private fun onClicks() {
        btnStartServiceNow.setOnClickListener(this)
        edtSearch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            edtSearch -> locationget()
            btnStartServiceNow -> {
                if (latitude.isNotEmpty() && longitude.isNotEmpty())
                    JsonObject().apply {
                        addProperty("address", tvAddress.text.toString())
                        addProperty("city", tvCity.text.toString())
                        addProperty("state", tvState.text.toString())
                        addProperty("postCode", tvZipcode.text.toString())
                        addProperty("latitude", latitude)
                        addProperty("longitude", longitude)
                        viewModel.bookingSteps(booking_id, "1", this)
                    }
                else toast("Complete address is required!")
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                latitude = place.latLng!!.latitude.toString()
                longitude = place.latLng!!.longitude.toString()
                val gcd = Geocoder(requireActivity(),
                    Locale.getDefault()).getFromLocation(place.latLng!!.latitude,
                    place.latLng!!.longitude,
                    1)
                if (gcd.isEmpty()) return
                tvAddress.setText(place.name)
                tvCity.setText(gcd[0].locality ?: gcd[0].adminArea)
                tvState.setText(gcd[0].adminArea ?: gcd[0].subAdminArea)
                tvZipcode.setText(gcd[0].postalCode)
                tvAddress.isEnabled = tvAddress.text.toString().isEmpty()
                tvCity.isEnabled = tvCity.text.toString().isEmpty()
                tvState.isEnabled = tvState.text.toString().isEmpty()
                tvZipcode.isEnabled = tvZipcode.text.toString().isEmpty()
            }
        }
    }

}