package com.feduss.pomodoro.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.feduss.pomodoro.enums.Consts
import com.feduss.pomodoro.enums.PrefParamName
import com.feduss.pomodoro.receivers.TimerReceiver

class AlarmUtils {

    companion object {

        fun setBackgroundAlert(context: Context, currentChipIndex: Int, currentCycle: Int, timerMillisRemaining: Long, millisSince1970: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val broadcastReceiverIntent = Intent(context, TimerReceiver::class.java)

            //Intent called when the timer started
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Consts.AlarmEnd.value.toInt(),
                broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmTime = timerMillisRemaining + millisSince1970
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                pendingIntent
            )

            PrefsUtils.setPref(context, PrefParamName.CurrentChip.name, currentChipIndex.toString())
            PrefsUtils.setPref(context, PrefParamName.CurrentCycle.name, currentCycle.toString())
            PrefsUtils.setPref(context, PrefParamName.AlarmSetTime.name, (alarmTime/1000).toString())
        }

        fun removeBackgroundAlert(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val broadcastReceiverIntent = Intent(context, TimerReceiver::class.java)

            //Intent called when the timer expired
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                broadcastReceiverIntent,
                0
            )
            alarmManager.cancel(pendingIntent)
            PrefsUtils.setPref(context, PrefParamName.CurrentChip.name, null)
            PrefsUtils.setPref(context, PrefParamName.CurrentCycle.name, null)
            PrefsUtils.setPref(context, PrefParamName.AlarmSetTime.name, null)
        }
    }
}