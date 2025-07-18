package com.feduss.tomato.viewmodel.timer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.helper.PrefsHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TimerViewModel @AssistedInject constructor(
    @Assisted("chips") val chips: List<Chip>,
    @Assisted("initialChipIndex") val initialChipIndex: Int = 0,
    @Assisted("initialCycle") val initialCycle: Int = 0,
    @Assisted("initialTimerSeconds") val initialTimerSeconds: Int = 0
) : ViewModel() {

    //Factory
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("chips") chips: List<Chip>,
            @Assisted("initialChipIndex") initialChipIndex: Int,
            @Assisted("initialCycle") initialCycle: Int,
            @Assisted("initialTimerSeconds") initialTimerSeconds: Int
        ): TimerViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            chips: List<Chip>,
            initialChipIndex: Int,
            initialCycle: Int,
            initialTimerSeconds: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(
                    chips,
                    initialChipIndex,
                    initialCycle,
                    initialTimerSeconds
                ) as T
            }
        }
    }

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

    fun setNextTimerInPrefs(
        context: Context, chipType: ChipType?, currentCycle: Int
    ) {
        PrefsHelper.setNextTimerInPrefs(context, chipType, currentCycle)
    }

    fun cancelTimer(context: Context) {
        PrefsUtils.cancelTimer(context)
    }


}