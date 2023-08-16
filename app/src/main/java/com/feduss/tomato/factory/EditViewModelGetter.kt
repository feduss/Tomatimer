package com.feduss.tomato.factory

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomato.hilt.ViewModelFactory
import com.feduss.tomato.view.MainViewController
import com.feduss.tomato.viewmodel.edit.EditViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun getEditViewModel(
    mainViewController: MainViewController,
    chip: Chip
): EditViewModel = viewModel(
    factory = EditViewModel.provideFactory(
        assistedFactory = EntryPointAccessors.fromActivity(
            mainViewController,
            ViewModelFactory::class.java
        ).editViewModelFactory(),
        chip = chip
    )
)