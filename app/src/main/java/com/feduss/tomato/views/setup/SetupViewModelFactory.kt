package com.feduss.tomato.views.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feduss.tomato.models.Chip

@Suppress("UNCHECKED_CAST")
class SetupViewModelFactory(private val chips: List<Chip>) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetupViewModel::class.java))
            return SetupViewModel(chips) as T
        throw IllegalArgumentException("Unknown Viewmodel class")
    }
}