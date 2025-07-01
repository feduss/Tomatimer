package com.feduss.tomato.uistate.viewmodel.setup

import com.feduss.tomato.uistate.viewmodel.ChipUiState
import com.feduss.tomato.uistate.CompactButtonUiState
import com.feduss.tomato.uistate.TextIdUiState

data class SetupUiState(
    val chipsUiState: List<ChipUiState>,
    val playCompactButtonUiState: CompactButtonUiState,
    val versionUiState: TextIdUiState,
    val settingsUiState: TextIdUiState,
    val settingsCompactButtonUiState: CompactButtonUiState,

    )