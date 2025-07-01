package com.feduss.tomato.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.utils.AlarmUtils
import com.feduss.tomatimer.utils.NotificationUtils
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.R
import com.feduss.tomato.view.notification.NotificationViewController


class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when Alarm expired in background.

        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
        if (!powerManager.isInteractive) {
            val wl = powerManager.newWakeLock(
        PowerManager.ACQUIRE_CAUSES_WAKEUP  or
                    PowerManager.ON_AFTER_RELEASE  or
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                "id:wakeupscreen"
            )
            wl.acquire(1000)
        }

        AlarmUtils.removeBackgroundAlert(context, TimerReceiver::class.java)
        PrefsUtils.setPref(context, PrefParamName.IsTimerActive.name, "false")
        NotificationUtils.removeOngoingNotification(context)

        val fullScreenIntent = Intent(context, NotificationViewController::class.java)
        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 110,
            fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val channel = NotificationChannel(
            Consts.SubChannelId.value,
            Consts.SubNotificationVisibleChannel.value,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val chipTitle = PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name)
            ?: "NoTitle"
        val currentCycle = PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)?.toInt()
            ?: -1

        val notificationBuilder =
            NotificationCompat.Builder(context, Consts.SubChannelId.value)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString( R.string.timer_expired_text, chipTitle, currentCycle + 1))
                .setContentIntent(fullScreenPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)

        val expiredTimerNotification = notificationBuilder.build()

        notificationManager.notify(Consts.SubNotificationId.value.toInt(), expiredTimerNotification)
    }
}