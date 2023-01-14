package com.feduss.tomato.enums

sealed class PrefParamName(val name: String) {
    object CurrentTimerIndex: PrefParamName("CurrentTimerIndex")
    object CurrentTimerName: PrefParamName("CurrentTimerName")
    object CurrentCycle: PrefParamName("CurrentCycle")
    object CurrentChipType: PrefParamName("CurrentChipType")
    object SecondsRemaining: PrefParamName("SecondsRemaining")
    object AlarmSetTime: PrefParamName("AlarmSetTime")
    object IsTimerActive: PrefParamName("IsTimerActive")
}