package com.feduss.tomato.views.timer

import android.content.Context
import androidx.lifecycle.ViewModel
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.models.Chip
import com.feduss.tomato.utils.PrefsUtils

class TimerViewModel(val chips: List<Chip>, val initialChipIndex: Int = 0,
                     val initialCycle: Int = 0, val initialTimerSeconds: Int = 0) : ViewModel() {

    val totalCycles = chips[2].value.toInt()

    fun saveCurrentTimerData(context: Context, chipType: ChipType, currentTimerName: String?, currentCycle: Int?, secondsRemaining: Int?) {

        //Save the current chip type
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.CurrentChipType.name,
            newValue = chipType.stringValue
        )

        //Save the current chip index
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.CurrentTimerIndex.name,
            newValue = chipType.tag.toString()
        )

        //Save the current timer title
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.CurrentTimerName.name,
            newValue = currentTimerName
        )

        //Save the current cycle
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.CurrentCycle.name,
            newValue = currentCycle?.toString()
        )

        //Save the timer seconds remaining
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.SecondsRemaining.name,
            newValue = secondsRemaining?.toString()
        )
    }

    fun loadTimerSecondsRemainings(context: Context): Int {
        return PrefsUtils.getPref(context, PrefParamName.SecondsRemaining.name)?.toInt() ?: 0
    }

    fun setTimerState(context: Context, isTimerActive: Boolean) {
        val stringValue = if (isTimerActive) "true" else "false"
        PrefsUtils.setPref(context, PrefParamName.IsTimerActive.name, stringValue)
    }

    fun cancelTimer(context: Context) {
        PrefsUtils.cancelTimer(context)
    }


}