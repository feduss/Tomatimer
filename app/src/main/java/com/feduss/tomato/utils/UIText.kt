package com.feduss.tomato.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UIText {

    class StringResource(@StringRes val resId: Int, vararg val args: Any): UIText()
    
    @Composable
    fun asString(): String {
        when (this) {
            is StringResource -> return stringResource(id = resId, formatArgs = args)
        }
    }

    fun asString(context: Context): String {
        when (this) {
            is StringResource -> return context.getString(resId, args)
        }
    }
}
