package com.feduss.tomato.viewmodel.notification

import android.content.Context
import androidx.lifecycle.ViewModel
import com.feduss.tomatimer.business.TimerInteractor
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomato.view.notification.NotificationViewController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val timerInteractor: TimerInteractor)
    : ViewModel() {

    fun getChipTitleFromPrefs(context: Context): String {
        return timerInteractor.getChipTitleFromPrefs(context)
    }

    fun getCurrentCycleFromPrefs(context: Context): Int {
        return timerInteractor.getCurrentCycleFromPrefs(context)
    }

    fun getCurrentChipTypeFromPrefs(context: Context): String {
        return timerInteractor.getCurrentChipTypeFromPrefs(context)
    }

    fun cancelTimerInPrefs(context: NotificationViewController) {
        timerInteractor.cancelTimerInPrefs(context)
    }

    fun setNextTimerInPrefs(context: NotificationViewController, chipType: ChipType?,
                            currentCycle: Int
    ) {
        timerInteractor.setNextTimerInPrefs(context, chipType, currentCycle)
    }
}