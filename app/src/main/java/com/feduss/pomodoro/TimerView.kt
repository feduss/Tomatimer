package com.feduss.pomodoro

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
fun TimerView(@PreviewParameter(ChipListProvider::class) chips: List<Chip>) {
    var isTimerOn = true
    val playIcon = ImageVector.vectorResource(id = R.drawable.ic_play_24dp)
    val pauseIcon = ImageVector.vectorResource(id = R.drawable.ic_pause_24dp)

    val progress by remember {
        mutableStateOf(1.0)
    }

    var sliderColor by remember {
        mutableStateOf(Color.Green)
    }

    val title by remember {
        mutableStateOf(chips[0].title)
    }

    val value by remember {
        val minutes = chips[0].value
        mutableStateOf("$minutes:00")
    }

    var iconImage by remember {
        mutableStateOf(pauseIcon)
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
            color = Color(("#E3BAFF".toColorInt())),
            textAlign = TextAlign.Center
        )
        Text(
            /*modifier = Modifier.weight(1f),*/
            text = value.toString(),
            color = Color(("#E3BAFF".toColorInt())),
            textAlign = TextAlign.Center
        )
        IconButton(
            modifier = Modifier
                .width(48.dp)
                .aspectRatio(1f)
                .background(
                    color = Color(("#E3BAFF".toColorInt())),
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
                sliderColor = if (isTimerOn) Color.Red else Color.Green
                isTimerOn = !isTimerOn
                //TODO timer logics
            }
        )
    }
}