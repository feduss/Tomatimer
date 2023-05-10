package com.feduss.tomato.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.feduss.tomato.R
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.models.Chip
import com.feduss.tomato.utils.UIText

val demoChips = listOf(
    Chip(
        shortTitle = UIText.StringResource(resId = R.string.tomato_timer_name),
        fullTitle = UIText.StringResource(R.string.tomato_timer_name),
        value = "45",
        unit = "min",
        type = ChipType.Tomato
    ),
    Chip(
        shortTitle = UIText.StringResource(R.string.short_break_short_name),
        fullTitle = UIText.StringResource(R.string.short_break_full_name),
        value = "5",
        unit = "min",
        type = ChipType.ShortBreak
    ),
    Chip(
        shortTitle = UIText.StringResource(R.string.cycle_number_short_name),
        fullTitle = UIText.StringResource(R.string.cycle_number_full_name),

        value = "4",
        type = ChipType.CyclesNumber
    ),
    Chip(
        shortTitle = UIText.StringResource(R.string.long_break_short_name),
        fullTitle = UIText.StringResource(R.string.long_break_full_name),
        value = "15",
        unit = "min",
        type = ChipType.LongBreak
    )
)

class ChipDatas{
    companion object {
        val demoList = demoChips
    }
}

class ChipProvider: PreviewParameterProvider<Chip> {

    override val values: Sequence<Chip> = sequenceOf(demoChips[0])
}