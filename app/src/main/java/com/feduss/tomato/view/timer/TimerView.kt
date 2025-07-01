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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomatimer.entity.enums.AlertType
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomato.R
import com.feduss.tomato.uistate.extension.PurpleCustom
import com.feduss.tomato.uistate.viewmodel.timer.TimerViewModel


@Preview
@Composable
fun TimerView(
    context: Context = LocalContext.current,
    navController: NavHostController = rememberSwipeDismissableNavController(),
    viewModel: TimerViewModel = hiltViewModel(),
    onTimerSet: (String) -> Unit = {},
    openNotification: () -> Unit = {},
) {

    val dataUiState by viewModel.dataUiState.collectAsState()
    val navUiState by viewModel.navUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initUiState(context)
    }


    navUiState?.let { state ->
        when(state) {
            TimerViewModel.NavUiState.GoBackToHome -> {
                backToHome(context, viewModel, navController)
                viewModel.changeAlertDialogStatus(
                    context = context,
                    alertType = null
                )
            }
            is TimerViewModel.NavUiState.GoToNextTimer -> {
                goToNextTimer(
                    context = context,
                    viewModel = viewModel,
                    chipType = state.currentChipType,
                    currentCycle = state.currentCycle,
                    navController = navController
                )
                viewModel.changeAlertDialogStatus(
                    context = context,
                    alertType = null
                )
            }
        }
    }

    dataUiState?.let { state ->

        var timer: CountDownTimer? by remember {
            mutableStateOf(null)
        }

        LaunchedEffect(
            state.currentChip.type,
            state.isTimerActive
        ) {
            if (state.isTimerActive) {
                timer = object : CountDownTimer(
                    state.maxTimerSeconds * 1000L, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val currentTimerSecondsRemaining = (millisUntilFinished / 1000).toInt()
                        val minutesRemaining = (currentTimerSecondsRemaining / 60)
                        val secondsRemaining = (currentTimerSecondsRemaining % 60)

                        val minutesString = if (minutesRemaining < 10) "0$minutesRemaining" else "$minutesRemaining"
                        val secondsString = if (secondsRemaining < 10) "0$secondsRemaining" else "$secondsRemaining"

                        val middleText = "$minutesString:$secondsString"


                        val chipTimerSeconds = state.currentChip.value.toInt() * 60
                        val progress = (1f - (1f - currentTimerSecondsRemaining.toFloat().div(chipTimerSeconds))).toDouble()

                        viewModel.updateStatus(
                            newMiddleText = middleText,
                            newProgress = progress,
                            currentTimerSecondsRemaining = currentTimerSecondsRemaining
                        )

                        viewModel.saveCurrentTimerData(
                            context,
                            currentChip = state.currentChip,
                            currentCycle = state.currentCycleUiState.value,
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

                }
                timer?.start()
            } else {
                timer?.cancel()
            }

        }

        val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

        onTimerSet(state.timeText)

        val swipeBackClosure = {
            viewModel.changeAlertDialogStatus(
                context = context,
                alertType = if (state.isAlertDialogVisible ) AlertType.StopTimer else null
            )
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
            val alertDialogUiState = state.alertDialogUiState
            if (state.isAlertDialogVisible && alertDialogUiState != null) {
                onTimerSet("")

                val resumeTimer = {
                    viewModel.changeAlertDialogStatus(
                        context = context,
                        alertType = null
                    )
                }

                AlertDialog(
                    titleId = alertDialogUiState.titleId,
                    negativeButtonIconId = alertDialogUiState.negativeButtonIconId,
                    negativeButtonIconDesc = alertDialogUiState.negativeButtonIconDesc,
                    negativeButtonClicked = resumeTimer,
                    positiveButtonIconId = alertDialogUiState.positiveButtonIconId,
                    positiveButtonIconDesc = alertDialogUiState.positiveButtonIconDesc,
                    positiveButtonClicked = {
                        when(alertDialogUiState.alertType) {
                            AlertType.StopTimer -> {
                                viewModel.userStopTimer()
                            }
                            AlertType.SkipTimer -> {
                                viewModel.userSkipTimer(
                                    currentChipType = state.currentChip.type,
                                    currentCycle = state.currentCycleUiState.value
                                )
                            }
                        }
                    }
                )

            }
            else {
                CircularProgressIndicator(
                    progress = { state.progressBarValue.toFloat() },
                    modifier = Modifier.fillMaxSize(),
                    color = state.progressBarColor,
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
                        text = state.titleUiState.text,
                        color = state.titleUiState.color,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = state.currentCycleUiState.textUiState.text,
                        color = state.currentCycleUiState.textUiState.color,
                        textAlign = TextAlign.Center,
                        fontSize = TextUnit(10f, TextUnitType.Sp)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        text = state.middleTextUiState.text,
                        color = state.middleTextUiState.color,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                    ) {
                        val bottomLeftButtonColor = state.bottomLeftButtonUiState.backgroundColor
                        CompactButton(
                            modifier = Modifier
                                .width(24.dp)
                                .aspectRatio(1f)
                                .background(
                                    color = bottomLeftButtonColor,
                                    shape = CircleShape
                                ),
                            colors = ButtonDefaults.primaryButtonColors(bottomLeftButtonColor, bottomLeftButtonColor),
                            content = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = state.bottomLeftButtonUiState.iconId),
                                    contentDescription = state.bottomLeftButtonUiState.iconDescription,
                                    tint = state.bottomLeftButtonUiState.iconColor
                                )
                            },
                            onClick = {
                                viewModel.setTimerState(
                                    context = context,
                                    isTimerActive = !state.isTimerActive
                                )
                            }
                        )
                        if (!state.isLastTimer) {
                            val bottomRightButtonColor = state.bottomRightButtonUiState.backgroundColor
                            CompactButton(
                                modifier = Modifier
                                    .width(24.dp)
                                    .aspectRatio(1f)
                                    .background(
                                        color = bottomRightButtonColor,
                                        shape = CircleShape
                                    ),
                                colors = ButtonDefaults.primaryButtonColors(bottomRightButtonColor, bottomRightButtonColor),
                                content = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = state.bottomRightButtonUiState.iconId),
                                        contentDescription = state.bottomRightButtonUiState.iconDescription,
                                        tint = state.bottomRightButtonUiState.iconColor
                                    )
                                },
                                onClick = {
                                    viewModel.changeAlertDialogStatus(
                                        context = context,
                                        alertType = AlertType.SkipTimer
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertDialog(
    titleId: Int,
    negativeButtonIconId: Int,
    negativeButtonIconDesc: String,
    negativeButtonClicked: () -> Unit,
    positiveButtonIconId: Int,
    positiveButtonIconDesc: String,
    positiveButtonClicked: () -> Unit
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
            val color = Color.PurpleCustom
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