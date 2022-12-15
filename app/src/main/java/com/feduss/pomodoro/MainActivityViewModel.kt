package com.feduss.pomodoro

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.feduss.pomodoro.enums.ChipType
import com.feduss.pomodoro.models.Chip
import com.feduss.pomodoro.utils.PrefsUtils

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var chips = listOf(
        Chip("Pomodoro", "45", "min", ChipType.Tomato),
        Chip("P. breve", "5", "min", ChipType.ShortBreak),
        Chip("Num. cicli", "4", type = ChipType.CyclesNumber),
        Chip("P. lunga", "15", "min", ChipType.LongBreak)
    )


    fun getData(activity: Activity): List<Chip> {
        for(chip in chips) {
            val loadedValue = PrefsUtils.getPref(activity, chip.type.valuePrefKey)
            if (!loadedValue.isNullOrEmpty()) {
                chip.value = loadedValue
            }
        }
        return chips
    }
}