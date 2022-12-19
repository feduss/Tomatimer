package com.feduss.tomato.enums

sealed class PrefParamName(val name: String) {
    object CurrentChip: PrefParamName("CurrentChip")
    object CurrentCycle: PrefParamName("CurrentCycleName")
    object SecondsRemaining: PrefParamName("SecondsRemaining")
    object AlarmSetTime: PrefParamName("AlarmSetTime")
}