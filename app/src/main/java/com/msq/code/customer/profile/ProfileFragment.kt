package com.msq.code.customer.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.buuzconnect.uis.addevent.ProfilePROViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.msq.BaseFragment
import com.msq.R
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import com.msq.code.auth.SignupProviderFragment
import com.msq.code.provider.profile.ProfilePROFragment
import com.msq.rest.entity.AuthData
import com.msq.util.CheckPermission
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.addWatcher
import com.msq.util.ext.commonIosPopup
import com.msq.util.ext.toast
import com.socialgalaxyApp.util.extension.loadUserImage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_profile_pro.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import me.shouheng.compress.strategy.config.ScaleMode
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*


class ProfileFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: ProfilePROViewModel
    lateinit var userData: AuthData
    var latitude = ""
    var longitude = ""
    var city = ""
    var state = ""
    var postCode = ""
    var mPictureFIle: File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_pro, container, false)
    }

    private val PLACE_PICKER_REQUEST = 787

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userData = pref.getUserData()!!
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ProfilePROViewModel::class.java)
        onClicks()
        observers()
        updateData()
        if (pref.getPrefString(SessionManager.USER_TYPE) != ConstUtils.TYPE_USER) {
            viewModel.getServices()
        }else{
            mIvBack.visibility=View.VISIBLE
        }
    }


    private fun observers() {
        viewModel.mUpdateProfileInfoEntity.observe(viewLifecycleOwner) {
            userData = it.data
            pref.savePrefString(SessionManager.USER_DATA, Gson().toJson(it.data))
            edtCity.isVisible = false
            edtState.isVisible = false
            edtZipcode.isVisible = false
            tvCity.isVisible = false
            tvState.isVisible = false
            tvZipcode.isVisible = false
            checkToUpdate()
            updateData()
        }
        viewModel.mServiceListEntity.observe(viewLifecycleOwner, {
            edtJob.setText(it.data.result.filter { it == userData.services[0] }[0].name)
            updateData()
        })
    }

    fun updateData() {
        edtJob.isVisible = pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_PROVIDER
        tvJob.isVisible = pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_PROVIDER
        edtAddress.setText(userData.address)
        edtName.setText(userData.name)
        edtNumber.setText(userData.phone)
        ivUser.loadUserImage(userData.profile)
        tvName.setText(userData.name)
        tvEmail.setText(userData.email)
        ivRatingBar.rating = userData.rating ?: 0f
        tvRating.setText("${userData.rating ?: 0f}")
    }

    private fun onClicks() {
        edtName.addWatcher { checkToUpdate() }
        edtNumber.addWatcher { checkToUpdate() }
        edtCity.addWatcher { checkToUpdate() }
        edtState.addWatcher { checkToUpdate() }
        edtZipcode.addWatcher { checkToUpdate() }
        edtAddress.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        mIvBack.setOnClickListener(this)
        tvChangePass.setOnClickListener(this)
        ivUser.setOnClickListener {
            CheckPermission(requireActivity()).storageCameraPermission {
                showImagePop()
            }
        }
    }

    private fun checkToUpdate() {
//        btnSubmit.text = if ( city.isNotEmpty() && state.isNotEmpty() && postCode.isNotEmpty() && postCode.length < 5 && edtAddress.text.toString().trim()
//                .equals(userData.address) && edtName.text.toString().trim()
//                .equals(userData.name) && edtNumber.text.toString().trim().equals(userData.phone)
//        ) (if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_USER) "Logout" else "Feedback to ARTISANS") else {
//            if (edtCity.text.toString().isNotEmpty() && edtState.text.toString().isNotEmpty() && edtZipcode.text.toString().isNotEmpty() && edtZipcode.text.toString().length >4)
//                getString(
//                    R.string.update_profile)
//            else (if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_USER) "Logout" else "Feedback to ARTISANS")
//        }


        btnSubmit.text=if (edtName.text.toString().trim().equals(userData.name).not())  getString(R.string.update_profile)
        else if (edtNumber.text.toString().trim().equals(userData.phone).not())  getString(R.string.update_profile)
        else if (edtAddress.text.toString().trim().equals(userData.address).not())  getString(R.string.update_profile)
        else if (edtAddress.text.toString().trim().equals(userData.address) &&city.isNotEmpty() && state.isNotEmpty() && postCode.isNotEmpty() || (edtCity.isVisible
                    || edtState.isVisible
                    || edtZipcode.isVisible))  {
            if (edtCity.text.toString().isNotEmpty() && edtState.text.toString().isNotEmpty() && edtZipcode.text.toString().isNotEmpty() && edtZipcode.text.toString().length >4)
                getString(
                    R.string.update_profile)
            else (if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_USER) "Logout" else "Feedback to ARTISANS")
        }else{
            (if (pref.getPrefString(SessionManager.USER_TYPE) == ConstUtils.TYPE_USER) "Logout" else "Feedback to ARTISANS")
        }
    }

    override fun onClick(v: View?) {
        when (v!!) {
            /*ivUser -> {
                CheckPermission(requireActivity()).storageCameraPermission {
                    showImagePop()
                }
            }*/
            mIvBack -> requireActivity().onBackPressed()
            tvChangePass -> showFragment(FragConst.CHANGE_PASS_PRO)
            edtAddress -> {
                locationget()
            }
            btnSubmit -> {
                if (btnSubmit.text.toString().equals(getString(
                        R.string.update_profile))
                ) {
                    city=edtCity.text.toString()
                    state=edtState.text.toString()
                    postCode=edtZipcode.text.toString()
                    val json = JsonObject()
                    if (city.isNotEmpty() && state.isNotEmpty() && postCode.isNotEmpty()) {
                        json.addProperty("address", edtAddress.text.toString())
                        json.addProperty("latitude", latitude)
                        json.addProperty("longitude", longitude)
                        json.addProperty("city", city)
                        json.addProperty("state", state)
                        json.addProperty("postCode", postCode)
                    }else {
                        if (edtCity.isVisible
                            || edtState.isVisible
                            || edtZipcode.isVisible) {
                            toast("Please select accurate and valid address!")
                            return
                        }
                    }

                    if (edtName.text.toString().trim()
                            .equals(userData.name).not()
                    ) {
                        json.addProperty("name", edtName.text.toString())
                    }
                    if (edtNumber.text.toString().trim()
                            .equals(userData.phone).not()
                    ) {
                        json.addProperty("phone", edtNumber.text.toString())
                    }

                    viewModel.updateProfile(json)
                } else if (btnSubmit.text.toString().equals("Logout")) {
                    LogoutAlert()
                }
            }
        }
    }

    private fun LogoutAlert() {
        commonIosPopup(msg = "Are you sure Logout?",
            btNo = "No",
            btYes = "Yes",
            isSingleBtn = false) { b, dialog ->
            if (b) {
                dialog.dismiss()
                util.showToast(context, "User Logout Successfully")
                pref.clearPrefs()
                showFragment(FragConst.WELCOME)
            } else dialog.dismiss()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                edtAddress.setText(place.address)
                latitude = place.latLng!!.latitude.toString()
                longitude = place.latLng!!.longitude.toString()
                val gcd = Geocoder(requireActivity(),
                    Locale.getDefault()).getFromLocation(place.latLng!!.latitude,
                    place.latLng!!.longitude,
                    1)
                if (gcd.isEmpty()) {
                    toast("Please search correct address!")
                    return
                }
                val mcity = gcd[0].locality ?: gcd[0].adminArea ?: ""
                val mstate = gcd[0].adminArea ?: gcd[0].subAdminArea ?: ""
                val mpostCode = gcd[0].postalCode ?: ""
                if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                    edtAddress.setText(place.name)
                    city = mcity
                    state = mstate
                    postCode = mpostCode
                    edtCity.setText(city)
                    edtState.setText(state)
                    edtZipcode.setText(postCode)
                    edtCity.isVisible = city.isEmpty()
                    tvCity.isVisible = city.isEmpty()
                    edtState.isVisible = state.isEmpty()
                    tvState.isVisible = state.isEmpty()
                    edtZipcode.isVisible = postCode.isEmpty()
                    tvZipcode.isVisible = postCode.isEmpty()
                } else toast("Please search correct address!")
                checkToUpdate()
            }
            else if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(ProfilePROFragment.myImageUri))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(requireActivity(), this)
            } else if (requestCode == ConstUtils.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                    .setAspectRatio(2, 1)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(requireActivity(), this)
            }

            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {

                    var resultUri = result.uri
                    var image_url= getAbsolutePath(context!!,resultUri).toString()
                    Glide.with(this).load(image_url).into(ivUser)
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    val mBuilder: Dialog by lazy { Dialog(requireActivity()) }
    fun showImagePop() {
        mBuilder.setContentView(R.layout.camera_dialog);
        mBuilder.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation;
        mBuilder.window!!.setGravity(Gravity.BOTTOM)
        mBuilder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mBuilder.findViewById<TextView>(R.id.titleCamera)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakePictureIntent()
            }
        mBuilder.findViewById<TextView>(R.id.titleGallery)
            .setOnClickListener {
                mBuilder.dismiss()
                dispatchTakeGalleryIntent()
            }
        mBuilder.findViewById<TextView>(R.id.titleCancel)
            .setOnClickListener { mBuilder.dismiss() }
        mBuilder.show();
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    ConstUtils.createImageFile(requireActivity())
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        "${requireActivity().packageName}.provider",
                        it
                    )
                    SignupProviderFragment.myImageUri = photoURI.toString()
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, ConstUtils.REQUEST_TAKE_PHOTO)
                }
            }

        }
    }

    private fun dispatchTakeGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, ConstUtils.REQUEST_IMAGE_GET)
        }
    }

    private fun saveCaptureImageResults(data: Uri) = try {
        val file = File(data.path!!)
//        val compressedImageFile = id.zelory.compressor.Compressor(requireActivity())
//            .setMaxHeight(1000).setMaxWidth(1000)
//            .setQuality(99)
//            .setCompressFormat(Bitmap.CompressFormat.JPEG)
//            .compressToFile(file)

        GlobalScope.launch {
            val result = Compress.with(requireActivity(), file)
                .setQuality(90)
                .concrete {
                    withMaxWidth(100f)
                    withMaxHeight(100f)
                    withScaleMode(ScaleMode.SCALE_HEIGHT)
                    withIgnoreIfSmaller(true)
                }
                .get(Dispatchers.IO)
            withContext(Dispatchers.Main) {
                mPictureFIle = result
                val img=hashMapOf<String, RequestBody>().apply {
                    put("profile\"; filename=\"image.jpg",
                        RequestBody.create("image/jpg".toMediaTypeOrNull(),
                            mPictureFIle!!))
                }
                viewModel.updateProfileWithImg(img)
                btnSubmit.text=getString(
                    R.string.update_profile
                )


            }
        }
    } catch (e: Exception) {
    }

    fun getAbsolutePath(activity: Context, uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor? = null
            try {
                cursor = activity.contentResolver.query(uri, projection, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: java.lang.Exception) {
                // Eat it
                e.printStackTrace()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getImageUri(context: Context, inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}