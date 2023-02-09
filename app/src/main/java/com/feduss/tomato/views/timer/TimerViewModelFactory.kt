package com.feduss.tomato.views.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feduss.tomato.models.Chip

@Suppress("UNCHECKED_CAST")
class TimerViewModelFactory(
    private val chips: List<Chip>, private val initialChipIndex: Int,
    private val initialCycle: Int, private val initialTimerSeconds: Int
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java))
            return TimerViewModel(chips, initialChipIndex, initialCycle, initialTimerSeconds) as T
        throw IllegalArgumentException("Unknown Viewmodel class")
    }
}