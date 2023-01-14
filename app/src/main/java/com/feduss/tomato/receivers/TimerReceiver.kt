package com.feduss.tomato.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.feduss.tomato.MainActivityViewController
import com.feduss.tomato.R
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.enums.Consts
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.utils.PrefsUtils

class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when Alarm expired in background.

        val chipTitle = PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name) ?: "NoTitle"
        val currentCycle = PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)?.toInt() ?: -1
        val currentChipType = PrefsUtils.getPref(context, PrefParamName.CurrentChipType.name) ?: "NoType"

        //The timer is gone, let's vibrate!
        val vibrationPattern = longArrayOf(0, 500, 50, 300)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorService = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorService.defaultVibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
        } else {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
            }
        }

        val fullScreenIntent = Intent(context, MainActivityViewController::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
            fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder =
            NotificationCompat.Builder(context, Consts.MainChannelId.value)
            .setSmallIcon(R.drawable.ic_timer_24dp_test)
            .setContentTitle("Timer $chipTitle (ciclo ${currentCycle + 1}) scaduto!")
            .addAction(R.drawable.ic_arrow_right_24, "Apri app", fullScreenPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            Consts.SubChannelId.value,
            Consts.SubNotificationVisibleChannel.value,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(
            Consts.SubNotificationId.value.toInt(),
            notificationBuilder.build()
        )

        //Go to the next timer, if needed
        val chipType = ChipType.fromString(currentChipType)

        chipType?.let { chipTypeNotNull ->
            val totalCycles: Int = PrefsUtils.getPref(context, ChipType.CyclesNumber.valuePrefKey)?.toInt() ?: -1

            when (chipType) {
                ChipType.Tomato -> {
                    if (currentCycle == totalCycles - 1) {
                        PrefsUtils.setPref(context, PrefParamName.CurrentTimerIndex.name, ChipType.LongBreak.tag.toString())
                    } else {
                        PrefsUtils.setPref(context, PrefParamName.CurrentTimerIndex.name, ChipType.ShortBreak.tag.toString())
                    }
                }
                ChipType.ShortBreak -> {
                    PrefsUtils.setPref(context, PrefParamName.CurrentTimerIndex.name, ChipType.Tomato.tag.toString())
                    PrefsUtils.setPref(context, PrefParamName.CurrentCycle.name, (currentCycle + 1).toString())
                }
                ChipType.LongBreak -> {
                    PrefsUtils.setPref(context, PrefParamName.CurrentTimerName.name, null)
                    PrefsUtils.setPref(context, PrefParamName.CurrentCycle.name, null)
                    PrefsUtils.setPref(context, PrefParamName.SecondsRemaining.name, null)
                }
                else -> {}
            }

            //TODO: è necessario?
            PrefsUtils.setPref(context, PrefParamName.SecondsRemaining.name, null)
        }
        }
}