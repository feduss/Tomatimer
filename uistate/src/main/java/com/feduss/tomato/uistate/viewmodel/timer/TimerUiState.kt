package com.feduss.tomato.uistate.viewmodel.timer

import androidx.compose.ui.graphics.Color
import com.feduss.tomatimer.entity.enums.AlertType
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomato.uistate.CompactButtonUiState
import com.feduss.tomato.uistate.TextIdUiState
import com.feduss.tomato.uistate.TextUiState
import java.util.UUID

data class TimerUiState(
    val currentChip: Chip,
    val progressBarColor: Color,
    val progressBarValue: Double,
    val titleUiState: TextUiState,
    val currentCycleUiState: CurrentCycleUiState,
    val middleTextUiState: TextUiState,
    val maxTimerSeconds: Int,
    val currentTimerSecondsRemaining: Int,
    val isLastTimer: Boolean,
    val bottomLeftButtonUiState: CompactButtonUiState,
    val bottomRightButtonUiState: CompactButtonUiState,
    val appWasOnPause: Boolean,
    val isTimerActive: Boolean,
    val timeText: String,
    val isAlertDialogVisible: Boolean,
    val alertDialogUiState: TimerAlertDialogUiState?
)

data class TimerAlertDialogUiState(
    val titleId: Int,
    val negativeButtonIconId: Int,
    val negativeButtonIconDesc: String,
    val positiveButtonIconId: Int,
    val positiveButtonIconDesc: String,
    val alertType: AlertType
)

data class CurrentCycleUiState(
    val value: Int,
    val textUiState: TextUiState
)