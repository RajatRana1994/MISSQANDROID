package com.msq.code.provider.orderdetail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.loader.content.CursorLoader
import com.msq.base.customPhotoPicker.Picker
import com.msq.base.customPhotoPicker.interfaces.PermissionCallback
import com.msq.base.customPhotoPicker.utils.PermissionUtils
import com.msq.base.customPhotoPicker.utils.PickerOptions
import com.buuzconnect.uis.addevent.BookingPROViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.base.FragConst
import com.msq.code.MainActivity
import com.msq.code.auth.SignupProviderFragment
import com.msq.util.ConstUtils
import com.msq.util.ViewModelFactory
import com.msq.util.ext.toast
import com.socialgalaxyApp.util.extension.loadUserImage
import com.socialgalaxyApp.util.extension.loadWallRound
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_order_proof.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import me.shouheng.compress.strategy.config.ScaleMode
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.io.IOException


class OrderProofFragment : BaseFragment(), View.OnClickListener {
    val booking_id by lazy { requireArguments().getString("booking_id") ?: "" }
    val service_name by lazy { requireArguments().getString("service_name") ?: "" }
    var pickerOptions: PickerOptions? = null
    lateinit var viewModel: BookingPROViewModel
    var serviceProofPic1: File? = null
    var serviceProofPic2: File? = null
    var typeFile = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_proof, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pickerOptions = PickerOptions()
        pickerOptions!!.maxCount = 1
        pickerOptions!!.excludeVideos = true
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(BookingPROViewModel::class.java)
        onClicks()
        lblCategory.text = "Service - ${service_name}"
        val drawable = when (service_name.trim()) {
            "Child Tutor" -> R.drawable.ten
            "House Keeping" -> R.drawable.two
            "Nanny" -> R.drawable.three
            "Massage Therapist" -> R.drawable.five
            "Haircut" -> R.drawable.eight
            else -> R.drawable.nine
        }
        ivBefore.loadWallRound(drawable)
        ivAfter.loadWallRound(drawable)
        observers()
    }

    private fun observers() {
        viewModel.mBookingStepEntity.observe(viewLifecycleOwner) {
            it?.let {
                if (it.success && btnComplete.isEnabled) {
                    btnComplete.isEnabled = false
                    viewModel.mBookingStepEntity.value = null
                    showFragment(FragConst.COMPLETE_ORDER_SETTLE_PRO,
                        bundleOf("booking_id" to booking_id))
                }
            }
        }
    }

    private fun onClicks() {
        tvBeforeUpload.setOnClickListener(this)
        tvAfterUpload.setOnClickListener(this)
        btnComplete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            tvBeforeUpload -> {
                PermissionUtils.checkForCameraWritePermissions(context as MainActivity,
                    object : PermissionCallback {
                        override fun onPermission(approved: Boolean) {
                            if (pickerOptions != null) {
                                typeFile = 0
                                Picker.startPicker(context as MainActivity, pickerOptions!!)
                            }
                        }
                    })

//                CheckPermission(requireActivity()).storageCameraPermission {
//                    typeFile = 0
//                    showImagePop()
//                }
            }
            tvAfterUpload -> {
                PermissionUtils.checkForCameraWritePermissions(context as MainActivity,
                    object : PermissionCallback {
                        override fun onPermission(approved: Boolean) {
                            if (pickerOptions != null) {
                                typeFile = 1
                                Picker.startPicker(context as MainActivity, pickerOptions!!)
                            }
                        }
                    })

//                CheckPermission(requireActivity()).storageCameraPermission {
//                    typeFile = 1
//                    showImagePop()
//                }
            }
            btnComplete -> {
                if (serviceProofPic1 == null) toast(getString(R.string.plz_select_before_image)) else if (serviceProofPic2 == null) toast(
                    getString(
                        R.string.plz_select_after_image)) else hashMapOf<String, RequestBody>().apply {
                    put("serviceProofPic1\"; filename=\"image.jpg",
                        RequestBody.create("image/jpg".toMediaTypeOrNull(), serviceProofPic1!!))
                    put("serviceProofPic2\"; filename=\"image.jpg",
                        RequestBody.create("image/jpg".toMediaTypeOrNull(), serviceProofPic2!!))
                    viewModel.bookingSteps(booking_id, "3", this)
                }
//                showFragment(FragConst.ORDER_DETAIL_PRO)
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
                when (typeFile) {
                    0 -> {
                        serviceProofPic1 = result
                        ivBefore.loadUserImage(result.path)
                    }
                    1 -> {
                        serviceProofPic2 = result
                        ivAfter.loadUserImage(result.path)
                    }
                }
            }
        }
    } catch (e: Exception) {
    }
    private fun saveCaptureImageResults(path: String) = try {
        val file = File(path)
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
                        serviceProofPic1 = result
                        ivBefore.loadUserImage(result.path)
                    }
                    1 -> {
                        serviceProofPic2 = result
                        ivAfter.loadUserImage(result.path)
                    }
                }
            }
        }
    } catch (e: Exception) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.REQUEST_CODE_PICKER) {
                val pathList =
                    data!!.getStringArrayListExtra(Picker.PICKED_MEDIA_LIST)
                val realPath = pathList?.mapNotNull {
                    getRealPath(requireActivity(),
                        Uri.parse(it))!!
                }
                try {
                    CropImage.activity(Uri.parse(pathList!!.get(0)))
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(1, 1)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                        .setGuidelinesColor(android.R.color.transparent).start(requireActivity(), this)
                }catch (e:Exception){
                    saveCaptureImageResults(realPath!![0])
                }
            }
            if (requestCode == ConstUtils.REQUEST_TAKE_PHOTO) {
                CropImage.activity(Uri.parse(SignupProviderFragment.myImageUri))
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(1, 1)
//                    .setMinCropWindowSize(1000,1200)
//                    .setMinCropWindowSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.6).toInt())
//                    .setMaxCropResultSize(displayMetrics.widthPixels,(displayMetrics.widthPixels*.8).toInt())
                    .setGuidelinesColor(android.R.color.transparent).start(requireActivity(), this)
            } else if (requestCode == ConstUtils.REQUEST_IMAGE_GET) {
                val uri: Uri = data?.data!!
                CropImage.activity(uri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(1, 1)
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

    fun getRealPath(context: Context, fileUri: Uri): String? {
        val realPath: String?
        // SDK < API11
        realPath = if (Build.VERSION.SDK_INT < 11) {
            getRealPathFromURI_BelowAPI11(context, fileUri)
        } else if (Build.VERSION.SDK_INT < 19) {
            getRealPathFromURI_API11to18(context, fileUri)
        } else {
            getRealPathFromURI_API19(context, fileUri)
        }
        return realPath
    }


    @SuppressLint("NewApi")
    fun getRealPathFromURI_API11to18(context: Context?, contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var result: String? = null
        val cursorLoader = CursorLoader(
            requireActivity(), contentUri!!, proj, null, null, null
        )
        val cursor = cursorLoader.loadInBackground()
        if (cursor != null) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
            cursor.close()
        }
        return result
    }

    fun getRealPathFromURI_BelowAPI11(context: Context, contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
        var column_index = 0
        var result: String? = ""
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
            cursor.close()
            return result
        }
        return result
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    fun getRealPathFromURI_API19(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


}