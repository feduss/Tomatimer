package com.feduss.tomatimer.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.enums.PrefParamName

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


                }
                ChipType.LongBreak -> {
                    setPref(context, PrefParamName.CurrentTimerName.name, null)
                    setPref(context, PrefParamName.CurrentCycle.name, null)
                    setPref(context, PrefParamName.SecondsRemaining.name, null)
                }
                else -> {}
            }

            setPref(context, PrefParamName.SecondsRemaining.name, null)
        }

        fun cancelTimer(context: Context) {
            setPref(context, PrefParamName.CurrentTimerIndex.name, null)
            setPref(context, PrefParamName.CurrentCycle.name, null)
            setPref(context, PrefParamName.SecondsRemaining.name, null)
        }

        fun getChipTitleFromPrefs(context: Context): String {
            return PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name) ?: "NoTitle"
        }

        fun getCurrentCycleFromPrefs(context: Context): Int {
            return PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)?.toInt() ?: -1
        }

        fun getCurrentChipTypeFromPrefs(context: Context): String {
            return PrefsUtils.getPref(context, PrefParamName.CurrentChipType.name) ?: "NoType"
        }

        fun cancelTimerInPrefs(context: Context) {
            PrefsUtils.cancelTimer(context)
        }

        fun setNextTimerInPrefs(context: Context, chipType: ChipType?,
                                currentCycle: Int
        ) {
            PrefsUtils.setNextTimer(context, chipType, currentCycle)
        }
    }
}