package com.feduss.pomodoro

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val chips = ArrayList<Chip>(
        listOf(
            Chip("Pomodoro", "25", "min"),
            Chip("P. breve", "5", "min"),
            Chip("Num. cicli", "4"),
            Chip("P. lunga", "25", "min")
        ))
}