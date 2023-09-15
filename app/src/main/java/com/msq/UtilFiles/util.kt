package com.msq.UtilFiles

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.msq.R
import com.msq.util.ext.commonIosPopup
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object util {
    const val SECURITYKEY = "gym_bae_007"
    var myDir_images_sent =
        File(Environment.getExternalStoragePublicDirectory("huakia"), "Images/Sent")
    var Invalid_Authorization = "Invaild Authorization"
    const val NOTIFICATION_MESSAGE = "com.msq.Message_Handle_Brodcast_Reciever.DISPLAY_MESSAGE"
    fun initializeProgress(context: Context?): ProgressDialog {
        /* ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.setCancelable(false);
        return progressDialog;*/
        val progress = ProgressDialog.show(context, null, null, true)
        progress.setContentView(R.layout.layout_progressbar)
        progress.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progress.dismiss()

        /* ProgressDialog pd = new ProgressDialog(context);
        pd = new ProgressDialog(context, R.style.MyTheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);*/return progress
    }
//
//    fun decode_string_base64(string: String?): String {
//        var decoded_string = ""
//        val data = Base64.decode(string, Base64.DEFAULT)
//        try {
//            decoded_string = String(data, "UTF-8")
//        } catch (e: UnsupportedEncodingException) {
//            e.printStackTrace()
//        }
//        return decoded_string
//    }

    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun IOSDialog(context: Context?, message: String?) {
        if (context is AppCompatActivity)
            context.commonIosPopup(msg = message!!)
            { isOK, d ->
                d.dismiss()
            } else
            com.ligl.android.widget.iosdialog.IOSDialog.Builder(context)
                .setTitle(context!!.getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK\n", null)
                /*.setNegativeButton("Cancel", null) */.show();

    }

    @JvmStatic
    fun IOSDialog(context: AppCompatActivity, message: String?) {
        /*new IOSDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK\n", null)
                */
        /* .setNegativeButton("Cancel", null)*/ /*.show();*/
        context.commonIosPopup(msg = message!!)
        { isOK, d ->
            d.dismiss()
        }
    }

    @JvmStatic
    fun IOSDialog(context: Fragment, message: String?) {
        /*new IOSDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK\n", null)
                */
        /* .setNegativeButton("Cancel", null)*/ /*.show();*/
        context.commonIosPopup(msg = message!!)
        { isOK, d ->
            d.dismiss()
        }
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
            } catch (e: Exception) {
                // Eat it
                e.printStackTrace()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    @JvmField
    var internet_Connection_Error = "Please Check Your Internet Connection"
    var message = ""
    fun hideKeyboard(context: Activity) {
        val view = context.currentFocus
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hideKeyboard(context: AppCompatActivity) {
        val view = context.currentFocus
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun isValidEmail(email: String?): Boolean {
        val EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun getOutputMediaFile(type: Int, file_name: String): File {
        val mediaFile: File
        mediaFile = File(myDir_images_sent, "$file_name.jpg")
        return mediaFile
    }

    fun getOutputMediaFileUri(file: File?): Uri {
        return Uri.fromFile(file)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKatOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video1" == type) {
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
            return getDataColumn(context, uri, null, null)
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
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs,
                null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
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

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun convertTimeStampDateTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        ///  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        val sdf = SimpleDateFormat("MM-dd-yyyy")
        sdf.timeZone = tz
        val currenTimeZone = Date(timestamp * 1000)
        return sdf.format(currenTimeZone)
    }

    fun convertTimeStampDateTime111(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        ///  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.timeZone = tz
        val currenTimeZone = Date(timestamp * 1000)
        return sdf.format(currenTimeZone)
    }

    fun convertTimeStampDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
        sdf.timeZone = tz
        val currenTimeZone = Date(timestamp * 1000)
        return sdf.format(currenTimeZone)
    }

    fun convertDateTimeStamp(date_text: String?): String {
        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
        var date: Date? = null
        try {
            date = formatter.parse(date_text) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val output = date!!.time / 1000L
        val str = java.lang.Long.toString(output)
        val timestamp = str.toLong()
        return timestamp.toString()
    }

    fun getCurrencySymbol(countryCode: String?): String {
        return Currency.getInstance(Locale("", countryCode)).symbol
    }

    fun getDecimalFormattedString(value: String): String {
        val lst = StringTokenizer(value, ".")
        var str1 = value
        var str2 = ""
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken()
            str2 = lst.nextToken()
        }
        var str3 = ""
        var i = 0
        var j = -1 + str1.length
        if (str1[-1 + str1.length] == '.') {
            j--
            str3 = "."
        }
        var k = j
        while (true) {
            if (k < 0) {
                if (str2.length > 0) str3 = "$str3.$str2"
                return str3
            }
            if (i == 3) {
                str3 = ",$str3"
                i = 0
            }
            str3 = str1[k].toString() + str3
            i++
            k--
        }
    }
}