package com.msq.util.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ScaleDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.msq.R
import com.msq.base.FragConst
import com.msq.base.MSQApp.Companion.ON_CLICK_DELAY
import com.msq.base.MSQApp.Companion.lastTimeClicked
import com.msq.code.MainActivity
import com.msq.rest.entity.ErrorsBody
import com.msq.util.CheckPermission
import com.msq.util.ProgressDialogs
import com.msq.util.addFragment
import com.msq.util.replaceFragment
import retrofit2.HttpException
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.util.*
import kotlin.reflect.KClass


fun AppCompatActivity.launchActivity(cls: KClass<*>) {
    startActivity(Intent(this, cls.java))
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

fun AppCompatActivity.addMainFragment(container: View, frag: Fragment) {
    try {
        supportFragmentManager.executePendingTransactions()
    } catch (e: java.lang.Exception) {

    }
    supportFragmentManager.addFragment(container.id, frag)
}

fun Fragment.addMainFragment(container: View, frag: Fragment) {
    try {
        fragmentManager?.executePendingTransactions()
    } catch (e: java.lang.Exception) {

    }
    fragmentManager!!.addFragment(container.id, frag)
}

fun Fragment.addMainFragment(container: Int, frag: Fragment) {
    try {
        fragmentManager?.executePendingTransactions()
    } catch (e: java.lang.Exception) {

    }
    fragmentManager!!.addFragment(container, frag)
}

fun AppCompatActivity.changeMainFragment(container: View, frag: Fragment) {
    try {
        supportFragmentManager.executePendingTransactions()
    } catch (e: java.lang.Exception) {

    }
    supportFragmentManager.replaceFragment(container.id, frag)
}

fun AppCompatActivity.changeMainFragment(container: Int, frag: Fragment) {
    try {
        supportFragmentManager.executePendingTransactions()
    } catch (e: java.lang.Exception) {

    }
    supportFragmentManager.replaceFragment(container, frag)
}

fun Fragment.changeMainFragment(container: Int, frag: Fragment) {
    parentFragmentManager.replaceFragment(container, frag)
}

fun Fragment.changeTheme(themeid: Int) {
    (context as AppCompatActivity).setTheme(themeid)
}


fun Fragment.launchActivity(cls: KClass<*>, openAsNew: Boolean = false) {
    val intent = Intent(context, cls.java).apply {
        flags = if (openAsNew) {
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        } else {
            Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
    }
    startActivity(intent)
    (context as AppCompatActivity).overridePendingTransition(
        android.R.anim.fade_in,
        android.R.anim.fade_out
    )
    if (openAsNew) {
        (context as AppCompatActivity).finish()
    }
}

fun Double.priceFormat(): String {
    val nf =
        DecimalFormat("################################################.###########################################");
    return nf.format(this);
}

fun String.priceFormat(): String {
    val nf =
        DecimalFormat("################################################.###########################################");
    return nf.format(this.toDouble());
}

fun Context.launchActivity(cls: KClass<*>, openAsNew: Boolean = false) {
    val intent = Intent(this, cls.java).apply {
        if (openAsNew) {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
    startActivity(intent)
    if (openAsNew) {
        (this as AppCompatActivity).finish()
    }
    (this as AppCompatActivity).overridePendingTransition(
        android.R.anim.fade_in,
        android.R.anim.fade_out
    )
}

fun View.getLifeCycle(): LifecycleOwner {
    var context = context
    while (context !is LifecycleOwner) {
        context = (context as ContextWrapper).baseContext
    }
    return context
}

fun AppCompatActivity.makeToast(message: String?) {
    var msg = message
    if ("null" == msg) msg = "Unable to connect with the server, Please Try Later!"

    AlertDialog.Builder(this)
        .setCancelable(false)
        .setTitle(getString(R.string.app_name))
        .setMessage(msg)
        .setPositiveButton(R.string.ok) { d, w ->
            d.dismiss()
        }.show()

}

fun Fragment.makeToast(message: String?) {
    if (context != null)
        context!!.makeToast(message)
}

fun Context.makeToast(message: String?) {
    (this as AppCompatActivity).makeToast(message)
}

var internetAlertDialog: AlertDialog? = null
fun Context.internetErrorDialog(forceExit: Boolean = false) {
    try {
        if (internetAlertDialog == null) {
            internetAlertDialog =
                createInternetAlertDialog(
                    this,
                    forceExit
                )
        } else if (internetAlertDialog?.isShowing == true) {
            internetAlertDialog?.dismiss()
        }
        internetAlertDialog?.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun createInternetAlertDialog(context: Context, forceExit: Boolean): AlertDialog {
    return AlertDialog.Builder(context)
        .setCancelable(false)
        .setTitle(context.getString(R.string.app_name))
        .setMessage("Please Check You Internet connection")
        .setPositiveButton(if (forceExit) "Exit" else context.getString(R.string.ok)) { d, w ->
            if (forceExit) {
                (context as? AppCompatActivity)?.finish()
                (context as? AppCompatActivity)?.finishAffinity()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            d.dismiss()
        }.create()
}

fun Editable?.trimString(): String = this.toString().trim()

fun EditText.getStringText(): String = this.text.toString().trim()

fun EditText.clear() = setText("")

fun AppCompatActivity.isInternetAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

}

fun Context.isInternetAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

}

fun Fragment.isInternetAvailable(): Boolean {
    val connectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

}

fun Activity.hideKeyboard() {
    val view = this.currentFocus
    view?.let { v ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val hideSoftInputFromWindow =
            imm?.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                ?: false
        if (!hideSoftInputFromWindow) imm?.hideSoftInputFromWindow(
            v.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}


fun AppCompatActivity.internetNotAvailableException() {
    makeToast("internet Not Available Exception")
}

fun Fragment.internetNotAvaliableException() {
    makeToast("internet Not Available Exception")
}

fun Fragment.hideKeyboard() {
    val activity = context as AppCompatActivity
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatActivity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun EditText.makeEmpty() {
    setText("")
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.setEndDrawableClickLisetnier(click: (View) -> Unit) {
    this.setOnTouchListener(object : View.OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >=
                    this@setEndDrawableClickLisetnier.right - this@setEndDrawableClickLisetnier.compoundDrawables[drawableRight].bounds.width() - 30
                ) {
                    click(this@setEndDrawableClickLisetnier)
                    return true
                }
            }
            return false
        }
    })
}

fun View.setOnSingleClickListener(onSafeClick: (View) -> Unit) {
    setOnClickListener {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < ON_CLICK_DELAY) {
            return@setOnClickListener
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeClick(this)
    }
}

fun View.setOnSingleClickListener(delay: Int = 0, onSafeClick: View.OnClickListener) {
    setOnClickListener {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < ((if (delay != 0) delay else ON_CLICK_DELAY))) {
            return@setOnClickListener
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeClick.onClick(this)
    }
}

fun EditText.addTextChangedListener(result: (String) -> Unit) {

    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            //    result.onTextChanged(s.trimString())
            result(s.trimString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            Log.d("beforeTextChanged", s.toString())
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.d("onTextChanged", s.toString())
        }
    })
}

fun View.switchVisibility(goneView: View? = null) {
    if (goneView != null) goneView.visibility = View.GONE
    visibility = View.VISIBLE
}

fun View.onOffVisibility(on: Boolean = true) {
    if (on.not()) visibility = View.GONE else visibility = View.VISIBLE
}

fun View.isShowing() = visibility == View.VISIBLE

fun View.onOffInvisibility(on: Boolean = true) {
    if (on.not()) visibility = View.INVISIBLE else visibility = View.VISIBLE
}

fun AppCompatActivity.loading(show: Boolean) {
    if (show) ProgressDialogs.showLoading(this) else ProgressDialogs.hideLoading()
}

fun Fragment.loading(show: Boolean) {
    if (this.isVisible)
    if (show) ProgressDialogs.showLoading(this.context as AppCompatActivity) else ProgressDialogs.hideLoading()
}

/*
fun <M, B : ViewDataBinding> RecyclerView.setMyAdapter(@LayoutRes layoutRes: Int, list: List<M>? = emptyList()): GRecyclerAdapter<M, B> {
    val adapter = GRecyclerAdapter<M, B>(layoutRes)
    return adapter.submitList(list)
}*/

/*fun <M, B : ViewDataBinding> RecyclerView.setMyAdapter(
    @LayoutRes layoutRes: Int, list: List<M>? = emptyList(),
    listner: (B, M, Int) -> Unit
): GRecyclerAdapter<M, B> {
    val adapter = GRecyclerAdapter(
        layoutRes,
        object : GRecyclerHolderListener<M, B> {
            override fun populateItemHolder(binding: B, data: M, position: Int) {
                listner(binding, data, position)
            }
        })

    return adapter.submitList(list)
}*/

//fun Spinner.setSpinnerSelectedListner(listener: SpinnerItemSelectedListener) {
//    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//            listener.onItemSelected(parent, view, position, id)
//        }
//
//        override fun onNothingSelected(parent: AdapterView<*>) {
//            Log.d("onNothingSelected", "onNothingSelected")
//        }
//    }
//}

fun View.converToBitMap(): Bitmap {
    //context: Context, view: View
    val displayMetrics = DisplayMetrics()
    (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    layoutParams =
        ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
    layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
    buildDrawingCache()
    val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    draw(canvas)

    return bitmap
}

inline fun <reified T : Enum<T>> Intent.putExtra(victim: T): Intent =
    putExtra(T::class.qualifiedName, victim.ordinal)

inline fun <reified T : Enum<T>> Intent.getEnumExtra(): T? =
    getIntExtra(T::class.qualifiedName, -1)
        .takeUnless { it == -1 }
        ?.let { T::class.java.enumConstants?.get(it) }

inline fun <reified T : Enum<T>> Bundle.putExtra(victim: T) =
    putInt(T::class.qualifiedName, victim.ordinal)

inline fun <reified T : Enum<T>> Bundle.getEnumExtra(): T? =
    getInt(T::class.qualifiedName, -1)
        .takeUnless { it == -1 }
        ?.let { T::class.java.enumConstants?.get(it) }

fun <T> MutableLiveData<T?>.observeNN(@NonNull owner: LifecycleOwner, obs: (t: T) -> Unit) {
    this.observe(owner, Observer {
        if (it != null) {
            obs(it)
            this.postValue(null)
        }
    })
}

//fun LatLng?.distanceTo(destination: LatLng): String {
//    val pk = (180f / Math.PI)
//
//    val a1 = (this?.latitude ?: 0.0) / pk
//    val a2 = (this?.longitude ?: 0.0) / pk
//    val b1 = destination.latitude / pk
//    val b2 = destination.longitude / pk
//
//    val t1 = cos(a1) * cos(a2) * cos(b1) * cos(b2)
//    val t2 = cos(a1) * sin(a2) * cos(b1) * sin(b2)
//    val t3 = sin(a1) * sin(b1)
//    val tt = acos(t1 + t2 + t3)
//
//    val meters = 6366000 * tt
//    val miles = ((meters / 1609.34) * 100).toInt().toDouble() / 100.0f
//    return miles.toString()
//}

fun String.substringMax(@IntRange(from = 1) strLength: Int): String {
    if (isEmpty()) return this
    if (length < strLength) return this
    return substring(0, strLength)
}

fun TextView.makeClickSpan(text: String, start: Int, end: Int, action: () -> Unit) {
    val spannableString = SpannableString(text)
    val clickable = object : ClickableSpan() {
        override fun onClick(v: View) {
            action()
        }
    }
    spannableString.setSpan(clickable, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(
        ForegroundColorSpan(Color.parseColor("#FF6633")),
        start,
        end,
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
    this.text = spannableString
    setMovementMethod(LinkMovementMethod.getInstance());
}

fun String.openInBrowser(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(this)
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun String.openInMail(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@renteeapp.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Rentee App Support")
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun String.toCamelCase(): String {
    if (isEmpty()) return this
    val last = subSequence(1, length).toString()
    val first = this.toCharArray()[0].toString().toUpperCase(Locale.getDefault())
    return first + last.toLowerCase(Locale.getDefault())
}


fun EditText.delayWatcher(delay: Long = 1500, call: (String) -> Runnable) {
    Handler().apply {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.toString().isNotEmpty())
                    postDelayed(call(text.toString()), delay)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) =
                removeCallbacks(call(text.toString()))
        })
    }
}

fun EditText.addWatcher(call: (String) -> Unit) {
    Handler().apply {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                call(text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        })
    }
}


fun EditText.KeyEventSearch(action: (String) -> Unit) {
    setOnKeyListener(object : View.OnKeyListener {
        override fun onKey(
            v: View,
            keyCode: Int,
            event: KeyEvent
        ): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        action(text.toString())
                        return true
                    }
                    else -> {
                    }
                }
            }
            return false
        }
    })
}

