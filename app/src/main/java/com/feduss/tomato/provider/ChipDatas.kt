package com.feduss.tomato.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.models.Chip

val demoChips = listOf(
    Chip("Pomodoro", "25", "min", type = ChipType.Tomato),
    Chip("P. breve", "5", "min", type = ChipType.ShortBreak),
    Chip("Num. cicli", "4", type = ChipType.CyclesNumber),
    Chip("P. lunga", "25", "min", type = ChipType.LongBreak)
)

class ChipDatas{
    companion object {
        val demoList = demoChips
    }
}

class ChipProvider: PreviewParameterProvider<Chip> {

    override val values: Sequence<Chip> = sequenceOf(demoChips[0])
}

class ChipListProvider: PreviewParameterProvider<ArrayList<Chip>> {

    override val values: Sequence<ArrayList<Chip>> = sequenceOf(
        ArrayList(demoChips)
    )
}