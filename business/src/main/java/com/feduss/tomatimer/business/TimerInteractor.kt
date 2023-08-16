package com.feduss.tomatimer.business

import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.utils.PrefsUtils
import android.content.Context
import javax.inject.Inject

interface TimerInteractor {
    fun getChipTitleFromPrefs(context: Context): String

    fun getCurrentCycleFromPrefs(context: Context): Int

    fun getCurrentChipTypeFromPrefs(context: Context): String

    fun cancelTimerInPrefs(context: Context)

    fun setNextTimerInPrefs(context: Context, chipType: ChipType?,
                            currentCycle: Int
    )
}

class TimerInteractorImpl @Inject constructor(): TimerInteractor {

    override fun getChipTitleFromPrefs(context: Context): String {
        return PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name) ?: "NoTitle"
    }

    override fun getCurrentCycleFromPrefs(context: Context): Int {
        return PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)?.toInt() ?: -1
    }

    override fun getCurrentChipTypeFromPrefs(context: Context): String {
        return PrefsUtils.getPref(context, PrefParamName.CurrentChipType.name) ?: "NoType"
    }

    override fun cancelTimerInPrefs(context: Context) {
        PrefsUtils.cancelTimer(context)
    }

    override fun setNextTimerInPrefs(context: Context, chipType: ChipType?,
                            currentCycle: Int
    ) {
        PrefsUtils.setNextTimer(context, chipType, currentCycle)
    }
}