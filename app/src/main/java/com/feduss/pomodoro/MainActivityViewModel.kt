package com.feduss.pomodoro

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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
            val loadedValue = getPrefOfChip(activity, chip.type.valuePrefKey)
            if (!loadedValue.isNullOrEmpty()) {
                chip.value = loadedValue
            }
        }
        return chips
    }

    private fun getSharedPreferences(activity: Activity): SharedPreferences {
        return activity.getPreferences(Context.MODE_PRIVATE)
    }

    fun getPrefOfChip(activity: Activity, pref: String): String? {
        return getSharedPreferences(activity).getString(pref, "")
    }

    fun userHasUpdatedPrefOfChip(activity: Activity, pref: String, newValue: String?) {
        getSharedPreferences(activity).edit().putString(pref, newValue).apply()
    }
}