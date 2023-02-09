package com.feduss.tomato.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import com.feduss.tomato.enums.Consts
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.receivers.TimerReceiver

class AlarmUtils {

    companion object {

        fun setBackgroundAlert(context: Context) {
            removeBackgroundAlert(context)

            val secondsRemaining = PrefsUtils.getPref(context, PrefParamName.SecondsRemaining.name)?.toLong() ?: 0L
            val millisSince1970 = System.currentTimeMillis()

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val broadcastReceiverIntent = Intent(context, TimerReceiver::class.java)
            broadcastReceiverIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)

            //Intent called when the timer ended
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Consts.AlarmEnd.value.toInt(),
                broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmTime = (secondsRemaining * 1000L) + millisSince1970
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                alarmTime,
//                pendingIntent
//            )

            val alarmClockInfo = AlarmManager.AlarmClockInfo(alarmTime, null)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

            PrefsUtils.setPref(context, PrefParamName.AlarmSetTime.name, (alarmTime/1000).toString())
        }

        fun removeBackgroundAlert(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val broadcastReceiverIntent = Intent(context, TimerReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Consts.AlarmEnd.value.toInt(),
                broadcastReceiverIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            PrefsUtils.setPref(context, PrefParamName.AlarmSetTime.name, null)
        }

        fun vibrate(context: Context) {
            val vibrationPattern = longArrayOf(0, 500, 50, 300)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorService =
                    context.getSystemService(AppCompatActivity.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorService.defaultVibrator.vibrate(
                    VibrationEffect.createWaveform(
                        vibrationPattern,
                        -1
                    )
                )
            } else {
                val vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
                }
            }
        }
    }
}