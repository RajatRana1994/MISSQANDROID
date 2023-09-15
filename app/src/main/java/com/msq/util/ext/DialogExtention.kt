package com.msq.util.ext

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.msq.R

////
//public fun AppCompatActivity.AllinOneDialog(
//    ttle: String = "", msg: String = "", btnLeft: String = "No",
//    btnRight: String = "Yes", onLeftClick: () -> Unit, onRightClick: () -> Unit
//) {
//    val builder = AlertDialog.Builder(this)
//    val viewDialog = this.layoutInflater.inflate(R.layout.custom_dialog, null)
////    viewDialog.background = ContextCompat.getDrawable(
////        this,
////        R.drawable.shadow_border_bg_more_rounded
////    )
//    builder.setView(viewDialog)
//    val alertDialog = builder.create()
//
//    if (ttle.isEmpty()) viewDialog.findViewById<TextView>(R.id.tvHeading).visibility = View.GONE
//    if (msg.isEmpty()) viewDialog.findViewById<TextView>(R.id.tvMessage).visibility = View.GONE
//    if (btnLeft.isEmpty()) viewDialog.findViewById<TextView>(R.id.btnCancel).visibility =
//        View.GONE
//    if (btnRight.isEmpty()) viewDialog.findViewById<TextView>(R.id.btnConfirm).visibility =
//        View.GONE
//
//    viewDialog.findViewById<TextView>(R.id.tvHeading).text = ttle
//    viewDialog.findViewById<TextView>(R.id.tvMessage).text = msg
//
//    viewDialog.findViewById<TextView>(R.id.btnCancel).apply {
//        text = btnLeft
//        setOnClickListener {
//            alertDialog.cancel()
//            onLeftClick()
//        }
//    }
//    viewDialog.findViewById<TextView>(R.id.btnConfirm).apply {
//        text = btnRight
//        setOnClickListener {
//            alertDialog.cancel()
//            onRightClick()
//        }
//    }
//    alertDialog.setOnDismissListener {
//        onLeftClick()
//    }
//    alertDialog.setCancelable(true)
//    alertDialog.show()
//
//    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    val displayMetrics = DisplayMetrics()
//    windowManager?.defaultDisplay?.getMetrics(displayMetrics)
//    val displayWidth = displayMetrics.widthPixels
//    val layoutParams = WindowManager.LayoutParams()
//    layoutParams.copyFrom(alertDialog.window?.getAttributes())
//    val dialogWindowWidth = (displayWidth * 0.9f).toInt()
//    layoutParams.width = dialogWindowWidth
//    alertDialog.window?.attributes = layoutParams
//}