fun ImageView.darkness() {
    background.setColorFilter(Color.parseColor("#AB000000"), PorterDuff.Mode.DARKEN)

}


fun AppCompatActivity.composeMail(subject: String, emailTo: String = "contact@instacuts.us") {
    var isFound = false
    val shareIntent = Intent(Intent.ACTION_SEND)
    try {
        shareIntent.type = "*/*";
        shareIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf<String>(emailTo))
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sent by Android device")
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        val pm = packageManager;
        val activityList = pm.queryIntentActivities(shareIntent, 0)

        for (app in activityList) {
            if ((app.activityInfo.name).contains("google.android.gm") || (app.activityInfo.name).contains(
                    "gmail"
                )
            ) {
                isFound = true
                val activity = app.activityInfo
                val name = ComponentName(activity.applicationInfo.packageName, activity.name)
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                shareIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                shareIntent.component = name
                startActivity(shareIntent)
            }
        }
        if (!isFound) {
            try {
                val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntent(arrayOfNulls<Parcelable>(shareIntent.size())))
                startActivity(chooserIntent)
            } catch (e: java.lang.Exception) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    } catch (e: Exception) {
        try {
            val chooserIntent = Intent.createChooser(shareIntent, "Select email app:")
            startActivity(chooserIntent)
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}

fun AppCompatActivity.makePhoneCall(phoneNumber: String) {
    try {
        CheckPermission(this).phoneCallPermission {
            if (!phoneNumber.isEmpty()) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")
                startActivity(intent)
            }
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun Window.statusBarTheme(color: Int) {
    try {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        ) {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            statusBarColor = color
            val darkness =
                1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(
                    color
                )) / 255;
            if (darkness < 0.5) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                };
            }
        }
    } catch (e: Exception) {
    }
}

