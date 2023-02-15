package com.feduss.tomato.views.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.*
import android.provider.Settings
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.wear.compose.material.SwipeToDismissBox
import com.feduss.tomato.MainViewController
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.utils.AlarmUtils
import com.feduss.tomato.utils.NotificationUtils
import com.feduss.tomato.utils.PrefsUtils


class NotificationViewController : AppCompatActivity() {

    private val viewModel: NotificationViewModel by viewModels()
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationUtils.removeOngoingNotification(context)
        PrefsUtils.setPref(context, PrefParamName.IsTimerActive.name, "false")

        val chipTitle = viewModel.getChipTitleFromPrefs(context)
        val currentCycle = viewModel.getCurrentCycleFromPrefs(context)
        val currentChipType = viewModel.getCurrentChipTypeFromPrefs(context)
        val chipType = ChipType.fromString(currentChipType)

        setContent {
            NotificationContent(chipType, currentCycle, chipTitle)
        }

        val dndStatus = Settings.Global.getInt(contentResolver, "zen_mode")

        /*
            0 - If DnD is off.
            1 - If DnD is on - Priority Only
            2 - If DnD is on - Total Silence
            3 - If DnD is on - Alarms Only
        */
        //If dnd is on, check ringerMode
        //Else, i.e. is active, vibrate
        if(dndStatus == 0) {

            val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            when (audio.ringerMode) {
                AudioManager.RINGER_MODE_NORMAL -> {
                    AlarmUtils.vibrate(context)
                    AlarmUtils.sound(context)
                }
                AudioManager.RINGER_MODE_SILENT -> {

                }
                AudioManager.RINGER_MODE_VIBRATE -> {
                    AlarmUtils.vibrate(context)
                }
            }
        } else {
            AlarmUtils.vibrate(context)
        }


    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    @Preview
    @OptIn(ExperimentalMaterial3Api::class)
    private fun NotificationContent(
        chipType: ChipType? = ChipType.Tomato,
        currentCycle: Int = 0,
        chipTitle: String = "Pomodoro"
    ) {
        BackHandler {
            handleBack(chipType, currentCycle)
        }

        MaterialTheme {
            val configuration = LocalConfiguration.current
            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp.dp

            val color = Color(("#E3BAFF".toColorInt()))

            val paddingValues = PaddingValues(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 8.dp
            )

            Scaffold(
                modifier = Modifier
                    .width(screenWidth)
                    .height(screenHeight)
            ) {
                SwipeToDismissBox(
                    onDismissed = {
                        handleBack(chipType, currentCycle)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp, 32.dp, 24.dp, 16.dp),
                        verticalArrangement = Arrangement.Top,
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
                                contentPadding = paddingValues,
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
                                contentPadding = paddingValues,
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
                                contentPadding = paddingValues,
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

    private fun goToNextTimer(chipType: ChipType?, currentCycle: Int) {
        viewModel.setNextTimerInPrefs(context, chipType, currentCycle)

        val appIntent = Intent(context, MainViewController::class.java)
        startApp(appIntent)
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
        startApp(appIntent)

        finish()
    }

    private fun startApp(appIntent: Intent) {
        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(appIntent)
    }
}