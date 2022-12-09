package com.feduss.pomodoro

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var chips = listOf(
        Chip("Pomodoro", "45", "min", ChipType.Tomato),
        Chip("P. breve", "5", "min", ChipType.ShortBreak),
        Chip("Num. cicli", "4", type = ChipType.CyclesNumber),
        Chip("P. lunga", "15", "min", ChipType.LongBreak)
    )


    fun getData(activity: Activity): List<Chip> {
        for(chip in chips) {
            val loadedValue = getValueOfChip(activity, chip.type)
            if (!loadedValue.isNullOrEmpty()) {
                chip.value = loadedValue
            }
        }
        return chips
    }

    private fun getSharedPreferences(activity: Activity): SharedPreferences {
        return activity.getPreferences(Context.MODE_PRIVATE)
    }

    fun getValueOfChip(activity: Activity, type: ChipType): String? {
        return when (type) {
            ChipType.Tomato -> getSharedPreferences(activity).getString(ChipType.Tomato.prefKey, "")
            ChipType.ShortBreak -> getSharedPreferences(activity).getString(ChipType.ShortBreak.prefKey, "")
            ChipType.CyclesNumber -> getSharedPreferences(activity).getString(ChipType.CyclesNumber.prefKey, "")
            ChipType.LongBreak -> getSharedPreferences(activity).getString(ChipType.LongBreak.prefKey, "")
            else -> null
        }
    }

    fun userHasUpdatedSettings(activity: Activity, type: ChipType, newValue: String) {
        when (type) {
            ChipType.Tomato -> getSharedPreferences(activity).edit().putString(ChipType.Tomato.prefKey, newValue).apply()
            ChipType.ShortBreak -> getSharedPreferences(activity).edit().putString(ChipType.ShortBreak.prefKey, newValue).apply()
            ChipType.CyclesNumber -> getSharedPreferences(activity).edit().putString(ChipType.CyclesNumber.prefKey, newValue).apply()
            ChipType.LongBreak -> getSharedPreferences(activity).edit().putString(ChipType.LongBreak.prefKey, newValue).apply()
            else -> {}
        }
    }
}