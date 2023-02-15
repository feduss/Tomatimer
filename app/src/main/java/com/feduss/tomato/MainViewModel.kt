package com.feduss.tomato

import android.content.Context
import androidx.lifecycle.ViewModel
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.models.Chip
import com.feduss.tomato.utils.PrefsUtils

open class MainViewModel : ViewModel() {
    private var chips = listOf(
        Chip("Pomodoro", "Pomodoro","45", "min", ChipType.Tomato),
        Chip("P. breve", "Pausa breve", "5", "min", ChipType.ShortBreak),
        Chip("Num. cicli", "Numero cicli","4", type = ChipType.CyclesNumber),
        Chip("P. lunga", "Pausa lunga","15", "min", ChipType.LongBreak)
    )

    fun loadDataFromPrefs(context: Context): List<Chip> {
        for(chip in chips) {
            val loadedValue = PrefsUtils.getPref(context, chip.type.valuePrefKey)
            if (loadedValue.isNullOrEmpty()) {
                PrefsUtils.setPref(context, chip.type.valuePrefKey, chip.value)
            } else {
                chip.value = loadedValue
            }
        }
        return chips
    }
}