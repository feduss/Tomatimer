package com.feduss.tomato.view.setup

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomatimer.entity.enums.OptionalParams
import com.feduss.tomatimer.entity.enums.Section
import com.feduss.tomato.BuildConfig
import com.feduss.tomato.view.ChipView
import com.feduss.tomato.uistate.viewmodel.setup.SetupViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun SetupView(
    context: Context = LocalContext.current,
    navController: NavHostController = rememberSwipeDismissableNavController(),
    viewModel: SetupViewModel = hiltViewModel(),
    columnState: ScalingLazyColumnState,
    openAppSettings: () -> Unit
) {

    val dataUiState by viewModel.dataUiState.collectAsState()
    val navUiState by viewModel.navUiState.collectAsState()

    //Go to timer screen if there was an active timer
    restoreSavedTimerFlow(context, viewModel, navController)

    //When the user edit the timer in EditView, the SetupView needs to refresh its datas
    val updateChipState = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
        Consts.NewValueKey.value)?.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.initUiState(context)
    }

    LaunchedEffect(updateChipState) {
        updateChipState?.value?.let { newValue ->
            viewModel.updateLastSelectedChip(newValue)
        }
    }

    val versionName = BuildConfig.VERSION_NAME
    val versionCode = BuildConfig.VERSION_CODE

    navUiState?.let {
        when(it) {
            SetupViewModel.NavUiState.GoToAppSettings -> {
                openAppSettings()
            }
            is SetupViewModel.NavUiState.GoToChipEdit -> {
                val args = listOf(it.tag)
                navController.navigate(Section.Edit.withArgs(args))
            }
            SetupViewModel.NavUiState.GoToStartTimer -> {
                navController.navigate(Section.Timer.baseRoute)
            }
        }
        viewModel.firedNavState()
    }

    dataUiState?.let { state ->
        ScalingLazyColumn(
            modifier = Modifier
                .padding(16.dp, 0.dp, 16.dp, 0.dp),
            columnState = columnState
        ) {
            items(state.chipsUiState) { chipUiState ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ChipView(
                        chipUiState = chipUiState,
                        tag = chipUiState.type.tag,
                        onChipClicked = { tag ->
                            viewModel.userTapOnChip(tag = tag)
                        }
                    )
                    Box(modifier = Modifier.height(4.dp))
                }
            }

            item {
                val color = state.playCompactButtonUiState.backgroundColor
                CompactButton(
                    modifier = Modifier
                        .width(32.dp)
                        .aspectRatio(1f)
                        .background(
                            color = color,
                            shape = CircleShape
                        ),
                    colors = ButtonDefaults.primaryButtonColors(color, color),
                    content = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = state.playCompactButtonUiState.iconId),
                            contentDescription = state.playCompactButtonUiState.iconDescription,
                            tint = state.playCompactButtonUiState.iconColor
                        )
                    },
                    onClick = {
                        viewModel.userHasTappedPlayButton()
                    }
                )
            }

            item {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(state.versionUiState.textId, versionName, versionCode),
                    textAlign = TextAlign.Center,
                    color = state.versionUiState.color,
                    fontSize = TextUnit(10f, TextUnitType.Sp)
                )
            }

            item {
                val color = state.settingsCompactButtonUiState.backgroundColor

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = stringResource(state.settingsUiState.textId),
                        textAlign = TextAlign.Center,
                        color = state.settingsUiState.color,
                        fontSize = TextUnit(10f, TextUnitType.Sp)
                    )
                    CompactButton(
                        modifier = Modifier
                            .width(32.dp)
                            .aspectRatio(1f)
                            .background(
                                color = color,
                                shape = CircleShape
                            ),
                        colors = ButtonDefaults.primaryButtonColors(color, color),
                        content = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = state.settingsCompactButtonUiState.iconId),
                                contentDescription = state.settingsCompactButtonUiState.iconDescription,
                                tint = state.settingsCompactButtonUiState.iconColor
                            )
                        },
                        onClick = {
                            viewModel.userHasTappedSettingsButton()
                        }
                    )
                }
            }
        }
    }
}

fun restoreSavedTimerFlow(context: Context, viewModel: SetupViewModel, navController: NavHostController) {
    val chipIndexFromPref = viewModel.getChipIndexFromPref(context)
    val cycleIndexFromPref = viewModel.getCycleIndexFromPref(context)
    var secondsRemainingFromPref = viewModel.getSecondsRemainingFromPref(context)

    //If secondsRemainingFromPref == null --> timer was not paused
    //If yes, try to restore this seconds from the background alarm, if set
    if (secondsRemainingFromPref == null && chipIndexFromPref != null && cycleIndexFromPref != null) {
        secondsRemainingFromPref =
            viewModel.getSecondsFromAlarmTime(context)
    }
    //If secondsRemainingFromPref == 0, the user early return in notification activity
    else if (secondsRemainingFromPref.equals("0")) {
        secondsRemainingFromPref = null
        viewModel.cancelTimer(context)
    }

    if(chipIndexFromPref != null && cycleIndexFromPref != null && secondsRemainingFromPref != null) {
        navController.navigate(
            Section.Timer.withArgs(
                optionalArgs = mapOf(
                    Pair(OptionalParams.ChipIndex.name, chipIndexFromPref),
                    Pair(OptionalParams.CycleIndex.name, cycleIndexFromPref),
                    Pair(OptionalParams.TimerSeconds.name, secondsRemainingFromPref)
                )
            ))
        viewModel.cancelTimer(context)
    }
}