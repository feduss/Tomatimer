package com.feduss.tomato.viewmodel.notification

import android.content.Context
import androidx.lifecycle.ViewModel
import com.feduss.tomatimer.business.TimerInteractor
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomato.helper.PrefsHelper
import com.feduss.tomato.view.notification.NotificationViewController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor()
    : ViewModel() {

    fun getChipTitleFromPrefs(context: Context): String {
        return PrefsHelper.getChipTitleFromPrefs(context)
    }

    fun getCurrentCycleFromPrefs(context: Context): Int {
        return PrefsHelper.getCurrentCycleFromPrefs(context)
    }

    fun getCurrentChipTypeFromPrefs(context: Context): String {
        return PrefsHelper.getCurrentChipTypeFromPrefs(context)
    }

    fun cancelTimerInPrefs(context: NotificationViewController) {
        PrefsHelper.cancelTimerInPrefs(context)
    }

    fun setNextTimerInPrefs(context: NotificationViewController, chipType: ChipType?,
                            currentCycle: Int
    ) {
        PrefsHelper.setNextTimerInPrefs(context, chipType, currentCycle)
    }
}