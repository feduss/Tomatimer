package com.feduss.tomato.factory

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomato.hilt.ViewModelFactory
import com.feduss.tomato.view.MainViewController
import com.feduss.tomato.viewmodel.edit.EditViewModel
import com.feduss.tomato.viewmodel.setup.SetupViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun getSetupViewModel(
    mainViewController: MainViewController,
    chips: List<Chip>
): SetupViewModel = viewModel(
    factory = SetupViewModel.provideFactory(
        assistedFactory = EntryPointAccessors.fromActivity(
            mainViewController,
            ViewModelFactory::class.java
        ).setupViewModelFactory(),
        chips = chips
    )
)