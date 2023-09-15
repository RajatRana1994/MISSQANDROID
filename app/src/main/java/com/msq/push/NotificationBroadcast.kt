package com.msq.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class NotificationBroadcast: BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action=="ACTION_NOTIFY_PAYMENT"){
            Intent("ACTION_LOCAL_NOTIFY_PAYMENT").apply {
                putExtras(p1!!)
                LocalBroadcastManager.getInstance(p0!!).sendBroadcast(this)
            }
        }else if (p1?.action=="ACTION_NOTIFY_POPUP"){
            Intent("ACTION_LOCAL_NOTIFY").apply {
                putExtras(p1)
                LocalBroadcastManager.getInstance(p0!!).sendBroadcast(this)
            }
        }
    }
}