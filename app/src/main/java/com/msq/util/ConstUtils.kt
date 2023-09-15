package com.msq.util

import android.content.Context
import android.os.Environment
import com.msq.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ConstUtils {
    companion object {
        fun createImageFile(context: Context): File {
            // Create an image file eventName
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = context.getExternalFilesDir(
                "${Environment.DIRECTORY_PICTURES}${File.separator}${
                    context.getString(
                        R.string.app_name
                    )
                }"
            )
//            val storageDir = File("${Environment.getExternalStorageDirectory()}${File.separator}instacuts")
            storageDir!!.mkdir()
            val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )
            return image
        }

        val REQUEST_TAKE_VIDEO = 3230
        val REQUEST_TAKE_PHOTO = 3210
        val REQUEST_IMAGE_GET = 1032
        val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1121

        val TYPE_USER = "0"
        val TYPE_PROVIDER = "1"

        val RATE_TO_PROVIDER = "1"
        val RATE_TO_USER = "2"

        val TEXT_MSG = "0"
        val IMAGE_MSG = "1"
        val VIDEO_MSG = "2"
        val AUDIO_MSG = "3"
        val GIF_MSG = "4"
//        const val PUBLISH_KEY = "pk_test_51Ke0QhFTwkTjEW92UVKsMLdqxFjZB9tvPUOaZPZGPDcFG2DizQQPfWFdB0mJj9uqKPrsHB9CVAsyjahaUl659Wpd00IE6nJUe4"
//        const val SECRET_KEY="sk_test_51Ke0QhFTwkTjEW92qOZRw3U2iU02E9hJvcuwu53d8wnLvb3PpB9DtBaFtlBD7TGoz50JcUWbU24lbLgRT2FyBZnz00LIwp8ylG"
        const val PUBLISH_KEY = "pk_test_51JGRfBGCY9DpIr5vORI38cM2qdtbCAb316w74qJ8ZEXlSvqvvUiWbft279VNLyoQJnqpqNoRCMMa3rbWT76D1qRJ00UXvrq20V"
        const val SECRET_KEY="sk_test_51JGRfBGCY9DpIr5vb3osl9D8P31uKdqDTQfq6mHz9N9jPHAh5Olg0pzRfR2agTqalx4tICeYYvGDETKIkETbr3xP00TXnJWw3B"

        fun String.paymentType() = when (this) {
            "1" -> "Cash"
            "2" -> "Stripe"
            else -> "Other"
        }
    }
}