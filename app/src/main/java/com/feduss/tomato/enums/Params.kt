package com.feduss.tomato.enums

sealed class Params(val name: String) {
    object Tag: Params("tag")
}
