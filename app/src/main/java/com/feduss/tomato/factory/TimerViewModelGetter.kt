package com.feduss.tomato.factory

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomato.hilt.ViewModelFactory
import com.feduss.tomato.view.MainViewController
import com.feduss.tomato.viewmodel.setup.SetupViewModel
import com.feduss.tomato.viewmodel.timer.TimerViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun getTimerViewModel(
    mainViewController: MainViewController,
    chips: List<Chip>,
    initialChipIndex: Int,
    initialCycle: Int,
    initialTimerSeconds: Int
): TimerViewModel = viewModel(
    factory = TimerViewModel.provideFactory(
        assistedFactory = EntryPointAccessors.fromActivity(
            mainViewController,
            ViewModelFactory::class.java
        ).timerViewModelFactory(),
        chips = chips,
        initialChipIndex = initialChipIndex,
        initialCycle = initialCycle,
        initialTimerSeconds = initialTimerSeconds
    )
)