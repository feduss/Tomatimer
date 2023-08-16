package com.feduss.tomatimer.entity.enums

sealed class ValueType {
    object Time: ValueType()
    object Cycle: ValueType()
}
