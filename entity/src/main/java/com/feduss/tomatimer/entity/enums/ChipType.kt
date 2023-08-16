package com.feduss.tomatimer.entity.enums

sealed class ChipType(val tag: Int, val valuePrefKey: String, val stringValue: String) {
    object Tomato: ChipType(0, "TomatoValue", "Tomato")
    object ShortBreak: ChipType(1, "ShortBreakValue", "ShortBreak")
    object CyclesNumber: ChipType(2, "CycleNumberValue", "CyclesNumber")
    object LongBreak: ChipType(3, "LongBreakValue", "LongBreak")

    companion object {
        fun fromString(input: String?): ChipType? {
            when(input) {
                Tomato.stringValue -> return Tomato
                ShortBreak.stringValue -> return ShortBreak
                CyclesNumber.stringValue -> return CyclesNumber
                LongBreak.stringValue -> return LongBreak
            }

            return null
        }
    }


}
