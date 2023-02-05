package com.feduss.tomatimer.enums

sealed class ValueType {
    object Time: ValueType()
    object Cycle: ValueType()
}
