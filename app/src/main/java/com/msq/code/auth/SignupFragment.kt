package com.msq.code.auth

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.msq.BaseFragment
import com.msq.Parser.AllAPIS
import com.msq.Parser.Message
import com.msq.Parser.PostMethod
import com.msq.R
import com.msq.UtilFiles.Parameters
import com.msq.UtilFiles.SavePref
import com.msq.UtilFiles.util
import com.msq.base.FragConst
import com.msq.rest.entity.AuthEntity
import com.msq.util.ConstUtils
import com.msq.util.SessionManager
import com.msq.util.ext.drawableSmall
import com.msq.util.ext.toast
import kotlinx.android.synthetic.main.fragment_signup.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class SignupFragment : BaseFragment(), View.OnClickListener {
    private var savePref: SavePref? = null
    var mDialog: ProgressDialog? = null
    var api_type = "";
    var image = "";
    private val PLACE_PICKER_REQUEST = 787
    var latitude = ""
    var longitude = ""
    var city = ""
    var state = ""
    var postCode = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        updateDrawbleSize()
    }

    private fun updateDrawbleSize() {
        edtFullName.drawableSmall(R.drawable.ic_user)
        edtAddress.drawableSmall(R.drawable.ic_location)
        edtPhone.drawableSmall(R.drawable.ic_phone)
        edtEmail.drawableSmall(R.drawable.ic_email)
        edtPassword.drawableSmall(R.drawable.ic_key)
        edtConPassword.drawableSmall(R.drawable.ic_key)
    }

    private fun onClick() {
        savePref = SavePref(context)
        mDialog = util.initializeProgress(context)


        btnLogin.setOnClickListener(this)
        edtAddress.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            btnLogin -> SignUpProcess()
            edtAddress -> locationget()
        }
    }

    private fun SignUpProcess() {
        if (city.isEmpty() ||edtCity.isVisible) city = edtCity.text.toString()
        if (state.isEmpty()||edtState.isVisible) state = edtState.text.toString()
        if (postCode.isEmpty()||edtZip.isVisible) postCode = edtZip.text.toString()

        if (edtFullName.text.toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Name")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtAddress.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Address")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtPhone.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Phone")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtEmail.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Email Address")
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
        } else if (!util.isValidEmail(edtEmail.getText().toString())) {
            util.IOSDialog(requireActivity(), "Please Enter a Valid Email Address")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake))
        } else if (edtPassword.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Password")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (edtConPassword.getText().toString().trim { it <= ' ' }.isEmpty()) {
            util.IOSDialog(requireActivity(), "Please Enter Confirm Password")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else if (!edtPassword.getText().toString().equals(edtPassword.getText().toString())) {
            util.IOSDialog(requireActivity(), "Password not match")
            btnLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        } else {
            USER_SIGNUP_API()
        }
    }

    private fun USER_SIGNUP_API() {
        api_type = "1"
        util.hideKeyboard(requireActivity())
        mDialog!!.show()
        val formBuilder = MultipartBody.Builder()
        formBuilder.setType(MultipartBody.FORM)
        if (!image!!.isEmpty()) {
            val MEDIA_TYPE =
                if (image!!.endsWith("png")) "image/png".toMediaTypeOrNull() else "image/jpeg"
                    .toMediaTypeOrNull()
            val file: File = File(image)
            formBuilder.addFormDataPart(
                Parameters.IMAGE, file.name, RequestBody.create(MEDIA_TYPE, file)
            )
        }
        formBuilder.addFormDataPart(Parameters.NAME, edtFullName.text.toString().trim())
        formBuilder.addFormDataPart(Parameters.EMAIL, edtEmail.text.toString().trim())
        formBuilder.addFormDataPart(Parameters.PHONE, edtPhone.text.toString().trim())
        formBuilder.addFormDataPart(Parameters.PASSWORD, edtPassword.text.toString().trim())
        formBuilder.addFormDataPart(Parameters.ADDRESS, edtAddress.text.toString().trim())
        formBuilder.addFormDataPart(Parameters.USERTYPE, ConstUtils.TYPE_USER)
        formBuilder.addFormDataPart(Parameters.LATITUDE, latitude)
        formBuilder.addFormDataPart(Parameters.LONGITUDE, longitude)
        formBuilder.addFormDataPart(Parameters.CITY, city)
        formBuilder.addFormDataPart(Parameters.STATE, state)
        formBuilder.addFormDataPart(Parameters.POSTCODE, postCode)

        formBuilder.addFormDataPart(Parameters.DEVICE_TYPE, "1")
        formBuilder.addFormDataPart(
            Parameters.DEVICE_TOKEN,
            pref.getPrefString(SessionManager.DEVICE_TOKEN)!!
        )
        val formBody: RequestBody = formBuilder.build()
        val getAsyncNew = PostMethod(context, mDialog, AllAPIS.USER_SIGNUP, formBody, "")
        getAsyncNew.hitApi()
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(event: Message) {
        mDialog!!.dismiss()
        val result: String = event.getMessage()
        if (result != null && !result.equals("", ignoreCase = true)) {
            try {
                val jsonMainobject = JSONObject(result)
                if ((if (jsonMainobject.has("code")) jsonMainobject.getString("code") else jsonMainobject.getString("status")).equals("200", ignoreCase = true)) {
                    util.hideKeyboard(requireActivity())
                    val it = Gson().fromJson<AuthEntity>(jsonMainobject.toString(),
                        AuthEntity::class.java)
//                    val body = jsonMainobject.getJSONObject("data")
//                    pref.savePrefString(SessionManager.USER_ID, it.data.id.toString())
//                    pref.savePrefString(SessionManager.USER_NAME, it.data.name)
//                    pref.savePrefString(SessionManager.USER_PROFILE, it.data.profile ?: "")
//                    pref.savePrefString(SessionManager.USER_TYPE, it.data.userType)
//                    pref.savePrefString(SessionManager.USER_DATA, Gson().toJson(it.data))
//                    pref.savePrefString(SessionManager.USER_TOKEN, it.data.authorizationKey)
//                    pref.savePrefFloat(SessionManager.BALANCE, it.data.balance ?: 0f)
//                    pref.savePrefBool(SessionManager.IS_LOGIN, true)
                    if (it.data.userType == ConstUtils.TYPE_PROVIDER) {
                        showFragment(FragConst.LOGIN)
                    } else {
                        showFragment(FragConst.LOGIN)
                    }
//                    savePref!!.authorization_key = body.getString("token")
//                    savePref!!.id = body.getString(Parameters.ID)
//                    savePref!!.name = body.optString(Parameters.NAME)
//                    savePref!!.email = body.getString(Parameters.EMAIL)
//                    savePref!!.image = body.optString(Parameters.IMAGE)
//                    savePref!!.phone = body.getString(Parameters.PHONE)
//
//                    savePref!!.setStringLatest(
//                        Parameters.ADDRESS,
//                        body.getString(Parameters.ADDRESS)
//                    )

                    util.showToast(context, "Signup Successfully!!!")
                    showFragment(FragConst.LOGIN)


                    /*  val intent = Intent(context, OTPActivity::class.java)
                      intent.putExtra("auth_key", body.getString("token"))
                      startActivity(intent)
                      overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)*/
                } else {
                    if (jsonMainobject.has("message"))
                        util.IOSDialog(requireActivity(), jsonMainobject.getString("message"))
                    else
                        util.IOSDialog(requireActivity(),
                            jsonMainobject.getString("error_message").capitalize(
                                Locale.ENGLISH))
                }
            } catch (ex: JSONException) {
                ex.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
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
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                val placeName = place.name.toString()
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
                    edtAddress.setText(placeName)
                    city = mcity
                    state = mstate
                    postCode = mpostCode
                    edtCity.setText(city)
                    edtState.setText(state)
                    edtZip.setText(postCode)
                    edtCity.isVisible = city.isEmpty()
                    edtState.isVisible = state.isEmpty()
                    edtZip.isVisible = postCode.isEmpty()
                } else toast("Please search correct address!")
            }

        }
    }

}