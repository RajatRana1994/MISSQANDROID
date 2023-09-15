package com.msq.UtilFiles

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*

class LocationLiveData {

    private val locationData=MutableLiveData<Location?>(null)
    private var context:Context?=null
    fun init(context: Context):LiveData<Location?>{
        this.context=context
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        //Check if lastLocation is available
        startLocationUpdate()
        return locationData!!
    }
    companion object {
        lateinit var fusedLocationProviderClient: FusedLocationProviderClient
        val locationRequest: LocationRequest =
                LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                        .setInterval(5 * 1000)
                        .setFastestInterval(1 * 1000)
                        .setSmallestDisplacement(100F)
    }


    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult?) {
            locationData.value = location?.lastLocation.also {
                locationRequest.fastestInterval = 10 * 1000
                locationRequest.interval = 30 * 1000
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun startLocationUpdate(){
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { locationData.value = it }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun stopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    fun onInactive() {
        stopLocationUpdate()
    }

    @SuppressLint("MissingPermission")
    fun enableFasterRequest() {
        locationRequest.fastestInterval = 1 * 1000
        locationRequest.interval = 10 * 1000
        locationRequest.smallestDisplacement = 1F
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


}