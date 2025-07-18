package com.feduss.tomato.view.setup

import android.app.AlarmManager
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomatimer.entity.enums.Consts
import com.feduss.tomatimer.entity.enums.OptionalParams
import com.feduss.tomatimer.entity.enums.Section
import com.feduss.tomato.BuildConfig
import com.feduss.tomato.R
import com.feduss.tomato.view.ChipView
import com.feduss.tomato.viewmodel.setup.SetupViewModel
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
    openAppSettings: () -> Unit,
    openAlarmSettings: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {

    val defaultColor = Color(("#E3BAFF".toColorInt()))
    val disabledColor = Color.LightGray

    val alarmManager by remember {
        mutableStateOf(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
    }

    var canScheduleExactAlarms by remember {
        mutableStateOf(false)
    }

    //Go to timer screen if there was an active timer
    restoreSavedTimerFlow(context, viewModel, navController)

    //When the user edit the timer in EditView, the SetupView needs to refresh its datas
    val updateChipState = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
        Consts.NewValueKey.value)?.observeAsState()

    LaunchedEffect(updateChipState) {
        updateChipState?.value?.let { newValue ->
            viewModel.updateLastSelectedChip(newValue)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                canScheduleExactAlarms = canScheduleExactAlarms(alarmManager)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val versionName = BuildConfig.VERSION_NAME
    val versionCode = BuildConfig.VERSION_CODE

    ScalingLazyColumn(
        modifier = Modifier
            .padding(16.dp, 0.dp, 16.dp, 0.dp),
        columnState = columnState
    ) {
        items(viewModel.chips) { chip ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChipView(
                    chip = chip,
                    tag = chip.type.tag,
                    onChipClicked = { tag ->
                        viewModel.userHasSelectedChip(tag.toInt())
                        val args = listOf(tag)
                        navController.navigate(Section.Edit.withArgs(args))
                    }
                )
                Box(modifier = Modifier.height(4.dp))
            }
        }

        item {
            val color = if (canScheduleExactAlarms) defaultColor else disabledColor
            CompactButton(
                modifier = Modifier
                    .width(32.dp)
                    .aspectRatio(1f)
                    .background(
                        color = color,
                        shape = CircleShape
                    ),
                colors = ButtonDefaults.primaryButtonColors(
                    color,
                    color
                ),
                enabled = canScheduleExactAlarms,
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

        if (!canScheduleExactAlarms) {
           item {
               Column(
                   verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                   horizontalAlignment = Alignment.CenterHorizontally
               ) {
                   Text(
                       modifier = Modifier.padding(top = 12.dp),
                       text = stringResource(viewModel.scheduleAlarmWarningId),
                       textAlign = TextAlign.Center,
                       color = Color.White,
                       fontSize = TextUnit(10f, TextUnitType.Sp),
                       lineHeight = TextUnit(16f, TextUnitType.Sp)
                   )

                   CompactButton(
                       modifier = Modifier
                           .width(32.dp)
                           .aspectRatio(1f)
                           .background(
                               color = defaultColor,
                               shape = CircleShape
                           ),
                       colors = ButtonDefaults.primaryButtonColors(defaultColor, defaultColor),
                       content = {
                           Icon(
                               imageVector = ImageVector.vectorResource(id = R.drawable.ic_alarm_settings),
                               contentDescription = "Alarm settings icon",
                               tint = Color.Black
                           )
                       },
                       onClick = {
                           openAlarmSettings()
                       }
                   )
               }
           }
        }

        item {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = "v$versionName ($versionCode)",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = TextUnit(10f, TextUnitType.Sp)
            )
        }

        item {
            val color = Color(("#E3BAFF".toColorInt()))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(R.string.app_settings_button_description),
                    textAlign = TextAlign.Center,
                    color = Color.White,
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
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_settings),
                            contentDescription = "Settings icon",
                            tint = Color.Black
                        )
                    },
                    onClick = {
                        openAppSettings()
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