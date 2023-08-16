package com.feduss.tomato.viewmodel.setup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feduss.tomatimer.entity.UIText
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.R
import com.feduss.tomato.viewmodel.edit.EditViewModel
import com.feduss.tomato.viewmodel.timer.TimerViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.*
import javax.inject.Inject

class SetupViewModel @AssistedInject constructor(
    @Assisted("chips") chips: List<Chip>
) : ViewModel() {

    //Factory
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("chips") chips: List<Chip>,
        ): SetupViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            chips: List<Chip>,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(
                    chips
                ) as T
            }
        }
    }

    var chips: List<Chip> = chips
    private var lastSelectedChipIndex: Int? = null

    fun updateLastSelectedChip(newValue: String) {
        lastSelectedChipIndex?.let { index ->
            val tempChips = chips
            tempChips[index].value = newValue
            chips = tempChips
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