public fun Fragment.customDialog(
    layout: Int, definition: (View, AlertDialog) -> Unit,
) {
    val builder = AlertDialog.Builder((context as AppCompatActivity))
    val viewDialog = (context as AppCompatActivity).layoutInflater.inflate(layout, null)
//    viewDialog.background = ContextCompat.getDrawable(
//        (context as AppCompatActivity),
//        R.drawable.shadow_border_bg_more_rounded
//    )
    builder.setView(viewDialog)
    val alertDialog = builder.create()

    //definition
    definition(viewDialog, alertDialog)

    alertDialog.setCancelable(true)
    alertDialog.show()

    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val displayMetrics = DisplayMetrics()
    (context as AppCompatActivity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val displayWidth = displayMetrics.widthPixels
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(alertDialog.window?.getAttributes())
    val dialogWindowWidth = (displayWidth * 0.9f).toInt()
    layoutParams.width = dialogWindowWidth
    alertDialog.window?.attributes = layoutParams
}

public fun AppCompatActivity.customDialog(
    layout: Int, definition: (View, AlertDialog) -> Unit,
) {
    val builder = AlertDialog.Builder(this)
    val viewDialog = (this).layoutInflater.inflate(layout, null)
//    viewDialog.background =
//        ContextCompat.getDrawable((this), R.drawable.shadow_border_bg_more_rounded)
    builder.setView(viewDialog)
    val alertDialog = builder.create()

    //definition
    definition(viewDialog, alertDialog)

    alertDialog.setCancelable(true)
    alertDialog.show()

    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val displayMetrics = DisplayMetrics()
    (this).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val displayWidth = displayMetrics.widthPixels
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(alertDialog.window?.attributes)
    val dialogWindowWidth = (displayWidth * 0.9f).toInt()
    layoutParams.width = dialogWindowWidth
    alertDialog.window?.attributes = layoutParams
}

public fun AppCompatActivity.customFullDialog(
    layout: Int, definition: (View, AlertDialog) -> Unit,
) {
    val builder = AlertDialog.Builder(this)
    val viewDialog = (this).layoutInflater.inflate(layout, null)
//    viewDialog.background =
//        ContextCompat.getDrawable((this), R.drawable.shadow_border_bg_more_rounded)
    builder.setView(viewDialog)
    val alertDialog = builder.create()

    //definition
    definition(viewDialog, alertDialog)

    alertDialog.setCancelable(true)
    alertDialog.show()

    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.window!!.setGravity(Gravity.CENTER)
    val displayMetrics = DisplayMetrics()
    (this).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val displayWidth = displayMetrics.widthPixels
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(alertDialog.window?.attributes)
    val dialogWindowWidth = (displayWidth).toInt()
    layoutParams.width = dialogWindowWidth
    layoutParams.height = (displayMetrics.heightPixels * .8).toInt()
    layoutParams.gravity = Gravity.CENTER
    alertDialog.window?.attributes = layoutParams
}

public fun AppCompatActivity.bottomDialog(
    layout: Int, definition: (View, AlertDialog) -> Unit,
) {
    val builder = AlertDialog.Builder(this, R.style.DialogAnimation)
    val viewDialog = this.layoutInflater.inflate(layout, null)
    viewDialog.background = ContextCompat.getDrawable(this, R.drawable.top_round_bg)
    builder.setView(viewDialog)
    val alertDialog = builder.create()
    //definition
    definition(viewDialog, alertDialog)

    alertDialog.setCancelable(true)
    alertDialog.show()
    alertDialog.window!!.setGravity(Gravity.BOTTOM)
    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.window!!.getAttributes().windowAnimations = R.style.DialogAnimation
    val displayMetrics = DisplayMetrics()
    this.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val displayWidth = displayMetrics.widthPixels
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(alertDialog.window?.getAttributes())
    val dialogWindowWidth = (displayWidth).toInt()
    layoutParams.width = dialogWindowWidth
    layoutParams.height = WRAP_CONTENT
    alertDialog.window?.attributes = layoutParams
}
/*
public fun Fragment.AllinOneDialog(
    ttle: String = "", msg: String = "", btnLeft: String = "No",
    btnRight: String = "Yes", onLeftClick: () -> Unit, onRightClick: () -> Unit
) {
    (context as AppCompatActivity).AllinOneDialog(
        ttle,
        msg,
        btnLeft,
        btnRight,
        onLeftClick,
        onRightClick
    )
}*/


fun AppCompatActivity.showOkDialog(
    title: String,
    message: String,
    okClickListener: DialogInterface.OnClickListener?,
) {
    val aDialog = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)

    if (okClickListener == null) {
        aDialog.setPositiveButton(R.string.ok) { dialog, _ -> dialog?.dismiss() }
    } else {
        aDialog.setPositiveButton(R.string.ok, okClickListener)
    }

    aDialog.show()
}

fun Fragment.showOkDialog(
    title: String = "",
    message: String?,
    okClickListener: DialogInterface.OnClickListener?,
) {
    val aDialog = AlertDialog.Builder(context!!)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)

    if (okClickListener == null) {
        aDialog.setPositiveButton(R.string.ok) { dialog, _ -> dialog?.dismiss() }
    } else {
        aDialog.setPositiveButton(R.string.ok, okClickListener)
    }
    aDialog.show()
}


fun AppCompatActivity.toastAction(message: String?, actionName: String, action: () -> Unit) {
    Snackbar.make(findViewById<View>(android.R.id.content), message!!, Snackbar.LENGTH_SHORT)
        .apply {
            if (actionName.isNotEmpty()) setAction(actionName) { action.invoke() }.apply {
                setActionTextColor(Color.WHITE)
            }
            setBackgroundTint(ContextCompat.getColor(this@toastAction, R.color.black))
            setTextColor(ContextCompat.getColor(this@toastAction, R.color.white))
            show()
        }
}


