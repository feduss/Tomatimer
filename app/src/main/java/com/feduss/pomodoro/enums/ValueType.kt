package com.feduss.pomodoro.enums

sealed class ValueType {
    object Time: ValueType()
    object Cycle: ValueType()
}
