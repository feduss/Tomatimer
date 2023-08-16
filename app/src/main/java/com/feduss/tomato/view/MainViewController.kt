package com.feduss.tomato.view

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomatimer.entity.enums.PrefParamName
import com.feduss.tomatimer.entity.enums.Section
import com.feduss.tomato.receivers.TimerReceiver
import com.feduss.tomatimer.utils.AlarmUtils
import com.feduss.tomatimer.utils.NotificationUtils
import com.feduss.tomatimer.utils.PrefsUtils
import com.feduss.tomato.R
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

        checkIfFromNotification(context, intent)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val filter = IntentFilter()
        registerReceiver(timerReceiver, filter)

        if(intent.getBooleanExtra(Consts.FromOngoingNotification.value, false)) {
            NotificationUtils.restoreTimerSecondsFromOngoingNotification(context)
        }

        val color = Color(("#E3BAFF".toColorInt()))

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

    private fun checkIfFromNotification(context: Context, intent: Intent?) {
        when(intent?.getStringExtra("notificationAction")) {
            "nextTimer" -> {
                viewModel.setNextTimerInPrefs(context)
            }

            "cancelQueue" -> {
                viewModel.cancelTimerInPrefs(context)
                NotificationUtils.removeOngoingNotification(context)
            }

            else -> {

            }
        }
    }

    @Composable
    private fun TextButtonView(color: Color, text: String, onButtonClicked: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp, 32.dp, 8.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    color = Color("#E3BAFF".toColorInt()),
                    textAlign = TextAlign.Center
                )
                Button(
                    colors = ButtonDefaults.buttonColors(color, color),
                    content = {
                        Text(
                            text = stringResource(R.string.continue_button),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        onButtonClicked()
                    }
                )
            }
        }
    }

    private fun requestOverlayPermission() {
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
        )
    }

    private fun isOverlayPermissionGranted() = Settings.canDrawOverlays(this)

    override fun onPause() {
        super.onPause()

        val isTimerActive = PrefsUtils.getPref(context, PrefParamName.IsTimerActive.name) == "true"

        if (isTimerActive) {
            AlarmUtils.setBackgroundAlert(context, TimerReceiver::class.java)
            NotificationUtils.setOngoingNotification(context, iconId = R.drawable.ic_notification_ongoing)
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