fun Fragment.toastAction(message: String?, actionName: String, action: () -> Unit) =
    context!!.toastAction(message, actionName, action)

fun Context.toastAction(message: String?, actionName: String, action: () -> Unit) =
    (this as AppCompatActivity).toastAction(message, actionName, action)


fun AppCompatActivity.toast(
    message: String?,
    success: Boolean = true,
    dismiss: (() -> Unit)? = null,
) {
    Snackbar.make(findViewById<View>(android.R.id.content), message!!, Snackbar.LENGTH_SHORT)
        .apply {
            val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            lp.topMargin = 100
            lp.gravity = Gravity.TOP
            setBackgroundTint(
                ContextCompat.getColor(
                    this@toast,
                    if (success) R.color.black else R.color.black
                )
            )
            setTextColor(ContextCompat.getColor(this@toast, R.color.white))
            setCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    dismiss?.let { dismiss() }
                }

                override fun onShown(sb: Snackbar?) {
                    super.onShown(sb)
                }
            })
            view.layoutParams = lp
            show()
        }
}

fun Fragment.toast(message: String?, success: Boolean = true, dismiss: (() -> Unit)? = null) =
    context!!.toast(message, success, dismiss)

fun Context.toast(message: String?, success: Boolean = true, dismiss: (() -> Unit)? = null) =
    (this as AppCompatActivity).toast(message, success, dismiss)

fun AppCompatActivity.bottomSheetDialog(
    layout: Int,
    clicks: (mBottomSheetDialog: BottomSheetDialog, sheetView: View) -> Unit,
): BottomSheetDialog {
    val mBottomSheetDialog = BottomSheetDialog(this/*, R.style.BottomSheetDialogTheme*/)
    val sheetView: View = layoutInflater.inflate(layout, null)
    mBottomSheetDialog.setContentView(sheetView)
    sheetView.setBackgroundColor(Color.TRANSPARENT)
    mBottomSheetDialog.setCancelable(false)
//    sheetView.setBackgroundColor(ContextCompat.getColor(this, R.color.trans_black))
    clicks(mBottomSheetDialog, sheetView)
    mBottomSheetDialog.dismissWithAnimation = true
    val metrics = DisplayMetrics()
    getWindowManager().getDefaultDisplay().getMetrics(metrics)
    mBottomSheetDialog.window!!.statusBarColor = Color.TRANSPARENT
    mBottomSheetDialog.behavior.setPeekHeight(metrics.heightPixels, true)
    val lp = sheetView.layoutParams
    lp.width = metrics.widthPixels
    sheetView.layoutParams = lp

//    val metrics = DisplayMetrics()
//    getWindowManager().getDefaultDisplay().getMetrics(metrics)
    mBottomSheetDialog.show()
    return mBottomSheetDialog
}

fun Fragment.bottomSheetDialog(
    layout: Int,
    clicks: (mBottomSheetDialog: BottomSheetDialog, sheetView: View) -> Unit,
) {
    val mBottomSheetDialog =
        BottomSheetDialog((context as AppCompatActivity)/*, R.style.BottomSheetDialogTheme*/)
    val sheetView: View = (context as AppCompatActivity).layoutInflater.inflate(layout, null)
    mBottomSheetDialog.setContentView(sheetView)
    clicks(mBottomSheetDialog, sheetView)
    mBottomSheetDialog.dismissWithAnimation = true
    mBottomSheetDialog.window!!.statusBarColor = Color.TRANSPARENT
    mBottomSheetDialog.show()
}

fun AppCompatActivity.showErrorDialog(errorMsg: String) {
//    customDialog(R.layout.custom_dialog) { view, alertD ->
////            view.findViewById<TextView>(R.id.tvHeading)
//        view.findViewById<TextView>(R.id.tvMessage).text = errorMsg
//        view.findViewById<TextView>(R.id.btnCancel).apply {
//            text = R.string.ok
//            setOnClickListener {
//                alertD.dismiss()
//            }
//        }
//        view.findViewById<TextView>(R.id.btnConfirm).onOffVisibility(false)
//    }

    materialNotifyDialog("Stylist", errorMsg, getString(R.string.ok)) {}
}

fun Fragment.showErrorDialog(errorMsg: String) {
    (context as AppCompatActivity).showErrorDialog(errorMsg)
}

