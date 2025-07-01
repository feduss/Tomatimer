package com.feduss.tomato.uistate.viewmodel

import com.feduss.tomatimer.entity.enums.ChipType

data class ChipUiState(
    val shortTitle: String,
    val fullTitle: String,
    var value: String,
    val unit: String = "",
    val type: ChipType
)
