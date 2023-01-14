package com.feduss.tomato.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

            //Intent called when the timer ended
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Consts.AlarmEnd.value.toInt(),
                broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )


            val alarmTime = (secondsRemaining * 1000L) + millisSince1970
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                pendingIntent
            )

            //PrefsUtils.setPref(context, PrefParamName.CurrentTimerIndex.name, currentChipIndex.toString())
            //PrefsUtils.setPref(context, PrefParamName.CurrentCycle.name, currentCycle.toString())
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
    }
}