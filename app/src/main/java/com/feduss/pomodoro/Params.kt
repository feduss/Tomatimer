package com.feduss.pomodoro

sealed class Params(val name: String) {
    object Title: Params("title")
    object Value: Params("value")
    object Unit: Params("unit")
}
