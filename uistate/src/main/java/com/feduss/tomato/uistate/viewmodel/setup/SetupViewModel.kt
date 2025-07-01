package com.feduss.tomato.uistate.viewmodel.setup

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.uistate.R
import com.feduss.tomato.uistate.viewmodel.ChipUiState
import com.feduss.tomato.uistate.CompactButtonUiState
import com.feduss.tomato.uistate.TextIdUiState
import com.feduss.tomato.uistate.extension.PurpleCustom
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class SetupViewModel @AssistedInject constructor(
    @Assisted("chips") private var chips: List<Chip>
) : ViewModel() {

    //Factory
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("chips") chips: List<Chip>,
        ): SetupViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            chips: List<Chip>,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(
                    chips
                ) as T
            }
        }
    }

    sealed class NavUiState {
        data class GoToChipEdit(val tag: String): NavUiState()
        data object GoToStartTimer: NavUiState()
        data object GoToAppSettings: NavUiState()
    }

    private var _dataUiState = MutableStateFlow<SetupUiState?>(null)
    var dataUiState = _dataUiState.asStateFlow()

    private var _navUiState = MutableStateFlow<NavUiState?>(null)
    val navUiState = _navUiState.asStateFlow()

    private var lastSelectedChipIndex: Int? = null

    // Copies
    private val appVersionTextId = R.string.setup_app_version_text
    private val settingsTextId = R.string.app_settings_button_description

    // Assets
    private val playIconId = R.drawable.ic_play_24dp
    private val playIconDescription = "ic_play"
    private val settingsIconId = R.drawable.ic_settings
    private val settingsIconDescription = "ic_settings"

    fun initUiState(context: Context) {

        if (_dataUiState.value != null) return

        _dataUiState.value = SetupUiState(
            chipsUiState = chips.map {
                ChipUiState(
                    shortTitle = it.shortTitle,
                    fullTitle = it.fullTitle,
                    value = it.value,
                    unit = it.unit,
                    type = it.type
                )
            },
            playCompactButtonUiState = CompactButtonUiState(
                backgroundColor = Color.PurpleCustom,
                iconId = playIconId,
                iconDescription = playIconDescription,
                iconColor = Color.Black
            ),
            versionUiState = TextIdUiState(
                textId = appVersionTextId,
                color = Color.White
            ),
            settingsUiState = TextIdUiState(
                textId = settingsTextId,
                color = Color.White
            ),
            settingsCompactButtonUiState = CompactButtonUiState(
                backgroundColor = Color.PurpleCustom,
                iconId = settingsIconId,
                iconDescription = settingsIconDescription,
                iconColor = Color.Black
            )
        )
    }

    fun updateLastSelectedChip(newValue: String) {
        lastSelectedChipIndex?.let { index ->
            val tempChips = chips
            tempChips[index].value = newValue
            chips = tempChips
            _dataUiState.update {
                it?.copy(
                    chipsUiState = chips.map {
                        ChipUiState(
                            shortTitle = it.shortTitle,
                            fullTitle = it.fullTitle,
                            value = it.value,
                            unit = it.unit,
                            type = it.type
                        )
                    }
                )
            }
        }
    }

    fun getSecondsFromAlarmTime(context: Context): String {
        val alarmSetTime = PrefsUtils.getPref(context, PrefParamName.AlarmSetTime.name)?.toLong() ?: 0L
        val nowSeconds = Calendar.getInstance().timeInMillis / 1000L

        return (alarmSetTime - nowSeconds).toString()
    }

    fun cancelTimer(context: Context) {
        PrefsUtils.cancelTimer(context)
    }

    fun getChipIndexFromPref(context: Context): String? {
        return PrefsUtils.getPref(context, PrefParamName.CurrentTimerIndex.name)
    }

    fun getCycleIndexFromPref(context: Context): String? {
        return PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)
    }

    fun getSecondsRemainingFromPref(context: Context): String? {
        return PrefsUtils.getPref(context, PrefParamName.SecondsRemaining.name)
    }

    fun userHasSelectedChip(index: Int) {

    }

    fun firedNavState() {
        _navUiState.value = null
    }

    fun userTapOnChip(tag: String) {
        _navUiState.value = NavUiState.GoToChipEdit(tag = tag)
        lastSelectedChipIndex = tag.toInt()
    }

    fun userHasTappedPlayButton() {
        _navUiState.value = NavUiState.GoToStartTimer
    }

    fun userHasTappedSettingsButton() {
        _navUiState.value = NavUiState.GoToAppSettings
    }
}