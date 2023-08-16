package com.feduss.tomato.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.feduss.tomato.R
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.utils.AlarmUtils
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.view.notification.NotificationViewController

class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when Alarm expired in background.

        AlarmUtils.removeBackgroundAlert(context, TimerReceiver::class.java)

        val fullScreenIntent = Intent(context, NotificationViewController::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextTimerIntent = Intent(context, NotificationViewController::class.java)
        nextTimerIntent.putExtra(
            Consts.NotificationActionIntentExtra.value,
            Consts.NotificationActionIntentExtraNextTimer.value
        )
        val nextTimerPendingIntent = PendingIntent.getActivity(context, 0,
            nextTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val cancelQueueIntent = Intent(context, NotificationViewController::class.java)
        cancelQueueIntent.putExtra(
            Consts.NotificationActionIntentExtra.value,
            Consts.NotificationActionIntentExtraCancelQueue.value
        )
        val cancelQueuePendingIntent = PendingIntent.getActivity(context, 0,
            cancelQueueIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            Consts.SubChannelId.value,
            Consts.SubNotificationVisibleChannel.value,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val chipTitle = PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name) ?: "NoTitle"
        val currentCycle = PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)?.toInt() ?: -1

        // Screen ok -> heads up notification with two actions and relative pending intens
        // Screen off -> fullScreenPendingIntent
        val notificationBuilder =
            NotificationCompat.Builder(context, Consts.SubChannelId.value)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString( R.string.timer_expired_text, chipTitle, currentCycle + 1))
                .addAction(0, context.getString(R.string.notification_next_timer_action), nextTimerPendingIntent)
                .addAction(1, context.getString(R.string.notification_delete_timer_queue_action), cancelQueuePendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)

        val expiredTimerNotification = notificationBuilder.build()

        notificationManager.notify(Consts.SubNotificationId.value.toInt(), expiredTimerNotification)
    }
}