fun View.bgTint(color: Int) {
    backgroundTintList = (ContextCompat.getColorStateList(context, color))
}


fun String.addQty(onDone: (Int) -> Unit): String {
    try {
        val qty = "${this.toLowerCase(Locale.ENGLISH).replace("add", "0").toInt() + 1}"
        onDone.invoke(qty.toInt())
        return qty
    } catch (e: NumberFormatException) {
        onDone.invoke(1)
        return "1"
    }
}

fun String.removeQty(onDone: (Int) -> Unit): String {
    if (this.toLowerCase(Locale.ENGLISH).replace("add", "0").toInt() > 0) {
        val qty = "${this.toLowerCase(Locale.ENGLISH).replace("add", "0").toInt() - 1}"
        onDone.invoke(qty.toInt())
        return qty
    }
    onDone.invoke(this.toLowerCase(Locale.ENGLISH).replace("add", "0").toInt())
    return this
}

fun Context.errorMessage(error: Throwable): String {
    try {
        val source = (error as HttpException).response()!!.errorBody()!!.source()
        source.request(Long.MAX_VALUE); // request the entire body.
        val buffer = source.buffer();
        // clone buffer before reading from
        val responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"))
        val gson = Gson()
        val err = gson.fromJson<ErrorsBody>(responseBodyString, ErrorsBody::class.java)
//        if (err.code==400) (this as AppCompatActivity).customDialog(R.layout.layout_api_req_failed) { s, d ->
//            s.findViewById<TextView>(R.id.btnContinue).setOnClickListener {
//                d.dismiss()
//                onBackPressed()
//            }
//        }
        if (err.code==401) (this as MainActivity).logout()
        return (err.message?:err.error_message).replace("_", " ")
    } catch (e: Exception) {
        return getString(R.string.something_went_wrong)
    }
}

