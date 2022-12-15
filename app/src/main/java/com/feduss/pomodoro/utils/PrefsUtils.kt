package com.feduss.pomodoro.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

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
    }
}