package com.feduss.tomato.viewmodel.edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.viewmodel.MainViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

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

    fun setNewValue(context: Context, type: ChipType, newValue: String) {
        PrefsUtils.setPref(context, type.valuePrefKey, newValue)
    }
}