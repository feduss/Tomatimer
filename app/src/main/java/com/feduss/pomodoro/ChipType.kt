package com.feduss.pomodoro

sealed class ChipType(val tag: Int, val prefKey: String) {
    object Tomato: ChipType(0, "Tomato")
    object ShortBreak: ChipType(1, "ShortBreak")
    object CyclesNumber: ChipType(2, "CycleNumber")
    object LongBreak: ChipType(3, "LongBreak")
}
