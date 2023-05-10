package com.feduss.tomato

import android.content.Context
import androidx.lifecycle.ViewModel
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.models.Chip
import com.feduss.tomato.utils.PrefsUtils
import com.feduss.tomato.utils.UIText

open class MainViewModel : ViewModel() {
    private var chips = listOf(
        Chip(
            shortTitle = UIText.StringResource(resId = R.string.tomato_timer_name),
            fullTitle = UIText.StringResource(R.string.tomato_timer_name),
            value = "45",
            unit = "min",
            type = ChipType.Tomato
        ),
        Chip(
            shortTitle = UIText.StringResource(R.string.short_break_short_name),
            fullTitle = UIText.StringResource(R.string.short_break_full_name),
            value = "5",
            unit = "min",
            type = ChipType.ShortBreak
        ),
        Chip(
            shortTitle = UIText.StringResource(R.string.cycle_number_short_name),
            fullTitle = UIText.StringResource(R.string.cycle_number_full_name),

            value = "4",
            type = ChipType.CyclesNumber
        ),
        Chip(
            shortTitle = UIText.StringResource(R.string.long_break_short_name),
            fullTitle = UIText.StringResource(R.string.long_break_full_name),
            value = "15",
            unit = "min",
            type = ChipType.LongBreak
        )
    )

    fun loadDataFromPrefs(context: Context): List<Chip> {
        for(chip in chips) {
            val loadedValue = PrefsUtils.getPref(context, chip.type.valuePrefKey)
            if (loadedValue.isNullOrEmpty()) {
                PrefsUtils.setPref(context, chip.type.valuePrefKey, chip.value)
            } else {
                chip.value = loadedValue
            }
        }
        return chips
    }
}