package com.msq.code.auth

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.buuzconnect.uis.addevent.AuthViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.msq.BaseFragment
import com.msq.R
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import com.msq.rest.entity.MSQService
import com.msq.util.CheckPermission
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_signup_provider.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import me.shouheng.compress.strategy.config.ScaleMode
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.*


class SignupProviderFragment : BaseFragment(), View.OnClickListener {

    private val PLACE_PICKER_REQUEST = 787
    var latitude = ""
    var longitude = ""
    var city = ""
    var state = ""
    var postCode = ""
    var selectedService: String = ""
    var dob: String = ""
    lateinit var viewModel: AuthViewModel

    companion object {
        var myImageUri = ""
    }

    var typeFile = 0
    var mPictureFIle: File? = null
    var mNBIClearanceFIle: File? = null
    var mPrimaryIDFIle: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_provider, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
            .get(AuthViewModel::class.java).also {
                it.getServices()
            }
        onClick()
        observers()
    }

    private fun observers() {
        viewModel.mServiceListEntity.observe(viewLifecycleOwner) {
            rvServices.adapter =
                ServiceSelectAdapter(it.data.result as MutableList<MSQService>) { id, pos ->
                    selectedService = id
                }
        }
        viewModel.mAuthEntity.observe(viewLifecycleOwner) {
//            pref.savePrefString(SessionManager.USER_ID, it.data.id.toString())
//            pref.savePrefString(SessionManager.USER_NAME, it.data.name)
//            pref.savePrefString(SessionManager.USER_PROFILE, it.data.profile ?: "")
//            pref.savePrefString(SessionManager.USER_TYPE, it.data.userType)
//            pref.savePrefString(SessionManager.USER_TOKEN, it.data.authorizationKey)
//            pref.savePrefFloat(SessionManager.BALANCE, it.data.balance ?: 0f)
//            pref.savePrefString(SessionManager.USER_DATA, Gson().toJson(it.data))
//            pref.savePrefBool(SessionManager.IS_LOGIN, true)
            toast(it.message)
//            if (it.data.userType == ConstUtils.TYPE_PROVIDER) {
//                if (it.data.stripeId!!.isNullOrEmpty())
//                    showFragment(FragConst.CONNECT_PRO)
//                else
//                    showFragment(FragConst.HOME_PRO)
//            } else {
//                showFragment(FragConst.HOME)
                showFragment(FragConst.LOGIN)
//            }
        }
    }


    private fun onClick() {
        btnLogin.setOnClickListener(this)
        edtAddress.setOnClickListener(this)
        edtAddress2.setOnClickListener(this)
        edtBirthDate.setOnClickListener(this)
        edtPicture.setOnClickListener(this)
        edtNBIClearance.setOnClickListener(this)
        edtPrimaryID.setOnClickListener(this)
//        cbHousekeepingNc2.setOnClickListener(this)
//        cbNannyNc2.setOnClickListener(this)
//        cbMassageNc2.setOnClickListener(this)
//        cbHaircutNc2.setOnClickListener(this)
//        cbChildTutor.setOnClickListener(this)
        cbTermsLink.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            btnLogin -> SignUpProcess()
            cbTermsLink -> showFragment(FragConst.TERMS)
            edtBirthDate -> showDatePicker()
            edtPicture -> CheckPermission(requireActivity()).storageCameraPermission {
                typeFile = 0
                showImagePop()
            }
            edtNBIClearance -> CheckPermission(requireActivity()).storageCameraPermission {
                typeFile = 1
                showImagePop()
            }
            edtPrimaryID -> CheckPermission(requireActivity()).storageCameraPermission {
                typeFile = 2
                showImagePop()
            }
            edtAddress, edtAddress2 -> locationget()
//            cbHousekeepingNc2, cbNannyNc2, cbMassageNc2, cbHaircutNc2, cbChildTutor -> {
//                cbHousekeepingNc2.isChecked = cbHousekeepingNc2 == v
//                cbNannyNc2.isChecked = cbNannyNc2 == v
//                cbMassageNc2.isChecked = cbMassageNc2 == v
//                cbHaircutNc2.isChecked = cbHaircutNc2 == v
//                cbChildTutor.isChecked = cbChildTutor == v
//                selectedService = (v as CheckedTextView).text.toString()
//            }
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
                    myImageUri = photoURI.toString()
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
                when (typeFile) {
                    0 -> {
                        edtPicture.text = result.name
                        mPictureFIle = result
                    }
                    1 -> {
                        edtNBIClearance.text = result.name
                        mNBIClearanceFIle = result
                    }
                    else -> {
                        edtPrimaryID.text = result.name
                        mPrimaryIDFIle = result
                    }
                }
            }
        }
    } catch (e: Exception) {
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
//                edtAddress.setText(place.address)
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
                    edtZip.setText(postCode)
                    edtCity.isVisible = city.isEmpty()
                    dotCity.isVisible = city.isEmpty()
                    edtState.isVisible = state.isEmpty()
                    dotState.isVisible = state.isEmpty()
                    edtZip.isVisible = postCode.isEmpty()
                    dotZip.isVisible = postCode.isEmpty()
                } else toast("Please search correct address!")
            } else if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(myImageUri))
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
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    saveCaptureImageResults(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    private fun SignUpProcess() {

        if (city.isEmpty() || edtCity.isVisible) city = edtCity.text.toString()
        if (state.isEmpty() || edtState.isVisible) state = edtState.text.toString()
        if (postCode.isEmpty() || edtZip.isVisible) postCode = edtZip.text.toString()

        if (edtFullName.text.toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Name")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtAddress.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Address")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtCity.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter City")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtState.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter State")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtZip.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Postal code")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtZip.getText().toString().trim { it <= ' ' }.length < 4) {
            util.IOSDialog(requireActivity(), "Please Enter correct Postal code")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtPhone.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Phone")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtEmail.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Email Address")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (!util.isValidEmail(edtEmail.getText().toString())) {
            util.IOSDialog(requireActivity(), "Please Enter a valid Email Address")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake))
        } else if (edtBirthDate.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), getString(R.string.plz_select_dob))
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (mPictureFIle == null) {
            util.IOSDialog(requireActivity(), "Please Upload Picture")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (mNBIClearanceFIle == null) {
            util.IOSDialog(requireActivity(), "Please Upload NBIClearance")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (mPrimaryIDFIle == null) {
            util.IOSDialog(requireActivity(), "Please Upload Primary ID")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtPassword.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Password")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtConPassword.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Confirm Password")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (!edtPassword.getText().toString().equals(edtPassword.getText().toString())) {
            util.IOSDialog(requireActivity(), "Password not match")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (selectedService.isEmpty()) {
            util.IOSDialog(requireActivity(), "Select a service")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (cbTerms.isChecked.not()) {
            util.IOSDialog(requireActivity(), "Accept Terms & Conditions")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else {
            viewModel.signupAPI(USER_SIGNUP_API())
        }
    }

    private fun USER_SIGNUP_API() = hashMapOf<String, RequestBody>().apply {
        put("name", edtFullName.text.toString().toRequestBody("multipart/form-data".toMediaType()))
        put("email", edtEmail.text.toString().toRequestBody("multipart/form-data".toMediaType()))
        put("password",
            edtPassword.text.toString().toRequestBody("multipart/form-data".toMediaType()))
        put("userType", ConstUtils.TYPE_PROVIDER.toRequestBody("multipart/form-data".toMediaType()))
        put("phone", edtPhone.text.toString().toRequestBody("multipart/form-data".toMediaType()))
        put("address",
            edtAddress.text.toString().toRequestBody("multipart/form-data".toMediaType()))
        put("latitude", latitude.toRequestBody("multipart/form-data".toMediaType()))
        put("longitude", longitude.toRequestBody("multipart/form-data".toMediaType()))
        put("city", city.toRequestBody("multipart/form-data".toMediaType()))
        put("state", state.toRequestBody("multipart/form-data".toMediaType()))
        put("postCode", postCode.toRequestBody("multipart/form-data".toMediaType()))
        put("deviceType", "1".toRequestBody("multipart/form-data".toMediaType()))
        put("deviceToken",
            pref.getPrefString(SessionManager.DEVICE_TOKEN)!!
                .toRequestBody("multipart/form-data".toMediaType()))
        put("dob", dob.toRequestBody("multipart/form-data".toMediaType()))
        put("serviceIds", selectedService.toRequestBody("multipart/form-data".toMediaType()))
        put("profile\"; filename=\"image.jpg", RequestBody.create("image/jpg".toMediaTypeOrNull(), mPictureFIle!!))
        put("nbi\"; filename=\"image.jpg", RequestBody.create("image/jpg".toMediaTypeOrNull(), mNBIClearanceFIle!!))
        put("primaryId\"; filename=\"image.jpg", RequestBody.create("image/jpg".toMediaTypeOrNull(), mPrimaryIDFIle!!))

    }

    fun showDatePicker() {
        val today = Calendar.getInstance()
        today.add(Calendar.YEAR, -18)
        DatePickerDialog(requireActivity(),
            R.style.DialogTheme,
            object : DatePicker.OnDateChangedListener,
                DatePickerDialog.OnDateSetListener {
                override fun onDateChanged(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                    edtBirthDate.text =
                        "$p1-${if ((p2 + 1) < 10) "0${p2 + 1}" else "${p2 + 1}"}-$p3"
                    Calendar.getInstance().apply {
                        set(Calendar.YEAR, p1)
                        set(Calendar.MONTH, (p2 + 1))
                        set(Calendar.DAY_OF_MONTH, p3)
                        dob = "${(this.timeInMillis) / 1000}"
                    }
                }
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)).show()
    }
}