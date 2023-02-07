package com.feduss.tomato.views.setup

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.models.Chip
import com.feduss.tomato.utils.PrefsUtils
import java.util.*

class SetupViewModel(chips: List<Chip>) : ViewModel() {

    val chips = MutableLiveData(chips)
    var lastSelectedChipIndex: Int? = null

    fun updateLastSelectedChip(newValue: String) {
        lastSelectedChipIndex?.let { index ->
            val tempChips = chips.value
            tempChips?.get(index)?.value = newValue
            chips.postValue(tempChips)
        }
    }

    fun getSecondsFromAlarmTime(context: Context): String {
        val alarmSetTime = PrefsUtils.getPref(context, PrefParamName.AlarmSetTime.name)?.toLong() ?: 0L
        val nowSeconds = Calendar.getInstance().timeInMillis / 1000L

        return (alarmSetTime - nowSeconds).toString()
    }

    fun cancelTimer(context: Context) {
        PrefsUtils.cancelTimer(context)
    }

    fun getChipIndexFromPref(context: Context): String? {
        return PrefsUtils.getPref(context, PrefParamName.CurrentTimerIndex.name)
    }

    fun getCycleIndexFromPref(context: Context): String? {
        return PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)
    }

    fun getSecondsRemainingFromPref(context: Context): String? {
        return PrefsUtils.getPref(context, PrefParamName.SecondsRemaining.name)
    }

    fun userHasSelectedChip(index: Int) {
        lastSelectedChipIndex = index
    }
}