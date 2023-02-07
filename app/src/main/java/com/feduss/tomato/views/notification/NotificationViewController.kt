package com.feduss.tomato.views.notification

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.wear.compose.material.SwipeToDismissBox
import com.feduss.tomato.MainViewController
import com.feduss.tomato.MainViewModel
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.utils.AlarmUtils
import com.feduss.tomato.utils.NotificationUtils
import com.feduss.tomato.utils.PrefsUtils

class NotificationViewController : AppCompatActivity() {

    private val viewModel: NotificationViewModel by viewModels()
    private val context = this

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationUtils.removeOngoingNotification(context)
        PrefsUtils.setPref(context, PrefParamName.IsTimerActive.name, "false")

        AlarmUtils.vibrate(context)

        val chipTitle = viewModel.getChipTitleFromPrefs(context)
        val currentCycle = viewModel.getCurrentCycleFromPrefs(context)
        val currentChipType = viewModel.getCurrentChipTypeFromPrefs(context)
        val chipType = ChipType.fromString(currentChipType)



        setContent {
            BackHandler() {
                handleBack(chipType, currentCycle)
            }

            MaterialTheme {
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp

                val color = Color(("#E3BAFF".toColorInt()))
                Scaffold(
                    modifier = Modifier
                        .width(screenWidth)
                        .height(screenHeight)) {
                    SwipeToDismissBox(onDismissed = {
                        handleBack(chipType, currentCycle)
                    }) {
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
                                    .padding(24.dp, 32.dp, 24.dp, 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Timer $chipTitle (ciclo ${currentCycle + 1}) scaduto!",
                                    color = color,
                                    textAlign = TextAlign.Center
                                )
                                if (chipType == ChipType.LongBreak) {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(color, color),
                                        content = {
                                            Text(
                                                text = "Apri app",
                                                color = Color.Black,
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        onClick = {
                                            cancelQueueAndOpenApp()
                                        }
                                    )
                                } else {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(color, color),
                                        content = {
                                            Text(
                                                text = "Prossimo timer",
                                                color = Color.Black,
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        onClick = {
                                            goToNextTimer(chipType, currentCycle)
                                        }
                                    )
                                    Button(
                                        colors = ButtonDefaults.buttonColors(color, color),
                                        content = {
                                            Text(
                                                text = "Cancella coda",
                                                color = Color.Black,
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        onClick = {
                                            cancelQueueAndOpenApp()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun goToNextTimer(chipType: ChipType?, currentCycle: Int) {
        viewModel.setNextTimerInPrefs(context, chipType, currentCycle)

        val appIntent =
            Intent(context, MainViewController::class.java)
        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(appIntent)

        Log.e(
            "TOMATO:",
            "Open app and go to next timer from notification activity"
        )

        finish()
    }

    private fun handleBack(chipType: ChipType?, currentCycle: Int) {
        if (chipType == ChipType.LongBreak) {
            PrefsUtils.cancelTimer(context)
        } else {
            PrefsUtils.setNextTimer(context, chipType, currentCycle)
            finish()
        }
    }

    private fun cancelQueueAndOpenApp() {
        viewModel.cancelTimerInPrefs(context)
        NotificationUtils.removeOngoingNotification(context)

        val appIntent = Intent(context, MainViewController::class.java)
        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(appIntent)

        Log.e("TOMATO:", "Cancel timers queue from notification activity")

        finish()
    }
}