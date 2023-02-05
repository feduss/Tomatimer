package com.feduss.tomatimer

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomatimer.enums.PrefParamName
import com.feduss.tomatimer.receivers.NotificationReceiver
import com.feduss.tomatimer.receivers.TimerReceiver
import com.feduss.tomatimer.utils.AlarmUtils
import com.feduss.tomatimer.utils.NotificationUtils
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomatimer.views.MainActivity


class MainActivityViewController : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavHostController
    private lateinit var context: Context
    private val timerReceiver = TimerReceiver()
    private val notificationReceiver = NotificationReceiver()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val filter = IntentFilter()
        registerReceiver(timerReceiver, filter)
        registerReceiver(notificationReceiver, filter)
        context = this

        val permissionGranted = MutableLiveData(true)

        val requestPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.forEach { permission ->
                val state = if (permission.value) "granted" else "not granted"
                Log.e("TOMATO:", "${permission.key} is $state ");

                if (!permission.value) {
                    permissionGranted.postValue(false)
                    return@forEach
                }
            }

            val arePermissionsGranted = permissionGranted.value ?: false
            permissionGranted.postValue(arePermissionsGranted)
        }

        val array = Array(5) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.POST_NOTIFICATIONS
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                android.Manifest.permission.SCHEDULE_EXACT_ALARM
            }
            android.Manifest.permission.WAKE_LOCK
            android.Manifest.permission.FOREGROUND_SERVICE
        }

        requestPermission.launch(array)

        setContent {
            MaterialTheme {
                navController = rememberSwipeDismissableNavController()
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp

                val arePermissionsGranted by permissionGranted.observeAsState()

                Scaffold(
                    modifier = Modifier
                        .width(screenWidth)
                        .height(screenHeight)) {
                    if (arePermissionsGranted == true) {
                        MainActivity(
                            navController = navController,
                            context = context,
                            viewController = this,
                            viewModel = viewModel
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Per continuare, devi accettare tutti i permessi. Quindi, chiudi e riavvia l'app.",
                                color = Color("#E3BAFF".toColorInt()),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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
            Log.e("TOMATO:", "Alarm and notification enabled")
        }
    }

    override fun onResume() {
        super.onResume()

        AlarmUtils.removeBackgroundAlert(context)
        NotificationUtils.removeNotification(context)

        Log.e("TOMATO:", "Alarm and notification removed")
    }

    override fun onDestroy() {
        unregisterReceiver(timerReceiver)
        unregisterReceiver(notificationReceiver)
        super.onDestroy()
    }
}