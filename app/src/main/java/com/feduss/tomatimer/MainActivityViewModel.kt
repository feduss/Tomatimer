package com.feduss.tomatimer

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.feduss.tomatimer.enums.ChipType
import com.feduss.tomatimer.models.Chip
import com.feduss.tomatimer.utils.PrefsUtils

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var chips = listOf(
        Chip("Pomodoro", "45", "min", ChipType.Tomato),
        Chip("P. breve", "5", "min", ChipType.ShortBreak),
        Chip("Num. cicli", "4", type = ChipType.CyclesNumber),
        Chip("P. lunga", "15", "min", ChipType.LongBreak)
    )


    fun getData(context: Context): List<Chip> {
        for(chip in chips) {
            val loadedValue = PrefsUtils.getPref(context, chip.type.valuePrefKey)
            if (!loadedValue.isNullOrEmpty()) {
                chip.value = loadedValue
            }
        }
        return chips
    }
}