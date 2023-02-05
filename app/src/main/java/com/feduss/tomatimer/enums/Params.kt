package com.feduss.tomatimer.enums

sealed class Params(val name: String) {
    object Tag: Params("tag")
}
