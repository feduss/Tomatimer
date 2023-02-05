package com.feduss.tomatimer.enums

sealed class OptionalParams(val name: String) {
    object Unit: OptionalParams(name = "unit")
    object ChipIndex: OptionalParams(name = "chipIndex")
    object CycleIndex: OptionalParams(name = "cycleIndex")
    object TimerSeconds: OptionalParams(name = "timerSeconds")
}
