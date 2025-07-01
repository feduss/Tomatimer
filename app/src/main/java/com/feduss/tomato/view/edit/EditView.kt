package com.feduss.tomato.view.edit

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.navigation.NavController
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PickerState
import androidx.wear.compose.material.rememberPickerState
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomato.uistate.viewmodel.edit.EditViewModel


@Composable
fun EditView(
    context: Context,
    navController: NavController,
    viewModel: EditViewModel
) {

    val dataUiState by viewModel.dataUiState.collectAsState()
    val navUiState by viewModel.navUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initUiState()
    }

    navUiState?.let { state ->
        when(state) {
            is EditViewModel.NavUiState.GoBackToHome -> {
                //The SetupView needs to refresh its data
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    Consts.NewValueKey.value,
                    state.newValue
                )

                navController.popBackStack()
            }
        }
        viewModel.firedNavState()
    }

    dataUiState?.let { state ->

        val pickerState: PickerState = rememberPickerState(
            initialNumberOfOptions = state.numberOfOption,
            initiallySelectedOption = state.initOption
        )

        val contentDescription by remember { derivedStateOf { "${pickerState.selectedOption + 1}" } }

        CircularProgressIndicator(
            progress = { state.progressBarState.toFloat() },
            modifier = Modifier.fillMaxSize(),
            color = state.progressBarColor,
            trackColor = Color.Transparent,
            strokeWidth = 4.dp
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp, 24.dp, 8.dp, 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.titleText,
                color = state.titleColor
            )

            Picker(
                state = pickerState,
                contentDescription = contentDescription,
                readOnly = false,
                readOnlyLabel = {
                    Text(
                        text = state.pickerReadOnlyLabelText,
                        color = state.pickerReadOnlyLabelColor
                    )
                },
                onSelected = {},
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                separation = 4.dp,
            ) {
                Text(
                    text = "${state.items[it]} ${state.pickerInnerLabelSuffixText}".trim(),
                    color = state.pickerInnerLabelColor
                )
                viewModel.userHasChangedOption(state.items[it])
            }

            val color = state.confirmButtonUiState.backgroundColor
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
                        imageVector = ImageVector.vectorResource(id = state.confirmButtonUiState.iconId),
                        contentDescription = state.confirmButtonUiState.iconDescription,
                        tint = state.confirmButtonUiState.iconColor
                    )
                },
                onClick = {
                    viewModel.userHasTappedConfirmButton(
                        context = context,
                        newValue = (pickerState.selectedOption + 1).toString()
                    )
                }
            )
        }
    }
}