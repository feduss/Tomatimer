package com.feduss.tomato.uistate.viewmodel.edit

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.uistate.CompactButtonUiState
import com.feduss.tomato.uistate.R
import com.feduss.tomato.uistate.extension.PurpleCustom
import com.feduss.tomato.uistate.viewmodel.MainViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditViewModel @AssistedInject constructor(
    @Assisted("chip") val chip: Chip
) : MainViewModel() {

    //Factory
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("chip") chip: Chip
        ): EditViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            chip: Chip
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(chip) as T
            }
        }
    }

    sealed class NavUiState {
        data class GoBackToHome(val newValue: String): NavUiState()
    }

    private var _dataUiState = MutableStateFlow<EditUiState?>(null)
    var dataUiState = _dataUiState.asStateFlow()

    private var _navUiState = MutableStateFlow<NavUiState?>(null)
    val navUiState = _navUiState.asStateFlow()

    private val numbOptions =
        when (chip.type) {
            ChipType.CyclesNumber -> 10
            else -> {
                60
            }
        }

    // Copies


    // Assets
    private val checkIconId = R.drawable.ic_check_24dp
    private val checkIconDescription = "ic_check"

    fun initUiState() {

        if (_dataUiState.value != null) return

        val initOption = chip.value.toInt() - 1
        val purpleColor = Color.PurpleCustom
        _dataUiState.value = EditUiState(
            numberOfOption = numbOptions,
            initOption = initOption,
            items = (1..numbOptions + 1).toList(),
            progressBarState = (initOption + 1).toDouble() / numbOptions,
            progressBarColor = purpleColor,
            titleText = chip.fullTitle,
            titleColor = purpleColor,
            pickerReadOnlyLabelText = chip.fullTitle,
            pickerReadOnlyLabelColor = Color.White,
            pickerInnerLabelSuffixText = chip.unit,
            pickerInnerLabelColor = purpleColor,
            confirmButtonUiState = CompactButtonUiState(
                backgroundColor = purpleColor,
                iconId = checkIconId,
                iconDescription = checkIconDescription,
                iconColor = Color.Black
            )

        )
    }

    fun userHasChangedOption(selectedOption: Int) {
        _dataUiState.update {
            it?.copy(
                progressBarState = (selectedOption + 1).toDouble() / numbOptions
            )
        }
    }

    fun userHasTappedConfirmButton(context: Context, newValue: String) {
        PrefsUtils.setPref(context, chip.type.valuePrefKey, newValue)
        _navUiState.value = NavUiState.GoBackToHome(newValue)
    }

    fun firedNavState(){
        _navUiState.value = null
    }
}