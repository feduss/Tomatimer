package com.feduss.pomodoro

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import java.util.Calendar

@Composable
fun MainActivity(navController: NavHostController,
                 activity: MainActivityViewController,
                 viewModel: MainActivityViewModel) {
    val startDestination = Section.Setup.baseRoute

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Section.Setup.baseRoute) {
            val chips by remember {
                mutableStateOf(viewModel.getData(activity))
            }
            SetupView(
                chips = chips,
                onChipClicked = { tag ->
                    val args = listOf(tag)
                    navController.navigate(Section.Edit.withArgs(args)) {
                        launchSingleTop = true
                    }
                },
                onPlayIconClicked = {
                    navController.navigate(Section.Timer.baseRoute) {
                        launchSingleTop = true
                    }
                },
                onRestoreSavedTimerFlow = {
                    val chipIndexFromPref = viewModel.getPref(activity, PrefParamName.CurrentChip.name)
                    val cycleIndexFromPref = viewModel.getPref(activity, PrefParamName.CurrentCycle.name)
                    val secondsRemainingFromPref = viewModel.getPref(activity, PrefParamName.SecondsRemaining.name)
                    if(chipIndexFromPref != null && cycleIndexFromPref != null && secondsRemainingFromPref != null) {
                        navController.navigate(Section.Timer.withArgs(
                            optionalArgs = mapOf(
                                Pair(OptionalParams.ChipIndex.name, chipIndexFromPref),
                                Pair(OptionalParams.CycleIndex.name, cycleIndexFromPref),
                                Pair(OptionalParams.TimerSeconds.name, secondsRemainingFromPref)
                            )
                        )){
                            launchSingleTop = true
                        }
                        viewModel.setPref(activity, PrefParamName.CurrentChip.name, null)
                        viewModel.setPref(activity, PrefParamName.CurrentCycle.name, null)
                        viewModel.setPref(activity, PrefParamName.SecondsRemaining.name, null)
                    }
                }
            )
        }
        composable( route = Section.Edit.parametricRoute, arguments = listOf(
                navArgument(Params.Tag.name) { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val tag: Int? = navBackStackEntry.arguments?.getString(Params.Tag.name)?.toIntOrNull()
            tag?.let { tagNotNull ->
                val chip = viewModel.getData(activity)[tagNotNull]
                EditView(chip, onConfirmClicked = { type, newValue ->
                    viewModel.setPref(activity, type.valuePrefKey, newValue)
                    navController.popBackStack()
                })
            }
        }
        composable(route = Section.Timer.parametricRoute, arguments = listOf(
            navArgument(OptionalParams.ChipIndex.name) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(OptionalParams.CycleIndex.name) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(OptionalParams.TimerSeconds.name) {
                type = NavType.StringType
                nullable = true
            },
        )) { navBackStackEntry ->
            val initialChipIndex = navBackStackEntry.arguments?.getString(OptionalParams.ChipIndex.name) ?: "0"
            val initialCycle = navBackStackEntry.arguments?.getString(OptionalParams.CycleIndex.name) ?: "0"
            val initialTimerSeconds = navBackStackEntry.arguments?.getString(OptionalParams.TimerSeconds.name) ?: "0"
            TimerView(
                initialChipIndex = initialChipIndex.toInt(),
                initialCycle = initialCycle.toInt(),
                initialTimerSeconds = initialTimerSeconds.toInt(),
                chips = viewModel.getData(activity),
                onTimerPausedOrStopped = { chipType, currentCycle, secondsRemaining ->

                    //Save the current chip index
                    viewModel.setPref(
                        activity = activity,
                        pref = PrefParamName.CurrentChip.name,
                        newValue = chipType.tag.toString()
                    )

                    //Save the current cycle
                    viewModel.setPref(
                        activity = activity,
                        pref = PrefParamName.CurrentCycle.name,
                        newValue = currentCycle?.toString()
                    )

                    //Save the timer seconds remaining
                    viewModel.setPref(
                        activity = activity,
                        pref = PrefParamName.SecondsRemaining.name,
                        newValue = secondsRemaining?.toString()
                    )

                    activity.removeBackgroundAlert()

                    //timer is finished, vibrate! //TODO: to test
                    if (secondsRemaining == null) {
                        Log.e("VIBRATION:", " OK")
                        val vibrationPattern = longArrayOf(0, 500, 50, 300)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val vibratorService = activity.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                            vibratorService.defaultVibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
                        } else {
                            val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            if (vibrator.hasVibrator()) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
                                } else {
                                    //deprecated in API 26
                                    vibrator.vibrate(vibrationPattern, -1)
                                }
                            }
                        }


                    }
                },
                onTimerStartedOrResumed = { chipType, secondsRemaming ->
                    val secondsRemainings =
                        secondsRemaming ?:
                        (viewModel.getPref(activity, PrefParamName.SecondsRemaining.name)?.toInt() ?: 0)

                    val millisSince1970 = Calendar.getInstance().timeInMillis

                    activity.setBackgroundAlert(secondsRemainings * 1000L, millisSince1970)

                    secondsRemainings
                },
                onBackToHome = { removeBackgroundAlert ->
                    if (removeBackgroundAlert) {
                        activity.removeBackgroundAlert()
                    }

                    navController.navigate(Section.Setup.baseRoute) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}