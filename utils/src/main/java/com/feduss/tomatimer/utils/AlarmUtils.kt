package com.feduss.tomatimer.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import com.feduss.tomatimer.entity.enums.PrefParamName

class AlarmUtils {

    companion object {

        fun<T> setBackgroundAlert(context: Context, timerReceiverClass: Class<T>) {
            removeBackgroundAlert(context, timerReceiverClass)

            val isTimerActive = PrefsUtils.getPref(
                context,
                PrefParamName.IsTimerActive.name
            )?.toBoolean() ?: false

            if (!isTimerActive) return;

            val timerSecondsRemaining = PrefsUtils.getPref(
                context,
                PrefParamName.SecondsRemaining.name
            )?.toLong() ?: 0L
            val currentMillisecondsTimestamp = System.currentTimeMillis()

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val broadcastReceiverIntent = Intent(context, timerReceiverClass)
            broadcastReceiverIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)

            //Intent called when the timer ended
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                com.feduss.tomatimer.entity.enums.Consts.AlarmEnd.value.toInt(),
                broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmTime = (timerSecondsRemaining * 1000L) + currentMillisecondsTimestamp

            val alarmClockInfo = AlarmManager.AlarmClockInfo(alarmTime, null)
            if (canScheduleExactAlarms(alarmManager)) {
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

                PrefsUtils.setPref(
                    context,
                    PrefParamName.AlarmSetTime.name,
                    (alarmTime / 1000).toString()
                )
            }
        }

        fun<T> removeBackgroundAlert(context: Context, timerReceiverClass: Class<T>) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val broadcastReceiverIntent = Intent(context, timerReceiverClass)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                com.feduss.tomatimer.entity.enums.Consts.AlarmEnd.value.toInt(),
                broadcastReceiverIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            PrefsUtils.setPref(
                context,
                PrefParamName.AlarmSetTime.name,
                null
            )
        }

        @SuppressLint("MissingPermission")
        fun vibrate(context: Context) {
            val vibrationPattern = longArrayOf(500, 50, 500, 50, 0, 0 ,0, 500, 50, 500, 50)
            val vibrator: Vibrator = getVibrator(context)
            Log.e("LogTest: ", "has vibration? ${vibrator.hasVibrator()}")
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        vibrationPattern,
                        -1
                    )
                )
            }
        }

        private fun getVibrator(context: Context): Vibrator {
            val vibrator: Vibrator =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorService =
                        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorService.defaultVibrator
                } else {
                    context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
                }
            return vibrator
        }

        fun sound(context: Context) {
            val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mp: MediaPlayer? = MediaPlayer.create(context, alarmSound)

            if (mp != null) {
                mp.start()
                Handler(Looper.getMainLooper()).postDelayed({
                    mp.release()
                }, 5000)
            }
        }
    }
}