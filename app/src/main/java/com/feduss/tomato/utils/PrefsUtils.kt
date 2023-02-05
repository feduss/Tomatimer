package com.feduss.tomato.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.enums.PrefParamName

class PrefsUtils {

    companion object {
        private fun getSharedPreferences(context: Context ): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        fun getPref(context: Context, pref: String): String? {
            return getSharedPreferences(context).getString(pref, null)
        }

        fun setPref(context: Context, pref: String, newValue: String?) {
            getSharedPreferences(context).edit().putString(pref, newValue).apply()
        }

        fun setNextTimer(context: Context, chipType: ChipType?, currentCycle: Int) {
            chipType?.let { chipTypeNotNull ->
                val totalCycles: Int =
                    getPref(context, ChipType.CyclesNumber.valuePrefKey)?.toInt() ?: -1

                when (chipType) {
                    ChipType.Tomato -> {
                        if (currentCycle == totalCycles - 1) {
                            setPref(
                                context,
                                PrefParamName.CurrentTimerIndex.name,
                                ChipType.LongBreak.tag.toString()
                            )
                        } else {
                            setPref(
                                context,
                                PrefParamName.CurrentTimerIndex.name,
                                ChipType.ShortBreak.tag.toString()
                            )
                        }
                    }
                    ChipType.ShortBreak -> {
                        setPref(
                            context,
                            PrefParamName.CurrentTimerIndex.name,
                            ChipType.Tomato.tag.toString()
                        )
                        setPref(
                            context,
                            PrefParamName.CurrentCycle.name,
                            (currentCycle + 1).toString()
                        )
                    }
                    ChipType.LongBreak -> {
                        setPref(context, PrefParamName.CurrentTimerName.name, null)
                        setPref(context, PrefParamName.CurrentCycle.name, null)
                        setPref(context, PrefParamName.SecondsRemaining.name, null)
                    }
                    else -> {}
                }

                //TODO: è necessario?
                setPref(context, PrefParamName.SecondsRemaining.name, null)
            }
        }
    }
}