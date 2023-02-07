package com.feduss.tomato.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.feduss.tomato.views.notification.NotificationViewController
import com.feduss.tomato.utils.AlarmUtils

class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when Alarm expired in background.

        Log.e("TOMATO:", "Timer receiver")

        AlarmUtils.removeBackgroundAlert(context)

        val notificationIntent = Intent(context, NotificationViewController::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(notificationIntent)

    }
}