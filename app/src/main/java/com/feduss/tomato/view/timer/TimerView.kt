package com.feduss.tomato.view.timer

import android.content.Context
import android.os.CountDownTimer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomato.R
import com.feduss.tomato.viewmodel.timer.TimerViewModel
import java.util.Calendar

enum class AlertType {
    StopTimer,
    SkipTimer
}


@Preview
@Composable
fun TimerView(
    context: Context = LocalContext.current,
    navController: NavHostController = rememberSwipeDismissableNavController(),
    viewModel: TimerViewModel = hiltViewModel(),
    onTimerSet: (String) -> Unit = {},
    openNotification: () -> Unit = {},
) {
    val playIcon = ImageVector.vectorResource(id = R.drawable.ic_play_24dp)
    val pauseIcon = ImageVector.vectorResource(id = R.drawable.ic_pause_24dp)
    val skipIcon = ImageVector.vectorResource(id = R.drawable.ic_skip_next)
    val activeColor = Color("#649e5d".toColorInt())
    val inactiveColor = Color("#a15757".toColorInt())

    var isAlertDialogVisible by remember {
        mutableStateOf(false)
    }

    var alertType: AlertType? by remember {
        mutableStateOf(null)
    }

    //Number of the cycle of the tomato timer
    val totalCycles = viewModel.totalCycles

    //Current timer index type
    val currentChipIndex = viewModel.initialChipIndex

    //Current timer
    val currentChip = viewModel.chips[currentChipIndex]

    //Current tomato cycle
    val currentCycle = viewModel.initialCycle

    //Timer state
    var isTimerActive by remember {
        mutableStateOf(true)
    }

    //Progress of the rounded progress bar
    var progress by remember {
        mutableDoubleStateOf(1.0)
    }

    //Progress color of the rounded progress bar
    var sliderColor by remember {
        mutableStateOf(activeColor)
    }

    //Upper title of the screen, that is the name of the timer
    val title by remember {
        mutableStateOf(currentChip.fullTitle)
    }

    //Middle label, that shows the minutes:seconds remaining
    var value by remember {
        val time = currentChip.value.toInt()
        val minutes: Int = time/60
        val seconds: Int = time%60
        mutableStateOf("$minutes:$seconds")
    }

    //Timer total seconds, that never changes, except for a change of timer type or status
    var maxTimerSeconds by remember {
        val seconds =
            if (viewModel.initialTimerSeconds > 0)
                viewModel.initialTimerSeconds else
                currentChip.value.toInt() * 60
        mutableIntStateOf(seconds)
    }

    //Timer seconds remaining, that changes every seconds when the timer is active
    //They could change also if the chip type change, or it is edited after a resume
    var currentTimerSecondsRemaining by remember{
        mutableIntStateOf(0)
    }

    //Icon image of the lower button
    var iconImage by remember {
        mutableStateOf(pauseIcon)
    }

    val appWasOnPause by remember {
        mutableStateOf(false)
    }

    val chipTimerSeconds = currentChip.value.toInt() * 60
    val timer by remember(currentChip.type, maxTimerSeconds, appWasOnPause) {
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

                viewModel.saveCurrentTimerData(
                    context,
                    chipType = currentChip.type,
                    currentTimerName = currentChip.fullTitle,
                    currentCycle = currentCycle,
                    secondsRemaining = currentTimerSecondsRemaining
                )
            }
            override fun onFinish() {
                setTimerExpired(
                    context,
                    viewModel,
                    openNotification
                )
            }

        }.start())
    }

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

    updateTimeText(currentTimerSecondsRemaining, onTimerSet)
    viewModel.setTimerState(context, isTimerActive = true)

    val swipeBackClosure = {
        isAlertDialogVisible = !isAlertDialogVisible

        if (isAlertDialogVisible) {
            alertType = AlertType.StopTimer
        } else {
            alertType = null
            maxTimerSeconds = viewModel.loadTimerSecondsRemainings(context)
            isTimerActive = true
        }
    }

    BackHandler {
        userSwippedBack(swipeBackClosure)
    }

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        onDismissed = {
            userSwippedBack(swipeBackClosure)
        }
    ) {
        if (isAlertDialogVisible) {
            onTimerSet("")
            timer.cancel()
            isTimerActive = false

            val resumeTimer = {
                maxTimerSeconds = viewModel.loadTimerSecondsRemainings(context)
                isAlertDialogVisible = false
                isTimerActive = true
                updateTimeText(currentTimerSecondsRemaining, onTimerSet)
                alertType = null
            }

            when(alertType) {
                AlertType.SkipTimer -> {
                    AlertDialog(
                        titleId = R.string.skip_timer,
                        negativeButtonIconId = R.drawable.ic_close_24dp,
                        negativeButtonIconDesc = "Close icon",
                        negativeButtonClicked = resumeTimer,
                        positiveButtonIconId = R.drawable.ic_check_24dp,
                        positiveButtonIconDesc = "Check icon",
                        positiveButtonClicked = {
                            goToNextTimer(
                                context = context,
                                viewModel = viewModel,
                                chipType = currentChip.type,
                                currentCycle = currentCycle,
                                navController = navController
                            )
                            alertType = null
                        }
                    )
                }

                AlertType.StopTimer -> {
                    AlertDialog(
                        titleId = R.string.stop_timer_question,
                        negativeButtonIconId = R.drawable.ic_close_24dp,
                        negativeButtonIconDesc = "Close icon",
                        negativeButtonClicked = resumeTimer,
                        positiveButtonIconId = R.drawable.ic_check_24dp,
                        positiveButtonIconDesc = "Check icon",
                        positiveButtonClicked = {
                            backToHome(context, viewModel, navController)
                            alertType = null
                        }
                    )
                }

                null -> {}
            }

        }
        else {
            CircularProgressIndicator(
                progress = { progress.toFloat() },
                modifier = Modifier.fillMaxSize(),
                color = sliderColor,
                trackColor = Color.Transparent,
                strokeWidth = 4.dp,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp, bottom = 32.dp, start = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = Color("#E3BAFF".toColorInt()),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.cycle_name, (currentCycle + 1), totalCycles),
                    color = Color("#E3BAFF".toColorInt()),
                    textAlign = TextAlign.Center,
                    fontSize = TextUnit(10f, TextUnitType.Sp)
                )
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    text = value,
                    color = Color("#E3BAFF".toColorInt()),
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    val color = Color("#E3BAFF".toColorInt())
                    CompactButton(
                        modifier = Modifier
                            .width(24.dp)
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

                            isTimerActive = !isTimerActive
                            viewModel.setTimerState(context, isTimerActive = isTimerActive)

                            if (isTimerActive) {
                                //Restore the paused timer with the remaining seconds
                                maxTimerSeconds = viewModel.loadTimerSecondsRemainings(context)
                                updateTimeText(maxTimerSeconds, onTimerSet)
                            } else {
                                timer.cancel()
                            }
                        }
                    )
                    val isLastTimer = currentChip.type == ChipType.LongBreak && currentCycle == totalCycles - 1
                    if (!isLastTimer) {
                        CompactButton(
                            modifier = Modifier
                                .width(24.dp)
                                .aspectRatio(1f)
                                .background(
                                    color = color,
                                    shape = CircleShape
                                ),
                            colors = ButtonDefaults.primaryButtonColors(color, color),
                            content = {
                                Icon(
                                    imageVector = skipIcon,
                                    contentDescription = "SKIP icon",
                                    tint = Color.Black
                                )
                            },
                            onClick = {
                                alertType = AlertType.SkipTimer
                                isAlertDialogVisible = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertDialog(
    titleId: Int,
    negativeButtonIconId: Int, negativeButtonIconDesc: String, negativeButtonClicked: () -> Unit,
    positiveButtonIconId: Int, positiveButtonIconDesc: String, positiveButtonClicked: () -> Unit
) {
    Alert(
        title = {
            Text(
                text = stringResource(titleId),
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
                            id = negativeButtonIconId
                        ),
                        contentDescription = negativeButtonIconDesc,
                        tint = Color.White
                    )
                },
                onClick = negativeButtonClicked
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
                            id = positiveButtonIconId
                        ),
                        contentDescription = positiveButtonIconDesc,
                        tint = Color.Black
                    )
                },
                onClick = positiveButtonClicked
            )
        }
    )
}

private fun updateTimeText(
    secondsRemaining: Int,
    onTimerSet: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.SECOND, secondsRemaining)

    val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
    val calendarMinutes = calendar.get(Calendar.MINUTE)
    val minutes = if (calendarMinutes < 10) "0$calendarMinutes" else calendarMinutes
    onTimerSet("$calendarHour:$minutes")
}

private fun userSwippedBack(swipeBackClosure: () -> Unit) {
    swipeBackClosure()
}

fun backToHome(context: Context, viewModel: TimerViewModel, navController: NavHostController){
    viewModel.cancelTimer(context)
    navController.popBackStack()
}

fun setTimerExpired(context: Context, viewModel: TimerViewModel, openNotification: () -> Unit) {
    viewModel.setTimerState(context, false)
    openNotification()
}

private fun goToNextTimer(context: Context, viewModel: TimerViewModel, chipType: ChipType?, currentCycle: Int, navController: NavHostController) {
    viewModel.setNextTimerInPrefs(context, chipType, currentCycle)
    navController.popBackStack()
}