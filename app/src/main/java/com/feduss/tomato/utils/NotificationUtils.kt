package com.feduss.tomato.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.feduss.tomato.MainViewController
import com.feduss.tomato.enums.Consts
import java.util.concurrent.TimeUnit
import com.feduss.tomato.R
import com.feduss.tomato.enums.PrefParamName


class NotificationUtils {

    companion object {

        fun setOngoingNotification(context: Context) {

            val timerName = PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name) ?: "Error"
            val timerSecondsRemaining = PrefsUtils.getPref(context, PrefParamName.SecondsRemaining.name)?.toLong() ?: 0L

            //Save in prefs when the notification is set
            val currentMillisecondsTimestamp = System.currentTimeMillis()
            PrefsUtils.setPref(context, PrefParamName.OngoingNotificationStartTime.name, currentMillisecondsTimestamp.toString())

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val appIntent = Intent(context, MainViewController::class.java)
            appIntent.putExtra(Consts.FromOngoingNotification.value, true)
            val appPendingIntent = PendingIntent.getActivity(
                context,
                117,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val runStartTime = SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(timerSecondsRemaining)

            val channel = NotificationChannel(
                Consts.MainChannelId.value,
                Consts.MainNotificationVisibleChannel.value,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

            val notificationBuilder = NotificationCompat.Builder(
                context,
                Consts.MainChannelId.value
            )
            .setContentTitle(timerName)
            .setSmallIcon(R.drawable.ic_timer_24dp_test)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)

            val ongoingActivityStatus = Status.Builder()
                .addTemplate("#timerType#: #time#")
                .addPart("timerType", Status.TextPart(timerName))
                .addPart("time", Status.StopwatchPart(runStartTime))
                .build()

            val ongoingActivity =
                OngoingActivity.Builder(
                    context.applicationContext,
                    Consts.MainNotificationId.value.toInt(),
                    notificationBuilder
                )
                    .setStaticIcon(R.drawable.ic_timer_24dp_test)
                    .setTouchIntent(appPendingIntent)
                    .setStatus(ongoingActivityStatus)
                    .build()

            ongoingActivity.apply(context.applicationContext)

            notificationManager.notify(
                Consts.MainNotificationId.value.toInt(),
                notificationBuilder.build()
            )
        }

        fun removeOngoingNotification(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(Consts.MainNotificationId.value.toInt())
        }
    }
}