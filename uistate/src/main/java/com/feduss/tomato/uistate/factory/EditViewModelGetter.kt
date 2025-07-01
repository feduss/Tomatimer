package com.feduss.tomato.uistate.factory

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomato.uistate.hilt.ViewModelFactory
import com.feduss.tomato.uistate.viewmodel.edit.EditViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun getEditViewModel(
    activity: Activity,
    chip: Chip
): EditViewModel = viewModel(
    factory = EditViewModel.provideFactory(
        assistedFactory = EntryPointAccessors.fromActivity(
            activity,
            ViewModelFactory::class.java
        ).editViewModelFactory(),
        chip = chip
    )
)