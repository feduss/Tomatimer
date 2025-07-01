package com.feduss.tomato.hilt

import com.feduss.tomato.viewmodel.edit.EditViewModel
import com.feduss.tomato.viewmodel.setup.SetupViewModel
import com.feduss.tomato.viewmodel.timer.TimerViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactory {
    fun editViewModelFactory(): EditViewModel.Factory
    fun setupViewModelFactory(): SetupViewModel.Factory
    fun timerViewModelFactory(): TimerViewModel.Factory
}