fun AppCompatActivity.materialDialog(
    tle: String,
    msg: String,
    pos: String,
    neg: String,
    onPositive: () -> Unit,
    onNeg: (() -> Unit)? = null,
): AlertDialog {
    return MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Light)
        .setTitle(tle)
        .setMessage(msg)
        .setPositiveButton(pos) { dialog, which ->
            dialog.dismiss()
            onPositive.invoke()
        }
        .setNegativeButton(neg) { dialog, which ->
            dialog.dismiss()
            if (onNeg != null) onNeg.invoke()
        }.create().apply {
//            window!!.setBackgroundDrawableResource(R.drawable.box_less_rounded_bg)
            show()
        }
}

fun AppCompatActivity.materialNotifyDialog(
    tle: String,
    msg: String,
    pos: String,
    onPositive: () -> Unit,
): AlertDialog {
    return MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Light).apply {
        setTitle(tle)
        if (msg.isNotEmpty()) setMessage(msg)
        setCancelable(false)
        setPositiveButton(pos) { dialog, which ->
            dialog.dismiss()
            onPositive.invoke()
        }
    }.create().apply {
//        window!!.setBackgroundDrawableResource(R.drawable.box_less_rounded_bg)
        show()
    }

}

fun AppCompatActivity.materialNotifyDialogR(
    tle: String,
    msg: String,
    pos: String,
    onPositive: (DialogInterface) -> Unit,
): AlertDialog {
    return MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Light).apply {
        setTitle(tle)
        if (msg.isNotEmpty()) setMessage(msg)
        setCancelable(false)
        setPositiveButton(pos) { dialog, which ->
            onPositive.invoke(dialog)
        }
    }.create().apply {
//        window!!.setBackgroundDrawableResource(R.drawable.box_less_rounded_bg)
        show()
    }

}

fun AppCompatActivity.materialListDialog(
    tle: String,
    msg: String,
    pos: String,
    onPositive: () -> Unit,
): AlertDialog {
    return MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Light).apply {
        setTitle(tle)
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this@materialListDialog, android.R.layout.test_list_item)
        setCancelable(false)
        arrayAdapter.add(msg)
        arrayAdapter.add(pos)
        setAdapter(arrayAdapter) { dialog, which ->
            onPositive.invoke()
            dialog.dismiss()
        }
    }.create().apply {
//        window!!.setBackgroundDrawableResource(R.drawable.box_less_rounded_bg)
        show()
    }

}


public fun AppCompatActivity.bottomSheet(
    layout: Int, definition: (View, Dialog) -> Unit,
) {
    val mBuilder = Dialog(this)
    val viewDialog = this.layoutInflater.inflate(layout, null)
    mBuilder.setContentView(viewDialog);
    viewDialog.background = ContextCompat.getDrawable(this, R.drawable.top_round_bg)
    mBuilder.window!!.getAttributes().windowAnimations = R.style.DialogAnimation;
    mBuilder.window!!.setGravity(Gravity.BOTTOM)
    val metrics = DisplayMetrics()
    getWindowManager().getDefaultDisplay().getMetrics(metrics)
    val lp = viewDialog.layoutParams
    lp.width = metrics.widthPixels
    viewDialog.layoutParams = lp
//    mBuilder.window!!.setLayout(
//        ViewGroup.LayoutParams.MATCH_PARENT,
//        ViewGroup.LayoutParams.MATCH_PARENT
//    )
    definition(viewDialog, mBuilder)
    mBuilder.show();
}

public fun AppCompatActivity.commonPopup(
    layout: Int, definition: (View, Dialog) -> Unit,
) {
    val mBuilder = Dialog(this)
    val viewDialog = this.layoutInflater.inflate(layout, null)
    mBuilder.setContentView(viewDialog);
    mBuilder.window!!.getAttributes().windowAnimations = R.style.DialogAnimation;
    mBuilder.window!!.setGravity(Gravity.CENTER)
    val metrics = DisplayMetrics()
    getWindowManager().getDefaultDisplay().getMetrics(metrics)
    val lp = viewDialog.layoutParams
    lp.width = (metrics.widthPixels * 0.9).toInt()
    viewDialog.layoutParams = lp
//    mBuilder.window!!.setLayout(
//        ViewGroup.LayoutParams.MATCH_PARENT,
//        ViewGroup.LayoutParams.MATCH_PARENT
//    )
    definition(viewDialog, mBuilder)
    mBuilder.show();
}

