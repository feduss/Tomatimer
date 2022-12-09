package com.feduss.pomodoro

sealed class Params(val name: String) {
    object Tag: Params("tag")
}
