package com.feduss.pomodoro.views

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.wear.compose.material.*
import com.feduss.pomodoro.R
import com.feduss.pomodoro.enums.ChipType
import com.feduss.pomodoro.enums.ValueType
import com.feduss.pomodoro.models.Chip
import com.feduss.pomodoro.models.ChipProvider
import kotlinx.coroutines.launch

private const val maxTimerValue = 60
private const val minTimerValue = 0

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun EditView(@PreviewParameter(ChipProvider::class) chip: Chip = Chip("Pomodoro", "25", "min", ChipType.Tomato),
             type: ValueType = ValueType.Time, onConfirmClicked: (ChipType, String) -> Unit = { _, _ ->}) {

    val color = Color(("#E3BAFF".toColorInt()))

    val numbOptions =
        when (chip.type) {
            ChipType.CyclesNumber -> 10
            else -> {
                60
            }
    }

    val items: List<Int> = (1..numbOptions + 1).toList()

    val initOption = chip.value.toInt()

    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val state = rememberPickerState(
        initialNumberOfOptions = numbOptions,
        initiallySelectedOption = initOption
    )

    val contentDescription by remember { derivedStateOf { "${state.selectedOption + 1}" } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 16.dp, 8.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = chip.title,
            color = Color(("#E3BAFF".toColorInt()))
        )

        Picker(
            state = state,
            contentDescription = contentDescription,
            readOnly = false,
            readOnlyLabel = { Text(
                                    text = chip.title,
                                    color = Color.White
                                )},
            onSelected = {

            },
            modifier = Modifier
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        if (it.verticalScrollPixels > 0f) {
                            state.scrollToOption(state.selectedOption + 1)
                        } else {
                            state.scrollToOption(state.selectedOption - 1)
                        }
                    }
                    true
                }
                .fillMaxHeight()
                .weight(1f)
                .focusRequester(focusRequester)
                .focusable(),
            separation = 4.dp,
        ) {
            Text(
                text = items[it].toString(),
                color = color
            )
        }

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
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_check_24dp),
                    contentDescription = "Confirm icon",
                    tint = Color.Black
                )
            },
            onClick = {
                onConfirmClicked(chip.type, state.selectedOption.toString())
            }
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

}