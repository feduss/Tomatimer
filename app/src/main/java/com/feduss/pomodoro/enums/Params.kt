package com.feduss.pomodoro.enums

sealed class Params(val name: String) {
    object Tag: Params("tag")
}
