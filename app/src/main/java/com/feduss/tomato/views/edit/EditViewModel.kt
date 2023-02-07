package com.feduss.tomato.views.edit

import android.content.Context
import com.feduss.tomato.MainViewModel
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.models.Chip
import com.feduss.tomato.utils.PrefsUtils

class EditViewModel(val chip: Chip) : MainViewModel() {

    fun setNewValue(context: Context, type: ChipType, newValue: String) {
        PrefsUtils.setPref(context, type.valuePrefKey, newValue)
    }
}