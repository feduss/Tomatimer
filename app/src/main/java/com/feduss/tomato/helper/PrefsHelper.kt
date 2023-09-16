package com.feduss.tomato.helper

import android.content.Context
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.utils.PrefsUtils

class PrefsHelper {

    companion object {
        fun getChipTitleFromPrefs(context: Context): String {
            return PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name) ?: "NoTitle"
        }

        fun getCurrentCycleFromPrefs(context: Context): Int {
            return PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)?.toInt() ?: -1
        }

        fun getCurrentChipTypeFromPrefs(context: Context): String {
            return PrefsUtils.getPref(context, PrefParamName.CurrentChipType.name) ?: "NoType"
        }

        fun cancelTimerInPrefs(context: Context) {
            PrefsUtils.cancelTimer(context)
        }

        fun setNextTimerInPrefs(context: Context, chipType: ChipType?,
                                currentCycle: Int
        ) {
            PrefsUtils.setNextTimer(context, chipType, currentCycle)
        }
    }
}