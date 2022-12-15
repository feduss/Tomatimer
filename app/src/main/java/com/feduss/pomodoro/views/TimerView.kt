package com.feduss.pomodoro

import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.ButtonDefaults
import com.feduss.pomodoro.enums.ChipType
import com.feduss.pomodoro.models.Chip
import com.feduss.pomodoro.models.ChipListProvider


@Preview
@Composable
fun TimerView(@PreviewParameter(ChipListProvider::class) chips: List<Chip>,
              initialChipIndex: Int = 0,
              initialCycle: Int = 0,
              initialTimerSeconds: Int = 0,
              onTimerPausedOrStopped: (ChipType, Int?, Int?) -> Unit = { _, _, _ ->},
              onTimerStartedOrResumed: (ChipType, String, Int?) -> Int = { _, _, _ -> 0 },
              onBackToHome: (Boolean) -> Unit = {}) {
    val playIcon = ImageVector.vectorResource(id = R.drawable.ic_play_24dp)
    val pauseIcon = ImageVector.vectorResource(id = R.drawable.ic_pause_24dp)
    val activeColor = Color("#649e5d".toColorInt())
    val inactiveColor = Color("#a15757".toColorInt())

    var isAlertDialogVisible by remember {
        mutableStateOf(false)
    }

    //Number of the cycle of the tomato timer
    val totalCycles by remember(Unit) {
        mutableStateOf(chips[2].value.toInt())
    }

    //Current timer index type
    var currentChipIndex by remember(Unit) {
        mutableStateOf(initialChipIndex)
    }

    //Current timer
    val currentChip = chips[currentChipIndex]

    //Current tomato cycle
    var currentCycle by remember(Unit) {
        mutableStateOf(initialCycle)
    }

    //Timer state
    var isTimerActive by remember(currentChip.type) {
        mutableStateOf(true)
    }

    //Progress of the rounded progress bar
    var progress by remember(currentChip.type) {
        mutableStateOf(1.0)
    }

    //Progress color of the rounded progress bar
    var sliderColor by remember(currentChip.type) {
        mutableStateOf(activeColor)
    }

    //Upper title of the screen, that is the name of the timer
    val title by remember(currentChip.type) {
        mutableStateOf(currentChip.title)
    }

    //Middle label, that shows the minutes:seconds remaining
    var value by remember(currentChip.type) {
        val time = currentChip.value.toInt()
        val minutes: Int = time/60
        val seconds: Int = time%60
        mutableStateOf("$minutes:$seconds")
    }

    //Timer total seconds, that never changes, except for a change of timer type or status
    var maxTimerSeconds by remember(currentChip.type) {
        val seconds =
            if (initialTimerSeconds > 0)
                initialTimerSeconds else
                currentChip.value.toInt() * 60
        mutableStateOf(seconds)
    }

    //Timer seconds remaining, that changes every seconds when the timer is active
    //They could change alse if the chip type change, or it is edited after a resume
    var currentTimerSecondsRemaining by remember(currentChip.type){
        mutableStateOf(0)
    }

    //Icon image of the lower button
    var iconImage by remember(currentChip.type) {
        mutableStateOf(pauseIcon)
    }

    val timer by remember(currentChip.type, maxTimerSeconds) {
        val chipTimerSeconds = currentChip.value.toInt() * 60
        mutableStateOf(object : CountDownTimer(
            maxTimerSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTimerSecondsRemaining = (millisUntilFinished / 1000).toInt()
                val minutesRemaining = (currentTimerSecondsRemaining / 60)
                val secondsRemaining = (currentTimerSecondsRemaining % 60)

                val minutesString = if (minutesRemaining < 10) "0$minutesRemaining" else "$minutesRemaining"
                val secondsString = if (secondsRemaining < 10) "0$secondsRemaining" else "$secondsRemaining"

                value = "$minutesString:$secondsString"


                progress = (1f - (1f - currentTimerSecondsRemaining.toFloat().div(chipTimerSeconds))).toDouble()
            }

            override fun onFinish() {
                onTimerPausedOrStopped(currentChip.type, null, null)

                when (currentChip.type) {
                    ChipType.Tomato -> {
                        if (currentCycle == totalCycles - 1) {
                            currentChipIndex = ChipType.LongBreak.tag
                        } else {
                            currentChipIndex = ChipType.ShortBreak.tag
                        }
                    }
                    ChipType.ShortBreak -> {
                        currentChipIndex = ChipType.Tomato.tag
                        currentCycle += 1
                    }
                    ChipType.LongBreak -> {
                        cancel()
                        val removeBackgroundAlert = false
                        onBackToHome(removeBackgroundAlert)
                    }
                    else -> {}
                }
            }

        })
    }

    //Conditional timer auto-start/pause (change of timer type or timer state)
    LaunchedEffect(currentChip.type, isTimerActive) {
        if (isTimerActive) {
            timer.start()
        } else {
            timer.cancel()
        }
    }

    BackHandler() {
        isAlertDialogVisible = true
    }

    //For every new timer (when the chip type changed), set a new background alert
    LaunchedEffect(currentChip.type) {
        onTimerStartedOrResumed(currentChip.type, currentChip.title, maxTimerSeconds)
    }

    SwipeToDismissBox(onDismissed = { isAlertDialogVisible = true }) {
        if (isAlertDialogVisible) {
            //TODO: title and body are invisible --> TO FIX
            Alert(
                title = {
                    Text(
                        text = "Attenzione",
                        textAlign = TextAlign.Center
                    )
                },
                verticalArrangement = Arrangement.Center,
                negativeButton = {
                    val color = Color.DarkGray
                    CompactButton(
                        modifier = Modifier
                            .width(48.dp)
                            .aspectRatio(1f)
                            .background(
                                color = color,
                                shape = CircleShape
                            ),
                        colors = ButtonDefaults.primaryButtonColors(color, color),
                        content = {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    id = R.drawable.ic_close_24dp
                                ),
                                contentDescription = "Close icon",
                                tint = Color.White
                            )
                        },
                        onClick = {
                            isAlertDialogVisible = false
                        }
                    )
                },
                positiveButton = {
                    val color = Color("#E3BAFF".toColorInt())
                    CompactButton(
                        modifier = Modifier
                            .width(48.dp)
                            .aspectRatio(1f)
                            .background(
                                color = color,
                                shape = CircleShape
                            ),
                        colors = ButtonDefaults.primaryButtonColors(color, color),
                        content = {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    id = R.drawable.ic_check_24dp
                                ),
                                contentDescription = "Check icon",
                                tint = Color.Black
                            )
                        },
                        onClick = {
                            if (isTimerActive) {
                                val removeBackgroundAlert = true
                                onBackToHome(removeBackgroundAlert)
                            } else {
                                val removeBackgroundAlert = false
                                onTimerPausedOrStopped(currentChip.type, null, null)
                                onBackToHome(removeBackgroundAlert)
                            }
                        }
                    )
                }
            ){
                Text(
                    text = "Vuoi terminare il timer?",
                    textAlign = TextAlign.Center
                )
            }

        }
        else {
            CircularProgressIndicator(
                progress = progress.toFloat(),
                modifier = Modifier.fillMaxSize(),
                color = sliderColor,
                strokeWidth = 16.dp
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = Color("#E3BAFF".toColorInt()),
                    textAlign = TextAlign.Center
                )
                Text(
                    /*modifier = Modifier.weight(1f),*/
                    text = value,
                    color = Color("#E3BAFF".toColorInt()),
                    textAlign = TextAlign.Center
                )
                val color = Color("#E3BAFF".toColorInt())
                CompactButton(
                    modifier = Modifier
                        .width(48.dp)
                        .aspectRatio(1f)
                        .background(
                            color = color,
                            shape = CircleShape
                        ),
                    colors = ButtonDefaults.primaryButtonColors(color, color),
                    content = {
                        Icon(
                            imageVector = iconImage,
                            contentDescription = "ADD icon",
                            tint = Color.Black
                        )
                    },
                    onClick = {
                        iconImage = if (isTimerActive) playIcon else pauseIcon
                        sliderColor = if (isTimerActive) inactiveColor else activeColor

                        if (isTimerActive) {
                            //Cancel the background alert and save the seconds remaining
                            onTimerPausedOrStopped(
                                currentChip.type,
                                currentCycle,
                                currentTimerSecondsRemaining
                            )
                        } else {
                            //Set the background alert with the seconds remaining saved in shared prefs (null input)
                            maxTimerSeconds = onTimerStartedOrResumed(currentChip.type, currentChip.title, null)
                        }

                        isTimerActive = !isTimerActive
                    }
                )
            }
        }
    }
}