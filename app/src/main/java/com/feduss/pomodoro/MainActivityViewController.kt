package com.feduss.pomodoro

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

    private val requestCode = 23
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

    fun setBackgroundAlert(timerMillisRemaining: Long, millisSince1970: Long) {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val broadcastReceiverIntent = Intent(this, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, broadcastReceiverIntent, 0)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            timerMillisRemaining + millisSince1970,
            pendingIntent
        )
    }

    fun removeBackgroundAlert() {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val broadcastReceiverIntent = Intent(this, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, broadcastReceiverIntent, 0)
        alarmManager.cancel(pendingIntent)
    }
}