package com.feduss.tomatimer.entity.enums

sealed class Params(val name: String) {
    object Tag: Params("tag")
}
