package com.feduss.tomato.views.edit

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomato.R
import com.feduss.tomato.enums.ChipType
import com.feduss.tomato.enums.Consts
import com.feduss.tomato.provider.ChipDatas
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun EditView(
    context: Context = LocalContext.current,
    navController: NavController = rememberSwipeDismissableNavController(),
    viewModel: EditViewModel = EditViewModel(ChipDatas.demoList[0])
) {

    val haptic = LocalHapticFeedback.current

    val color = Color(("#E3BAFF".toColorInt()))

    val numbOptions =
        when (viewModel.chip.type) {
            ChipType.CyclesNumber -> 10
            else -> {
                60
            }
    }

    val items: List<Int> = (1..numbOptions + 1).toList()

    val initOption = viewModel.chip.value.toInt() - 1

    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val state = rememberPickerState(
        initialNumberOfOptions = numbOptions,
        initiallySelectedOption = initOption
    )

    val contentDescription by remember { derivedStateOf { "${state.selectedOption + 1}" } }

    //Progress of the rounded progress bar
    val progress by remember(contentDescription) {
        mutableStateOf((state.selectedOption + 1).toDouble() / numbOptions)
    }

    CircularProgressIndicator(
        progress = progress.toFloat(),
        modifier = Modifier.fillMaxSize(),
        color = color,
        strokeWidth = 8.dp
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 16.dp, 8.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = viewModel.chip.title,
            color = color
        )

        Picker(
            state = state,
            contentDescription = contentDescription,
            readOnly = false,
            readOnlyLabel = { Text(
                                    text = viewModel.chip.title,
                                    color = Color.White
                                )},
            onSelected = {},
            modifier = Modifier
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        if (it.verticalScrollPixels > 0f) {
                            state.scrollToOption(state.selectedOption + 1)
                        } else {
                            state.scrollToOption(state.selectedOption - 1)
                        }
                        performHapticFeedback(haptic)
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
                confirmButtonClicked(context, viewModel, navController, viewModel.chip.type, (state.selectedOption + 1).toString())
            }
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

}

fun performHapticFeedback(haptic: HapticFeedback) {
    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
}

fun confirmButtonClicked(context: Context, viewModel: EditViewModel, navController: NavController,
                         chipType: ChipType, newValue: String) {
    viewModel.setNewValue(context, chipType, newValue)

    //The SetupView needs to refresh its data
    navController.previousBackStackEntry?.savedStateHandle?.set(Consts.NewValueKey.value, newValue)

    navController.popBackStack()
}