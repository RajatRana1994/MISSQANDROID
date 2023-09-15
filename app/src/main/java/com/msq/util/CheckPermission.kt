package com.msq.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class CheckPermission (val context:Activity){

    fun allPermission(listener:()->Unit) {
        Dexter.withActivity(context)
            .withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted()) {
                        showAlertOfPermissions(context)
                    }else{
                        listener.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }

    fun smsPhonePermission(listener:()->Unit) {
        Dexter.withActivity(context)
            .withPermissions(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.CALL_PHONE,
//                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_PHONE_STATE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted()) {
//                        showAlertOfPermissions(context)
                    }else{
                        listener.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }

    fun storageCameraPermission(listener:()->Unit){
        Dexter.withActivity(context)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted()) {
                        showAlertOfPermissions(context,"context.getString(R.string.storage_permission_req)")
                    }else{
                        listener.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()

    }

    fun storagePermission(listener:()->Unit){
        Dexter.withActivity(context)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted()) {
                        showAlertOfPermissions(context,"context.getString(R.string.storage_permission_req)")
                    }else{
                        listener.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()

    }


    fun locationPermission(listener:()->Unit){
        Dexter.withActivity(context)
            .withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted()) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                          showAlertOfPermissions(context,"context.getString(R.string.location_permission_req)")
                    }else{
                        listener.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener {
//                showAlertOfPermissions(context)
            }.check()
    }
    fun receiveSMSPermission(listener:()->Unit){
        Dexter.withActivity(context)
            .withPermissions(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted()) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                            &&ActivityCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
                          showAlertOfPermissions(context,"context.getString(R.string.location_permission_req)")
                    }else{
                        listener.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }
    fun phoneCallPermission(listener:()->Unit){
        Dexter.withActivity(context)
            .withPermissions(
                Manifest.permission.CALL_PHONE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted()) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                            showAlertOfPermissions(context,"context.getString(R.string.location_permission_req)")
                    }else{
                        listener.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }
    fun readMyNumberPermission(listener:(Boolean)->Unit){
        Dexter.withActivity(context)
            .withPermissions(
                Manifest.permission.READ_PHONE_STATE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted()) {
//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
//                            showAlertOfPermissions(context,context.getString(R.string.location_permission_req))
                        listener.invoke(false)
                    }else{
                        listener.invoke(true)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }


    private fun showAlertOfPermissions(context: Activity,message:String="context.getString(R.string.all_permission_req)") {
        AlertDialog.Builder(context)
            .setTitle("Permission Error")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Settings") { dialog, which ->
                goToSettings(context)
                dialog.dismiss()
            }
            .show()
    }

    private fun goToSettings(context:Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
        context.finish()
    }


}