package com.feduss.tomato.views.timer

import android.content.Context
import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomato.R
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.enums.Consts
import com.feduss.tomato.enums.Section
import com.feduss.tomato.provider.ChipDatas


@OptIn(ExperimentalUnitApi::class)
@Preview
@Composable
fun TimerView(context: Context = LocalContext.current,
              navController: NavHostController = rememberSwipeDismissableNavController(),
              viewModel: TimerViewModel = TimerViewModel(ChipDatas.demoList),
              openNotification: () -> Unit = {}
) {
    val playIcon = ImageVector.vectorResource(id = R.drawable.ic_play_24dp)
    val pauseIcon = ImageVector.vectorResource(id = R.drawable.ic_pause_24dp)
    val activeColor = Color("#649e5d".toColorInt())
    val inactiveColor = Color("#a15757".toColorInt())

    var isAlertDialogVisible by remember {
        mutableStateOf(false)
    }

    //Number of the cycle of the tomato timer
    val totalCycles by remember(Unit) {
        mutableStateOf(viewModel.totalCycles)
    }

    //Current timer index type
    var currentChipIndex by remember(Unit) {
        mutableStateOf(viewModel.initialChipIndex)
    }

    //Current timer
    val currentChip = viewModel.chips[currentChipIndex]

    //Current tomato cycle
    var currentCycle by remember(Unit) {
        mutableStateOf(viewModel.initialCycle)
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
            if (viewModel.initialTimerSeconds > 0)
                viewModel.initialTimerSeconds else
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

    val timer by remember(currentChip.type, 5000/*maxTimerSeconds*/) {
        val chipTimerSeconds = currentChip.value.toInt() * 60
        mutableStateOf(object : CountDownTimer(
            /*maxTimerSeconds*/5 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTimerSecondsRemaining = (millisUntilFinished / 1000).toInt()
                val minutesRemaining = (currentTimerSecondsRemaining / 60)
                val secondsRemaining = (currentTimerSecondsRemaining % 60)

                val minutesString = if (minutesRemaining < 10) "0$minutesRemaining" else "$minutesRemaining"
                val secondsString = if (secondsRemaining < 10) "0$secondsRemaining" else "$secondsRemaining"

                value = "$minutesString:$secondsString"


                progress = (1f - (1f - currentTimerSecondsRemaining.toFloat().div(chipTimerSeconds))).toDouble()

                viewModel.saveCurrentTimerData(
                    context,
                    chipType = currentChip.type,
                    currentTimerName = currentChip.title,
                    currentCycle = currentCycle,
                    secondsRemaining = currentTimerSecondsRemaining
                )
            }

            override fun onFinish() {
                setTimerExpired(
                    context,
                    viewModel,
                    navController,
                    openNotification
                )
            }

        })
    }

    //Conditional timer auto-start/pause (change of timer type or timer state)
    LaunchedEffect(currentChip.type, isTimerActive) {
        viewModel.setTimerState(context, isTimerActive = isTimerActive)
        if (isTimerActive) {
            timer.start()
        } else {
            timer.cancel()
        }
    }

    BackHandler() {
        isAlertDialogVisible = !isAlertDialogVisible

        if (!isAlertDialogVisible) {
            maxTimerSeconds = viewModel.loadTimerSecondsRemainings(context)
            isTimerActive = true
        }
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
                            maxTimerSeconds = viewModel.loadTimerSecondsRemainings(context)
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
                            backToHome(context, viewModel, navController)
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
                strokeWidth = 8.dp
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
                            maxTimerSeconds = viewModel.loadTimerSecondsRemainings(context)
                        }

                        isTimerActive = !isTimerActive
                        viewModel.setTimerState(context, isTimerActive = isTimerActive)
                    }
                )
            }
        }
    }
}

fun backToHome(context: Context, viewModel: TimerViewModel, navController: NavHostController){
    viewModel.cancelTimer(context)
    navController.popBackStack()
}

fun setTimerExpired(context: Context, viewModel: TimerViewModel, navController: NavHostController, openNotification: () -> Unit) {
    viewModel.setTimerState(context, false)
    openNotification()
}