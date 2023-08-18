package com.feduss.tomato.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.utils.AlarmUtils
import com.feduss.tomatimer.utils.NotificationUtils
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.R
import com.feduss.tomato.view.MainViewController
import com.feduss.tomato.view.notification.NotificationViewController

class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when Alarm expired in background.

        AlarmUtils.removeBackgroundAlert(context, TimerReceiver::class.java)

        val notificationIntent = Intent(context, NotificationViewController::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(notificationIntent)

        NotificationUtils.removeOngoingNotification(context)

    }
}