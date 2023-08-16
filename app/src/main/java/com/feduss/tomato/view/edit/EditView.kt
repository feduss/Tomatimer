package com.feduss.tomato.view.edit

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomatimer.entity.enums.ChipType
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomato.R
import com.feduss.tomato.viewmodel.edit.EditViewModel
import kotlinx.coroutines.launch


@Composable
fun EditView(
    context: Context = LocalContext.current,
    navController: NavController = rememberSwipeDismissableNavController(),
    viewModel: EditViewModel
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
        strokeWidth = 4.dp
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 24.dp, 8.dp, 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = viewModel.chip.fullTitle,
            color = color
        )

        ConstraintLayout(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            val (picker, unitLabel) = createRefs()

            Picker(
                state = state,
                contentDescription = contentDescription,
                readOnly = false,
                readOnlyLabel = {
                    Text(
                    text = viewModel.chip.fullTitle,
                    color = Color.White
                    )
                },
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
                    .focusRequester(focusRequester)
                    .focusable()
                    .constrainAs(picker) {
                        centerHorizontallyTo(parent)
                        centerVerticallyTo(parent)
                    },
                separation = 4.dp,
            ) {
                Text(
                    text = items[it].toString(),
                    color = color
                )
            }

            Text(
                modifier = Modifier
                    .constrainAs(unitLabel) {
                      centerVerticallyTo(parent)
                      absoluteLeft.linkTo(picker.absoluteRight, margin = 4.dp)
                    },
                text = viewModel.chip.unit,
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