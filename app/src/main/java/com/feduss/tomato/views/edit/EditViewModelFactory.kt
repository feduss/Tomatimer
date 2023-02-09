package com.feduss.tomato.views.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feduss.tomato.models.Chip

@Suppress("UNCHECKED_CAST")
class EditViewModelFactory(private val chip: Chip) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditViewModel::class.java))
            return EditViewModel(chip) as T
        throw IllegalArgumentException("Unknown Viewmodel class")
    }
}