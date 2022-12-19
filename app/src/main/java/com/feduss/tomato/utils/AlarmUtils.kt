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

        fun setBackgroundAlert(context: Context, chipTitle: String, currentChipIndex: Int, currentCycle: Int, timerMillisRemaining: Long, millisSince1970: Long) {
            removeBackgroundAlert(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val broadcastReceiverIntent = Intent(context, TimerReceiver::class.java)
            broadcastReceiverIntent.putExtra(Consts.TimerTitle.value, chipTitle)

            //Intent called when the timer ended
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                Consts.AlarmEnd.value.toInt(),
                broadcastReceiverIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val alarmTime = timerMillisRemaining + millisSince1970
            alarmManager.setExactAndAllowWhileIdle(
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