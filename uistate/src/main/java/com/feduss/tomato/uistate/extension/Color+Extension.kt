package com.feduss.tomato.uistate.extension

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt

val Color.Companion.PurpleCustom: Color
    get() { return Color("#E3BAFF".toColorInt()) }

//TODO: find another name
val Color.Companion.ActiveTimer: Color
    get() { return Color("#649e5d".toColorInt()) }

//TODO: find another name
val Color.Companion.InactiveTimer: Color
    get() { return Color("#a15757".toColorInt()) }

fun Color.toHexString(): String {
    return String.format("#%06X", 0xFFFFFF and this.toArgb())
}

fun Color.Companion.fromHex(hex: String?): Color? {
    if (hex.isNullOrEmpty()) {
        return null
    }
    return Color(android.graphics.Color.parseColor("#$hex"))
}