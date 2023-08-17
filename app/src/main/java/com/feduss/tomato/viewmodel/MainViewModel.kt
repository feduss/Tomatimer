package com.feduss.tomato.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.feduss.tomatimer.business.TimerInteractor
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class MainViewModel @Inject constructor(
    private val timerInteractor: TimerInteractor
) : ViewModel() {

    fun getChips(context: Context): List<Chip> {
        var defaultChips = listOf(
            Chip(
                shortTitle = context.getString(R.string.tomato_timer_name),
                fullTitle = context.getString(R.string.tomato_timer_name),
                value = "45",
                unit = "min",
                type = ChipType.Tomato
            ),
            Chip(
                shortTitle = context.getString(R.string.short_break_short_name),
                fullTitle = context.getString(R.string.short_break_full_name),
                value = "5",
                unit = "min",
                type = ChipType.ShortBreak
            ),
            Chip(
                shortTitle = context.getString(R.string.cycle_number_short_name),
                fullTitle = context.getString(R.string.cycle_number_full_name),

                value = "4",
                type = ChipType.CyclesNumber
            ),
            Chip(
                shortTitle = context.getString(R.string.long_break_short_name),
                fullTitle = context.getString(R.string.long_break_full_name),
                value = "15",
                unit = "min",
                type = ChipType.LongBreak
            )
        )

        for(chip in defaultChips) {
            val loadedValue = PrefsUtils.getPref(context, chip.type.valuePrefKey)
            if (loadedValue.isNullOrEmpty()) {
                PrefsUtils.setPref(context, chip.type.valuePrefKey, chip.value)
            } else {
                chip.value = loadedValue
            }
        }

        return defaultChips
    }

    private fun getCurrentCycleFromPrefs(context: Context): Int {
        return timerInteractor.getCurrentCycleFromPrefs(context)
    }

    private fun getCurrentChipTypeFromPrefs(context: Context): String {
        return timerInteractor.getCurrentChipTypeFromPrefs(context)
    }

    fun setNextTimerInPrefs(context: Context) {
        val stringChipType = getCurrentChipTypeFromPrefs(context)
        val chipType = ChipType.fromString(stringChipType)
        val currentCycle = getCurrentCycleFromPrefs(context)

        timerInteractor.setNextTimerInPrefs(
            context,
            chipType,
            currentCycle
        )
    }

    fun cancelTimerInPrefs(context: Context) {
        timerInteractor.cancelTimerInPrefs(context)
    }
}