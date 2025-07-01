package com.feduss.tomato.uistate.viewmodel.timer

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feduss.tomatimer.entity.enums.AlertType
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.uistate.CompactButtonUiState
import com.feduss.tomato.uistate.R
import com.feduss.tomato.uistate.TextUiState
import com.feduss.tomato.uistate.extension.ActiveTimer
import com.feduss.tomato.uistate.extension.InactiveTimer
import com.feduss.tomato.uistate.extension.PurpleCustom
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class TimerViewModel @AssistedInject constructor(
    @Assisted("chips") val chips: List<Chip>,
    @Assisted("initialChipIndex") val initialChipIndex: Int = 0,
    @Assisted("initialCycle") val initialCycle: Int = 0,
    @Assisted("initialTimerSeconds") val initialTimerSeconds: Int = 0
) : ViewModel() {

    //Factory
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("chips") chips: List<Chip>,
            @Assisted("initialChipIndex") initialChipIndex: Int,
            @Assisted("initialCycle") initialCycle: Int,
            @Assisted("initialTimerSeconds") initialTimerSeconds: Int
        ): TimerViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            chips: List<Chip>,
            initialChipIndex: Int,
            initialCycle: Int,
            initialTimerSeconds: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(
                    chips,
                    initialChipIndex,
                    initialCycle,
                    initialTimerSeconds
                ) as T
            }
        }
    }

    sealed class NavUiState {
        data object GoBackToHome: NavUiState()
        data class GoToNextTimer(
            val currentChipType: ChipType,
            val currentCycle: Int
        ): NavUiState()
    }

    private var _dataUiState = MutableStateFlow<TimerUiState?>(null)
    var dataUiState = _dataUiState.asStateFlow()

    private var _navUiState = MutableStateFlow<NavUiState?>(null)
    val navUiState = _navUiState.asStateFlow()

    val totalCycles = chips[2].value.toInt()

    // Copies
    val skipTimerTextId = R.string.skip_timer
    val stopTimerTextId = R.string.stop_timer_question
    val cycleNameTextId = R.string.cycle_name

    // Assets
    val playIconId = R.drawable.ic_play_24dp
    val playIconDescription = "ic_play"
    val pauseIconId = R.drawable.ic_pause_24dp
    val pauseIconDescription = "ic_pause"
    val skipIconId = R.drawable.ic_skip_next
    val skipIconDescription = "ic_skip_next"
    val closeIconId = R.drawable.ic_close_24dp
    val closeIconDescription = "ic_close"
    val checkIconId = R.drawable.ic_check_24dp
    val checkIconDescription = "ic_check"

    fun initUiState(context: Context) {

        if (_dataUiState.value != null) return

        //Current timer
        val currentChip = chips[initialChipIndex]

        val time = currentChip.value.toInt()
        val minutes: Int = time/60
        val seconds: Int = time%60
        val middleText =  "$minutes:$seconds"

        val maxTimerSeconds =
            if (initialTimerSeconds > 0)
                initialTimerSeconds else
                currentChip.value.toInt() * 60

        _dataUiState.value = TimerUiState(
            progressBarColor = Color.ActiveTimer,
            progressBarValue = 0.0, //TODO: to check
            titleUiState = TextUiState(
                text = currentChip.fullTitle,
                color = Color.PurpleCustom
            ),
            currentChip = currentChip,
            currentCycleUiState = CurrentCycleUiState(
                value = initialCycle,
                textUiState = TextUiState(
                    text = context.getString(cycleNameTextId, (initialCycle + 1), totalCycles),
                    color = Color.PurpleCustom
                )
            ),
            middleTextUiState = TextUiState(
                text = middleText,
                color = Color.PurpleCustom
            ),
            maxTimerSeconds = maxTimerSeconds,
            currentTimerSecondsRemaining = maxTimerSeconds,
            isTimerActive = true,
            bottomLeftButtonUiState = CompactButtonUiState(
                backgroundColor = Color.PurpleCustom,
                iconId = pauseIconId,
                iconDescription = pauseIconDescription,
                iconColor = Color.Black
            ),
            bottomRightButtonUiState = CompactButtonUiState(
                backgroundColor = Color.PurpleCustom,
                iconId = skipIconId,
                iconDescription = skipIconDescription,
                iconColor = Color.Black
            ),
            appWasOnPause = false,
            isLastTimer = isLastTimer(
                currentChipType = currentChip.type,
                currentCycle = initialCycle
            ),
            timeText = getUpdatedTimeText(maxTimerSeconds),
            isAlertDialogVisible = false,
            alertDialogUiState = null
        )
    }

    fun updateNewTimeText(secondsRemaining: Int) {
        _dataUiState.update {
            it?.copy(
                timeText = getUpdatedTimeText(secondsRemaining)
            )
        }
    }

    fun updateStatus(
        newMiddleText: String,
        newProgress: Double,
        currentTimerSecondsRemaining: Int
    ) {
        _dataUiState.update {
            it?.copy(
                middleTextUiState = it.middleTextUiState.copy(
                    text = newMiddleText
                ),
                progressBarValue = newProgress,
                currentTimerSecondsRemaining = currentTimerSecondsRemaining
            )
        }
    }

    fun changeAlertDialogStatus(context: Context, alertType: AlertType?) {
        val isVisible = alertType != null
        _dataUiState.update {
            val secondsRemainings = if (isVisible) it?.maxTimerSeconds ?: 0 else loadTimerSecondsRemainings(context)
            return@update it?.copy(
                progressBarColor = if (isVisible) Color.InactiveTimer else Color.ActiveTimer,
                isAlertDialogVisible = isVisible,
                isTimerActive = !isVisible,
                maxTimerSeconds = secondsRemainings,
                timeText = getUpdatedTimeText(secondsRemainings),
                alertDialogUiState = if (alertType != null) {
                    TimerAlertDialogUiState(
                        titleId = if (alertType == AlertType.SkipTimer) skipTimerTextId else stopTimerTextId ,
                        negativeButtonIconId = closeIconId ,
                        negativeButtonIconDesc = closeIconDescription,
                        positiveButtonIconId = checkIconId,
                        positiveButtonIconDesc = checkIconDescription,
                        alertType = alertType

                    )
                } else {
                    null
                }
            )
        }
    }

    private fun getUpdatedTimeText(secondsRemaining: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, secondsRemaining)

        val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
        val calendarMinutes = calendar.get(Calendar.MINUTE)
        val minutes = if (calendarMinutes < 10) "0$calendarMinutes" else calendarMinutes
        return "$calendarHour:$minutes"
    }

    private fun isLastTimer(currentChipType: ChipType, currentCycle: Int): Boolean {
        return currentChipType == ChipType.LongBreak && currentCycle == totalCycles - 1
    }

    fun saveCurrentTimerData(
        context: Context,
        currentChip: Chip,
        currentCycle: Int,
        secondsRemaining: Int
    ) {

        //Save the current chip type
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.CurrentChipType.name,
            newValue = currentChip.type.stringValue
        )

        //Save the current chip index
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.CurrentTimerIndex.name,
            newValue = currentChip.type.tag.toString()
        )

        //Save the current timer title
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.CurrentTimerName.name,
            newValue = currentChip.fullTitle
        )

        //Save the current cycle
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.CurrentCycle.name,
            newValue = currentCycle.toString()
        )

        //Save the timer seconds remaining
        PrefsUtils.setPref(
            context = context,
            pref = PrefParamName.SecondsRemaining.name,
            newValue = secondsRemaining?.toString()
        )
    }

    fun loadTimerSecondsRemainings(context: Context): Int {
        return PrefsUtils.getPref(context, PrefParamName.SecondsRemaining.name)?.toInt() ?: 0
    }

    fun setTimerState(context: Context, isTimerActive: Boolean) {
        val stringValue = if (isTimerActive) "true" else "false"
        PrefsUtils.setPref(context, PrefParamName.IsTimerActive.name, stringValue)

        _dataUiState.update {
            val secondsRemainings = if (isTimerActive) it?.maxTimerSeconds ?: 0 else loadTimerSecondsRemainings(context)
            return@update it?.copy(
                isTimerActive = isTimerActive,
                progressBarColor = if (isTimerActive) Color.ActiveTimer else Color.InactiveTimer,
                maxTimerSeconds = secondsRemainings,
                bottomLeftButtonUiState = it.bottomRightButtonUiState.copy(
                    iconId = if (isTimerActive) pauseIconId else playIconId,
                    iconDescription = if (isTimerActive) pauseIconDescription else playIconDescription
                )
            )
        }
    }

    fun setNextTimerInPrefs(
        context: Context, chipType: ChipType?, currentCycle: Int
    ) {
        PrefsUtils.setNextTimerInPrefs(context, chipType, currentCycle)
    }

    fun cancelTimer(context: Context) {
        PrefsUtils.cancelTimer(context)
    }

    fun userStopTimer() {
        _navUiState.value = NavUiState.GoBackToHome
    }

    fun userSkipTimer(currentChipType: ChipType, currentCycle: Int) {
        _navUiState.value = NavUiState.GoToNextTimer(
            currentChipType,
            currentCycle
        )
    }

    fun firedNavState() {
        _navUiState.value = null
    }

}