public fun AppCompatActivity.commonIosPopup(
    tle: String = getString(R.string.app_name),
    msg: String = "",
    btYes: String = getString(R.string.ok),
    btNo: String = "Cancel",
    isSingleBtn: Boolean = true,
    definition: (Boolean, Dialog) -> Unit,
) {
    val mBuilder = Dialog(this)
    val viewDialog = this.layoutInflater.inflate(R.layout.dailog_ios, null)
    mBuilder.setContentView(viewDialog);
    val tvTitle = viewDialog.findViewById<TextView>(R.id.tvTitle)
    val tvMessage = viewDialog.findViewById<TextView>(R.id.tvMessage)
    val btnCancel = viewDialog.findViewById<TextView>(R.id.btnCancel)
    val viewDivider = viewDialog.findViewById<View>(R.id.viewDivider)
    val btnYes = viewDialog.findViewById<TextView>(R.id.btnYes)
    tvTitle.text = tle
    tvMessage.text = msg
    btnCancel.isVisible = isSingleBtn.not()
    btnCancel.text = btNo
    viewDivider.isVisible = isSingleBtn.not()
    btnYes.text = btYes
    btnCancel.setOnClickListener {
        definition(false, mBuilder)
    }
    btnYes.setOnClickListener {
        definition(true, mBuilder)
    }
    mBuilder.window!!.getAttributes().windowAnimations = R.style.DialogAnimation;
    mBuilder.window!!.setGravity(Gravity.CENTER)
    val metrics = DisplayMetrics()
    getWindowManager().getDefaultDisplay().getMetrics(metrics)
    val lp = viewDialog.layoutParams
    lp.width = (metrics.widthPixels * 0.9).toInt()
    viewDialog.layoutParams = lp
//    mBuilder.window!!.setLayout(
//        ViewGroup.LayoutParams.MATCH_PARENT,
//        ViewGroup.LayoutParams.MATCH_PARENT
//    )
    mBuilder.show();
}

public fun Fragment.commonIosPopup(
    tle: String = getString(R.string.app_name),
    msg: String = "",
    btYes: String = getString(R.string.ok),
    btNo: String = "Cancel",
    isSingleBtn: Boolean = true,
    definition: (Boolean, Dialog) -> Unit,
) {
    val mBuilder = Dialog(this.context as AppCompatActivity)
    val viewDialog =
        (this.context as AppCompatActivity).layoutInflater.inflate(R.layout.dailog_ios, null)
    mBuilder.setContentView(viewDialog);
    val tvTitle = viewDialog.findViewById<TextView>(R.id.tvTitle)
    val tvMessage = viewDialog.findViewById<TextView>(R.id.tvMessage)
    val btnCancel = viewDialog.findViewById<TextView>(R.id.btnCancel)
    val viewDivider = viewDialog.findViewById<View>(R.id.viewDivider)
    val btnYes = viewDialog.findViewById<TextView>(R.id.btnYes)
    tvTitle.text = tle
    tvMessage.text = msg
    btnCancel.isVisible = isSingleBtn.not()
    btnCancel.text = btNo
    viewDivider.isVisible = isSingleBtn.not()
    btnYes.text = btYes
    btnCancel.setOnClickListener {
        definition(false, mBuilder)
    }
    btnYes.setOnClickListener {
        definition(true, mBuilder)
    }
    mBuilder.window!!.getAttributes().windowAnimations = R.style.DialogAnimation;
    mBuilder.window!!.setGravity(Gravity.CENTER)
    val metrics = DisplayMetrics()
    (this.context as AppCompatActivity).getWindowManager().getDefaultDisplay().getMetrics(metrics)
    val lp = viewDialog.layoutParams
    lp.width = (metrics.widthPixels * 0.9).toInt()
    viewDialog.layoutParams = lp
//    mBuilder.window!!.setLayout(
//        ViewGroup.LayoutParams.MATCH_PARENT,
//        ViewGroup.LayoutParams.MATCH_PARENT
//    )
    mBuilder.show();
}
