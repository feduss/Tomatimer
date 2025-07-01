package com.feduss.tomato.uistate.viewmodel.notification

import android.content.Context
import androidx.lifecycle.ViewModel
import com.feduss.tomatimer.business.TimerInteractor
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.utils.PrefsUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor()
    : ViewModel() {

    fun getChipTitleFromPrefs(context: Context): String {
        return PrefsUtils.getChipTitleFromPrefs(context)
    }

    fun getCurrentCycleFromPrefs(context: Context): Int {
        return PrefsUtils.getCurrentCycleFromPrefs(context)
    }

    fun getCurrentChipTypeFromPrefs(context: Context): String {
        return PrefsUtils.getCurrentChipTypeFromPrefs(context)
    }

    fun cancelTimerInPrefs(context: Context) {
        PrefsUtils.cancelTimerInPrefs(context)
    }

    fun setNextTimerInPrefs(context: Context, chipType: ChipType?,
                            currentCycle: Int
    ) {
        PrefsUtils.setNextTimerInPrefs(context, chipType, currentCycle)
    }
}