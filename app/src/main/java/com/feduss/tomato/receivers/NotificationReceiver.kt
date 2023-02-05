package com.feduss.tomato.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.feduss.tomato.MainActivityViewController
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.utils.NotificationUtils
import com.feduss.tomato.utils.PrefsUtils

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.e("TOMATO:", "Notification receiver: action --> ${intent.getStringExtra("action")}")
        when (intent.getStringExtra("action")) {
            //If the user has tapped "next timer" in the notification,
            //go to the next timer, if needed
            "open" -> {
                val currentChipType = intent.getStringExtra("currentChipType")
                val currentCycle = intent.getIntExtra("currentCycle", 0)
                val chipType = ChipType.fromString(currentChipType)

                PrefsUtils.setNextTimer(context, chipType, currentCycle)

                val appIntent = Intent(context, MainActivityViewController::class.java)
                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(appIntent)

                Log.e("TOMATO:", "Open app and go to next timer from notification")
            }
            "cancel" -> {
                PrefsUtils.setPref(context, PrefParamName.CurrentTimerIndex.name, null)
                PrefsUtils.setPref(context, PrefParamName.CurrentCycle.name, null)
                PrefsUtils.setPref(context, PrefParamName.SecondsRemaining.name, null)
                NotificationUtils.removeNotification(context)

                Log.e("TOMATO:", "Cancel timers queue from notification")
            }
        }
    }
}