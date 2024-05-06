package com.feduss.tomatimer.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomatimer.entity.enums.PrefParamName
import java.util.concurrent.TimeUnit


class NotificationUtils {

    companion object {

        fun setOngoingNotification(context: Context, iconId: Int) {

            val timerName = PrefsUtils.getPref(
                context,
                PrefParamName.CurrentTimerName.name
            ) ?: "Error"

            val timerSecondsRemaining = PrefsUtils.getPref(
                context,
                PrefParamName.SecondsRemaining.name
            )?.toLong() ?: 0L

            //Save in prefs when the notification is set
            val currentMillisecondsTimestamp = System.currentTimeMillis()
            PrefsUtils.setPref(
                context,
                PrefParamName.OngoingNotificationStartTime.name,
                currentMillisecondsTimestamp.toString()
            )

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val appIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            appIntent?.putExtra(Consts.FromOngoingNotification.value, true)
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
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)

            val notificationBuilder = NotificationCompat.Builder(
                context,
                Consts.MainChannelId.value
            )
            .setContentTitle(timerName)
            .setSmallIcon(iconId)
            //.setColor(Color.Red.toArgb())
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
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
                .setStaticIcon(iconId)
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

        fun restoreTimerSecondsFromOngoingNotification(context: Context) {
            val ongoingNotificationStartTime = PrefsUtils.getPref(
                context,
                PrefParamName.OngoingNotificationStartTime.name
            )?.toLong() ?: 0L
            val timerSecondsRemaining = PrefsUtils.getPref(
                context,
                PrefParamName.SecondsRemaining.name
            )?.toLong() ?: 0L

            val timerSecondsEndTime = (ongoingNotificationStartTime / 1000L) + timerSecondsRemaining
            val currentMillisecondsTimestamp = System.currentTimeMillis()

            var newTimerSecondsRemaining = timerSecondsEndTime - (currentMillisecondsTimestamp / 1000)

            //Corner case?
            if(newTimerSecondsRemaining < 0) {
                newTimerSecondsRemaining = 0
            }

            PrefsUtils.setPref(
                context,
                PrefParamName.SecondsRemaining.name,
                newTimerSecondsRemaining.toString()
            )
        }
    }
}