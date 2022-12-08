package com.feduss.pomodoro

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class Chip(val title: String, val value: String, val unit: String = "") {

}

class ChipProvider: PreviewParameterProvider<Chip> {

    override val values: Sequence<Chip> = sequenceOf(
        Chip("Pomodoro", "25", "min")
    )
}

class ChipListProvider: PreviewParameterProvider<ArrayList<Chip>> {

    override val values: Sequence<ArrayList<Chip>> = sequenceOf(
        ArrayList(
            listOf(
                Chip("Pomodoro", "25", "min"),
                Chip("P. breve", "5", "min"),
                Chip("Num. cicli", "4"),
                Chip("P. lunga", "25", "min")
            ))
    )
}