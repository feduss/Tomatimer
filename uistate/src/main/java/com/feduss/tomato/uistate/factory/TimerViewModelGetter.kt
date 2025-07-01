package com.feduss.tomato.uistate.factory

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomato.uistate.hilt.ViewModelFactory
import com.feduss.tomato.uistate.viewmodel.timer.TimerViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun getTimerViewModel(
    activity: Activity,
    chips: List<Chip>,
    initialChipIndex: Int,
    initialCycle: Int,
    initialTimerSeconds: Int
): TimerViewModel = viewModel(
    factory = TimerViewModel.provideFactory(
        assistedFactory = EntryPointAccessors.fromActivity(
            activity,
            ViewModelFactory::class.java
        ).timerViewModelFactory(),
        chips = chips,
        initialChipIndex = initialChipIndex,
        initialCycle = initialCycle,
        initialTimerSeconds = initialTimerSeconds
    )
)