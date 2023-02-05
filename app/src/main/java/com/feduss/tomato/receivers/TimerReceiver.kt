package com.feduss.tomato.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.feduss.tomato.NotificationActivity
import com.feduss.tomato.R
import com.feduss.tomato.enums.Consts
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.utils.AlarmUtils
import com.feduss.tomato.utils.PrefsUtils


class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when Alarm expired in background.

        Log.e("TOMATO:", "Timer receiver")

        AlarmUtils.removeBackgroundAlert(context)

        val notificationIntent = Intent(context, NotificationActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(notificationIntent)

    }
}