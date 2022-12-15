package com.feduss.pomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController


class MainActivityViewController : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val activity = this
        setContent {
            MaterialTheme() {
                navController = rememberSwipeDismissableNavController()
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp
                Box(
                    modifier = Modifier.width(screenWidth).height(screenHeight)) {
                    MainActivity(
                        navController = navController,
                        activity = activity,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}