package com.feduss.pomodoro

sealed class ValueType {
    object Time: ValueType()
    object Cycle: ValueType()
}
