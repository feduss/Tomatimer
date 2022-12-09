package com.feduss.pomodoro

import android.widget.TextView
import androidx.compose.foundation.layout.*
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

private const val maxTimerValue = 60
private const val minTimerValue = 0

@Preview
@Composable
fun EditView(@PreviewParameter(ChipProvider::class) chip: Chip = Chip("Pomodoro", "25", "min", ChipType.Tomato),
             type: ValueType = ValueType.Time, onConfirmClicked: (ChipType, String) -> Unit = {_, _ ->}) {
    var newValue by remember {
        mutableStateOf(chip.value.toInt())
    }
    val unit = chip.unit
    Column(
        Modifier.padding(8.dp, 24.dp, 8.dp, 0.dp).fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = chip.title,
            color = Color(("#E3BAFF".toColorInt()))
        )
        Row(
            Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_sub_white_24dp),
                        contentDescription = "Sub icon",
                        tint = Color(("#E3BAFF".toColorInt()))
                    )
                },
                onClick = {
                    newValue-=1
                    if (type == ValueType.Time && newValue < minTimerValue) {
                        newValue = minTimerValue
                    }
                }
            )
            Text(
                modifier = Modifier.weight(1f),
                text = ("$newValue $unit").trim(),
                color = Color(("#E3BAFF".toColorInt())),
                textAlign = TextAlign.Center
            )
            IconButton(
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_white_24dp),
                        contentDescription = "ADD icon",
                        tint = Color(("#E3BAFF".toColorInt()))
                    )
                },
                onClick = {
                    newValue+=1
                    if (type == ValueType.Time && newValue > maxTimerValue) {
                        newValue = maxTimerValue
                    }
                }
            )
        }
        IconButton(
            content = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_check_pink_24dp),
                    contentDescription = "Confirm icon",
                    tint = Color(("#E3BAFF".toColorInt()))
                )
            },
            onClick = {
                onConfirmClicked(chip.type, newValue.toString())
            }
        )
    }
}