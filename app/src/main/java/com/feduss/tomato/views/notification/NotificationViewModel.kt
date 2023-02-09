package com.feduss.tomato.views.notification

import android.content.Context
import androidx.lifecycle.ViewModel
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.utils.PrefsUtils

class NotificationViewModel: ViewModel() {

    fun getChipTitleFromPrefs(context: Context): String {
        return PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name) ?: "NoTitle"
    }

    fun getCurrentCycleFromPrefs(context: Context): Int {
        return PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)?.toInt() ?: -1
    }

    fun getCurrentChipTypeFromPrefs(context: Context): String {
        return PrefsUtils.getPref(context, PrefParamName.CurrentChipType.name) ?: "NoType"
    }

    fun cancelTimerInPrefs(context: NotificationViewController) {
        PrefsUtils.cancelTimer(context)
    }

    fun setNextTimerInPrefs(context: NotificationViewController, chipType: ChipType?,
                            currentCycle: Int
    ) {
        PrefsUtils.setNextTimer(context, chipType, currentCycle)
    }
}