package com.feduss.pomodoro

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.pomodoro.receivers.TimerReceiver


class MainActivityViewController : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavHostController

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val activity = this
        val timerReceiver = TimerReceiver()
        val filter = IntentFilter()
        registerReceiver(timerReceiver, filter)

        setContent {
            MaterialTheme() {
                navController = rememberSwipeDismissableNavController()
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp
                Scaffold(
                    modifier = Modifier
                        .width(screenWidth)
                        .height(screenHeight)) {
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