fun TextView.drawableSmall(drblID: Int,width:Float=context.resources.getDimension(R.dimen._32sdp),height:Float=context.resources.getDimension(R.dimen._32sdp)) {
    val drawable = ScaleDrawable(
        context.resources.getDrawable(drblID),
        0,
        width.toFloat(),
        height.toFloat()
    ).drawable!!
    drawable.setBounds(0, 0, width.toInt(), height.toInt());
    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}
fun EditText.drawableSmall(drblID: Int,width:Float=context.resources.getDimension(R.dimen._32sdp),height:Float=context.resources.getDimension(R.dimen._32sdp)) {
    val drawable = ScaleDrawable(
        context.resources.getDrawable(drblID),
        0,
        width.toFloat(),
        height.toFloat()
    ).drawable!!
    drawable.setBounds(0, 0, width.toInt(), height.toInt());
    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

//    // get huge drawable
//    // get huge drawable
//    val drawable = context.resources.getDrawable(drblID)
//// create our wrapper
//// create our wrapper
//    val wrappedDrawable = WrappedDrawable(drawable)
//// set bounds on wrapper
//// set bounds on wrapper
//    wrappedDrawable.setBounds(0, 0, 32, 32)
//// use wrapped drawable
//// use wrapped drawable
//    setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null)
}