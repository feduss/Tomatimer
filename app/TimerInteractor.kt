import android.content.Context

interface TimerInteractor {
    fun getChipTitleFromPrefs(context: Context): String

    fun getCurrentCycleFromPrefs(context: Context): Int

    fun getCurrentChipTypeFromPrefs(context: Context): String

    fun cancelTimerInPrefs(context: Context)

    fun setNextTimerInPrefs(context: Context, chipType: ChipType?,
                            currentCycle: Int
    )
}

class TimerInteractorImpl: TimerInteractor {

    fun getChipTitleFromPrefs(context: Context): String {
        return PrefsUtils.getPref(context, PrefParamName.CurrentTimerName.name) ?: "NoTitle"
    }

    fun getCurrentCycleFromPrefs(context: Context): Int {
        return PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)?.toInt() ?: -1
    }

    fun getCurrentChipTypeFromPrefs(context: Context): String {
        return PrefsUtils.getPref(context, PrefParamName.CurrentChipType.name) ?: "NoType"
    }

    fun cancelTimerInPrefs(context: NotificationViewController) {
        PrefsUtils.cancelTimer(context)
    }

    fun setNextTimerInPrefs(context: NotificationViewController, chipType: ChipType?,
                            currentCycle: Int
    ) {
        PrefsUtils.setNextTimer(context, chipType, currentCycle)
    }
}