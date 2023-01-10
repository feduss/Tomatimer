package com.feduss.tomato

import android.annotation.SuppressLint
import android.content.Context
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
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.receivers.TimerReceiver
import com.feduss.tomato.utils.AlarmUtils
import com.feduss.tomato.utils.NotificationUtils
import com.feduss.tomato.utils.PrefsUtils
import com.feduss.tomato.views.MainActivity


class MainActivityViewController : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavHostController
    private lateinit var context: Context

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val timerReceiver = TimerReceiver()
        val filter = IntentFilter()
        registerReceiver(timerReceiver, filter)
        context = this

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
                        context = context,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        val isTimerActive = PrefsUtils.getPref(context, PrefParamName.IsTimerActive.name) == "true"

        if (isTimerActive) {
            AlarmUtils.setBackgroundAlert(context)
            NotificationUtils.setNotification(context)
        }
    }

    override fun onResume() {
        super.onResume()

        AlarmUtils.removeBackgroundAlert(context)
        NotificationUtils.removeNotification(context)
    }
}