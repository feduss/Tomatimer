package com.feduss.tomatimer.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.feduss.tomatimer.R
import com.feduss.tomatimer.enums.Consts
import com.feduss.tomatimer.enums.PrefParamName
import com.feduss.tomatimer.utils.PrefsUtils


class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when Alarm expired in background.

        val chipTitle = PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name) ?: "NoTitle"
        val currentCycle = PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)?.toInt() ?: -1
        val currentChipType = PrefsUtils.getPref(context, PrefParamName.CurrentChipType.name) ?: "NoType"

        val vibrationPattern = longArrayOf(0, 500, 50, 300)

        //when the user clicks the next timer action in the fullscreen notification,
        //open the main activity, setting the next timer, if needed
        val nextTimerIntent = Intent(context, NotificationReceiver::class.java)
        addNextTimerExtras(nextTimerIntent, currentChipType, currentCycle)
        val nextTimerPendingIntent = PendingIntent.getBroadcast(context, 232,
            nextTimerIntent, PendingIntent.FLAG_IMMUTABLE)

        //when the user cancels the notification,
        //close the notification and clear timers datas
        val cancelTimerIntent = Intent(context, NotificationReceiver::class.java)
        addCancelTimerExtras(cancelTimerIntent)
        val cancelTimerPendingIntent = PendingIntent.getBroadcast(context, 232,
            cancelTimerIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder =
            NotificationCompat.Builder(context, Consts.MainChannelId.value)
                .setSmallIcon(R.drawable.ic_timer_24dp_test)
                .setContentTitle("Timer $chipTitle (ciclo ${currentCycle + 1}) scaduto!")
                .addAction(R.drawable.ic_arrow_right_24, "Prossimo timer", nextTimerPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVibrate(vibrationPattern)
                .setDeleteIntent(cancelTimerPendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            Consts.SubChannelId.value,
            Consts.SubNotificationVisibleChannel.value,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(
            Consts.SubNotificationId.value.toInt(),
            notificationBuilder.build()
        )

        Log.e("TOMATO:", "Timer receiver")
    }

    private fun addCancelTimerExtras(cancelTimerIntent: Intent) {
        cancelTimerIntent.putExtra("action", "cancel")
    }

    private fun addNextTimerExtras(
        nextTimerIntent: Intent,
        currentChipType: String,
        currentCycle: Int
    ) {
        nextTimerIntent.putExtra("action", "open")
        nextTimerIntent.putExtra("currentChipType", currentChipType)
        nextTimerIntent.putExtra("currentCycle", currentCycle)
    }
}