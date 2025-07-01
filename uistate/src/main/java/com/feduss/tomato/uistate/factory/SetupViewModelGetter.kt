package com.feduss.tomato.uistate.factory

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomato.uistate.hilt.ViewModelFactory
import com.feduss.tomato.uistate.viewmodel.setup.SetupViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun getSetupViewModel(
    activity: Activity,
    chips: List<Chip>
): SetupViewModel = viewModel(
    factory = SetupViewModel.provideFactory(
        assistedFactory = EntryPointAccessors.fromActivity(
            activity,
            ViewModelFactory::class.java
        ).setupViewModelFactory(),
        chips = chips
    )
)