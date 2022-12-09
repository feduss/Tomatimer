package com.feduss.pomodoro

sealed class ChipType(val tag: Int, val valuePrefKey: String, val valueRemainingPrefKey: String) {
    object Tomato: ChipType(0, "TomatoValue", "TomatoRemaining")
    object ShortBreak: ChipType(1, "ShortBreakValue", "ShortBreakRemaining")
    object CyclesNumber: ChipType(2, "CycleNumberValue", "CycleNumberRemaining")
    object LongBreak: ChipType(3, "LongBreakValue", "LongBreakRemaining")
}
