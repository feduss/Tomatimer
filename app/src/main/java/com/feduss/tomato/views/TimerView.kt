package com.feduss.tomato.views

import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.ButtonDefaults
import com.feduss.tomato.R
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.models.Chip
import com.feduss.tomato.models.ChipListProvider


@OptIn(ExperimentalUnitApi::class)
@Preview
@Composable
fun TimerView(@PreviewParameter(ChipListProvider::class) chips: List<Chip>,
              initialChipIndex: Int = 0,
              initialCycle: Int = 0,
              initialTimerSeconds: Int = 0,
              onSaveCurrentTimerData: (ChipType, String?, Int?, Int?) -> Unit = { _, _, _, _ ->},
              onLoadTimerSecondsRemainings: () -> Int = { 0 },
              onSetTimerState: (Boolean) -> Unit = {},
              onBackToHome: () -> Unit = {}) {
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

                onSaveCurrentTimerData(
                    currentChip.type, //chipType
                    currentChip.title, //timerTitle
                    currentCycle, //currentCycle
                    currentTimerSecondsRemaining //secondsRemaining
                )
            }

            override fun onFinish() {

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
                        onSaveCurrentTimerData(
                            currentChip.type, //chipType
                            null, //timerTitle
                            null, //currentCycle
                            null //secondsRemaining
                        )
                        onBackToHome()
                    }
                    else -> {}
                }
            }

        })
    }

    //Conditional timer auto-start/pause (change of timer type or timer state)
    LaunchedEffect(currentChip.type, isTimerActive) {
        onSetTimerState(isTimerActive)
        if (isTimerActive) {
            timer.start()
        } else {
            timer.cancel()
        }
    }

    BackHandler() {
        isAlertDialogVisible = true
    }

    SwipeToDismissBox(onDismissed = { isAlertDialogVisible = true }) {
        if (isAlertDialogVisible) {
            isTimerActive = false
            Alert(
                title = {
                    Text(
                        text = "Vuoi terminare il timer?",
                        textAlign = TextAlign.Center,
                        color = Color.White
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
                            maxTimerSeconds = onLoadTimerSecondsRemainings()
                            isAlertDialogVisible = false
                            isTimerActive = true
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
                            onSaveCurrentTimerData(
                                currentChip.type, //chipType
                                null, //timerTitle
                                null, //currentCycle
                                null //secondsRemaining
                            )
                            onBackToHome()
                        }
                    )
                }
            )

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
                Column(
                    modifier = Modifier
                    .padding(4.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Text(
                        text = title,
                        color = Color("#E3BAFF".toColorInt()),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ciclo ${currentCycle + 1}/${totalCycles}",
                        color = Color("#E3BAFF".toColorInt()),
                        textAlign = TextAlign.Center,
                        fontSize = TextUnit(10f, TextUnitType.Sp)
                    )
                }
                Text(
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

                        if (!isTimerActive) {
                            //Restore the paused timer with the remaining seconds
                            maxTimerSeconds = onLoadTimerSecondsRemainings()
                        }

                        isTimerActive = !isTimerActive
                        onSetTimerState(isTimerActive)
                    }
                )
            }
        }
    }
}