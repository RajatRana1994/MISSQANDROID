package com.msq.util.ext

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun String.getFormatTime(from: String, to: String): String {
    var sdf = SimpleDateFormat(from, Locale.ENGLISH);
    val date = sdf.parse(this)
    sdf = SimpleDateFormat(to, Locale.ENGLISH);
    try {
        return sdf.format(date);
    } catch (e1: ParseException) {
        e1.printStackTrace();
    }
    return ""
}

fun Long.getFormatTime(to: String): String {
    val sdf = SimpleDateFormat(to, Locale.ENGLISH);
//    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.timeInMillis = this
    try {
        return sdf.format(cal.time);
    } catch (e1: ParseException) {
        e1.printStackTrace();
    }
    return ""
}
fun String.getFomatDate(from: String,to: String): String {
    var sdf = SimpleDateFormat(from, Locale.ENGLISH);
    val cal = Calendar.getInstance(Locale.getDefault())
    val currentcal = Calendar.getInstance(Locale.getDefault())
    cal.time = sdf.parse(this)!!
    sdf = SimpleDateFormat(to, Locale.ENGLISH);
    try {
        if (currentcal.get(Calendar.DATE)-cal.get(Calendar.DATE)==0) return "Today"
        else if (currentcal.get(Calendar.DATE)-cal.get(Calendar.DATE)==1) return "Yesterday"
        else return sdf.format(cal.time);
    } catch (e1: ParseException) {
        e1.printStackTrace();
    }
    return ""
}
fun String.getTimeInMillis(from: String): Long {
    val sdf = SimpleDateFormat(from, Locale.ENGLISH);
    val cal = Calendar.getInstance(Locale.getDefault())
    val currentcal = Calendar.getInstance(Locale.getDefault())
    cal.time = sdf.parse(this)!!
    try {
       return cal.timeInMillis
    } catch (e1: ParseException) {
        e1.printStackTrace();
    }
    return cal.timeInMillis
}

fun Long.getFomatDayName(to: String): String {
    val sdf = SimpleDateFormat(to, Locale.ENGLISH);
    val cal = Calendar.getInstance(Locale.getDefault())
    val currentcal = Calendar.getInstance(Locale.getDefault())
    cal.timeInMillis = this
    try {
        return if (currentcal.timeInMillis.getFormatTime("dd").toInt()==cal.timeInMillis.getFormatTime("dd").toInt()) "Today"
        else if (currentcal.timeInMillis.getFormatTime("dd").toInt()-cal.timeInMillis.getFormatTime("dd").toInt()==1) "Yesterday" else sdf.format(cal.time);
    } catch (e1: ParseException) {
        e1.printStackTrace();
    }
    return ""
}


fun Double.ferToCalsius(currentFormat: String): Int {
    if (currentFormat.equals("F")) {
        return this.toInt()
    } else {
        return ((this - 32.0) * 5 / 9).toInt()
    }
}



fun String.format24(): String {
    var sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.time=sdf.parse(this)!!
    sdf = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    return sdf.format(cal.time)
}


fun String.format12(format:String="HH:mm:ss"): String {
    var sdf = SimpleDateFormat(format, Locale.ENGLISH);
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.time=sdf.parse(this)!!
    sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    return sdf.format(cal.time)
}
fun String.getDateObj(format:String="HH:mm:ss"): Date {
    var sdf = SimpleDateFormat(format, Locale.ENGLISH);
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.time=sdf.parse(this)!!
    return cal.time
}

fun Double.oneDecimal() = String.format("%.1f",this)
fun Double.twoDecimal() = String.format("%.2f",this)