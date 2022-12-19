package com.feduss.tomato.enums

sealed class ValueType {
    object Time: ValueType()
    object Cycle: ValueType()
}
