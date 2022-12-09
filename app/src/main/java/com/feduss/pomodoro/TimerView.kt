package com.feduss.pomodoro

import android.os.CountDownTimer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt


@Preview
@Composable
fun TimerView(@PreviewParameter(ChipListProvider::class) chips: List<Chip>,
              onTimerPausedOrStopped: (ChipType, Int?) -> Unit = { _, _ ->}, onTimerResumed: (ChipType) -> Int = { 0 }) {
    var isTimerOn = true
    val playIcon = ImageVector.vectorResource(id = R.drawable.ic_play_24dp)
    val pauseIcon = ImageVector.vectorResource(id = R.drawable.ic_pause_24dp)
    val activeColor = Color("#649e5d".toColorInt())
    val inactiveColor = Color("#a15757".toColorInt())

    var currentChipIndex by remember {
        mutableStateOf(0)
    }

    var totalSecondsRemaing = 0

    val currentChip = chips[currentChipIndex]

    var progress by remember {
        mutableStateOf(1.0)
    }

    var sliderColor by remember {
        mutableStateOf(activeColor)
    }

    val title by remember {
        mutableStateOf(currentChip.title)
    }

    var value by remember {
        val minutes =currentChip.value
        mutableStateOf("$minutes:00")
    }

    var timerSeconds by remember {
        val minutes = currentChip.value.toInt()
        mutableStateOf(minutes * 60)
    }

    var iconImage by remember {
        mutableStateOf(pauseIcon)
    }

    //TODO: fix pause
    var timer by remember {
        mutableStateOf(object : CountDownTimer(timerSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                totalSecondsRemaing = (millisUntilFinished / 1000).toInt()
                val minutesRemaining = (totalSecondsRemaing / 60)
                val secondsRemaining = (totalSecondsRemaing % 60)

                val minutesString = if (minutesRemaining < 10) "0$minutesRemaining" else "$minutesRemaining"
                val secondsString = if (secondsRemaining < 10) "0$secondsRemaining" else "$secondsRemaining"

                value = "$minutesString:$secondsString"
                //TODO: to semplify
                progress = (1f - (1f - totalSecondsRemaing.toFloat().div(timerSeconds))).toDouble()
            }

            override fun onFinish() {
                onTimerPausedOrStopped(currentChip.type, null)
                //TODO: handle the next timer or stop the execution
            }

        })
    }

    LaunchedEffect(currentChip.type) {
        timer.start()
    }

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
        horizontalAlignment = Alignment.CenterHorizontally) {
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
        IconButton(
            modifier = Modifier
                .width(48.dp)
                .aspectRatio(1f)
                .background(
                    color = Color("#E3BAFF".toColorInt()),
                    shape = CircleShape
                ),
            content = {
                Icon(
                    imageVector = iconImage,
                    contentDescription = "ADD icon",
                    tint = Color.Black
                )
            },
            onClick = {
                iconImage = if (isTimerOn) playIcon else pauseIcon
                sliderColor = if (isTimerOn) inactiveColor else activeColor

                if (isTimerOn) {
                    timer.cancel()
                    onTimerPausedOrStopped(currentChip.type, totalSecondsRemaing)
                } else {
                    timerSeconds = onTimerResumed(currentChip.type)
                    timer.start()
                }

                isTimerOn = !isTimerOn
            }
        )
    }
}