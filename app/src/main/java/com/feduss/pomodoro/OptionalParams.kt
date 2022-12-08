package com.feduss.pomodoro

sealed class OptionalParams(val name: String) {
    object Unit: OptionalParams(name = "unit")
}
