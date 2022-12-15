package com.feduss.pomodoro.models

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.feduss.pomodoro.enums.ChipType

class Chip(val title: String, var value: String, val unit: String = "", val type: ChipType) {

}

class ChipProvider: PreviewParameterProvider<Chip> {

    override val values: Sequence<Chip> = sequenceOf(
        Chip("Pomodoro", "25", "min", type = ChipType.Tomato)
    )
}

class ChipListProvider: PreviewParameterProvider<ArrayList<Chip>> {

    override val values: Sequence<ArrayList<Chip>> = sequenceOf(
        ArrayList(
            listOf(
                Chip("Pomodoro", "25", "min", type = ChipType.Tomato),
                Chip("P. breve", "5", "min", type = ChipType.ShortBreak),
                Chip("Num. cicli", "4", type = ChipType.CyclesNumber),
                Chip("P. lunga", "25", "min", type = ChipType.LongBreak)
            ))
    )
}