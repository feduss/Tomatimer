package com.feduss.tomato.views.setup

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomato.R
import com.feduss.tomato.enums.Consts
import com.feduss.tomato.enums.OptionalParams
import com.feduss.tomato.enums.Section
import com.feduss.tomato.provider.ChipDatas
import com.feduss.tomato.views.ChipView

@Preview
@Composable
fun SetupView(context: Context = LocalContext.current,
              navController: NavHostController = rememberSwipeDismissableNavController(),
              viewModel: SetupViewModel = SetupViewModel(ChipDatas.demoList),
              closeApp: () -> Unit = {}) {
    //Go to timer screen if there was an active timer
    restoreSavedTimerFlow(context, viewModel, navController)

    //When the user edit the timer in EditView, the SetupView needs to refresh its datas
    val updateChipState = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(Consts.NewValueKey.value)?.observeAsState()

    LaunchedEffect(updateChipState) {
        updateChipState?.value?.let { newValue ->
            viewModel.updateLastSelectedChip(newValue)
        }
    }

    var isAlertDialogVisible by remember {
        mutableStateOf(false)
    }

    BackHandler {
        isAlertDialogVisible = !isAlertDialogVisible
    }

    SwipeToDismissBox(onDismissed = { isAlertDialogVisible = true }) {
        if (isAlertDialogVisible) {
            Alert(
                title = {
                    Text(
                        text = "Vuoi chiudere l'app?",
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
                                    id = R.drawable.ic_close_24dp
                                ),
                                contentDescription = "Close icon",
                                tint = Color.White
                            )
                        },
                        onClick = {
                            isAlertDialogVisible = false
                        }
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
                                    id = R.drawable.ic_check_24dp
                                ),
                                contentDescription = "Check icon",
                                tint = Color.Black
                            )
                        },
                        onClick = { closeApp() }
                    )
                }
            )

        }
        else {
            Column(
                Modifier
                    .padding(32.dp, 16.dp, 32.dp, 8.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(0.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.chips) { chip ->
                        ChipView(
                            chip = chip,
                            tag = chip.type.tag,
                            fontSize = 10f,
                            onChipClicked = { tag ->
                                viewModel.userHasSelectedChip(tag.toInt())
                                val args = listOf(tag)
                                navController.navigate(Section.Edit.withArgs(args))
                            }
                        )
                    }
                }
                val color = Color(("#E3BAFF".toColorInt()))
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
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_play_24dp),
                            contentDescription = "Play icon",
                            tint = Color.Black
                        )
                    },
                    onClick = {
                        navController.navigate(Section.Timer.baseRoute)
                    }
                )
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