package com.feduss.tomato.enums

sealed class ChipType(val tag: Int, val valuePrefKey: String) {
    object Tomato: ChipType(0, "TomatoValue")
    object ShortBreak: ChipType(1, "ShortBreakValue")
    object CyclesNumber: ChipType(2, "CycleNumberValue")
    object LongBreak: ChipType(3, "LongBreakValue")
}
