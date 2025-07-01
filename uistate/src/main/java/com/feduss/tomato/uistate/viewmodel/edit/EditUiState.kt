package com.feduss.tomato.uistate.viewmodel.edit

import androidx.compose.ui.graphics.Color
import com.feduss.tomato.uistate.CompactButtonUiState

data class EditUiState(
    val numberOfOption: Int,
    val initOption: Int,
    val items: List<Int>,
    val progressBarState: Double,
    val progressBarColor: Color,
    val titleText: String,
    val titleColor: Color,
    val pickerReadOnlyLabelText: String,
    val pickerReadOnlyLabelColor: Color,
    val pickerInnerLabelSuffixText: String,
    val pickerInnerLabelColor: Color,
    val confirmButtonUiState: CompactButtonUiState
)
