package com.feduss.tomato.view

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.entity.enums.Section
import com.feduss.tomatimer.utils.AlarmUtils
import com.feduss.tomatimer.utils.NotificationUtils
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.R
import com.feduss.tomato.receivers.TimerReceiver
import com.feduss.tomato.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainViewController : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavHostController
    private lateinit var context: Context
    private val timerReceiver = TimerReceiver()

    private lateinit var notificationManager: NotificationManager

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()
        super.onCreate(savedInstanceState)

        context = this

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val filter = IntentFilter()
        registerReceiver(timerReceiver, filter)

        if(intent.getBooleanExtra(Consts.FromOngoingNotification.value, false)) {
            NotificationUtils.restoreTimerSecondsFromOngoingNotification(context)
        }

        setContent {
            MaterialTheme {
                navController = rememberSwipeDismissableNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainActivity(
                        context = context,
                        activity = this,
                        navController = navController,
                        chips = viewModel.getChips(context),
                        startDestination = Section.Setup.baseRoute
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        val isTimerActive = PrefsUtils.getPref(context, PrefParamName.IsTimerActive.name) == "true"

        if (isTimerActive) {
            AlarmUtils.setBackgroundAlert(context, TimerReceiver::class.java)
            NotificationUtils.setOngoingNotification(context, iconId = R.drawable.ic_app)
        }
    }

    override fun onResume() {
        super.onResume()
        AlarmUtils.removeBackgroundAlert(context, TimerReceiver::class.java)
        NotificationUtils.removeOngoingNotification(context)
    }

    override fun onDestroy() {
        unregisterReceiver(timerReceiver)
        super.onDestroy()
    }
}