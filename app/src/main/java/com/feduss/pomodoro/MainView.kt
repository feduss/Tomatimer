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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.util.Calendar

@Composable
fun MainActivity(navController: NavHostController,
                 startDestination: String = Section.Setup.baseRoute,
                 activity: MainActivityViewController,
                 viewModel: MainActivityViewModel) {
    NavHost(
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
                })
        }
        composable( route = Section.Edit.parametricRoute, arguments = listOf(
                navArgument(Params.Tag.name) { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val tag: Int? = navBackStackEntry.arguments?.getString(Params.Tag.name)?.toIntOrNull()
            tag?.let { tagNotNull ->
                val chip = viewModel.getData(activity)[tagNotNull]
                EditView(chip, onConfirmClicked = { type, newValue ->
                    viewModel.userHasUpdatedPrefOfChip(activity, type.valuePrefKey, newValue)
                    navController.popBackStack()
                })
            }
        }
        composable(route = Section.Timer.baseRoute) {
            TimerView(
                chips = viewModel.getData(activity),
                onTimerPausedOrStopped = { chipType, secondsRemaining ->
                    viewModel.userHasUpdatedPrefOfChip(
                        activity = activity,
                        pref = chipType.valueRemainingPrefKey,
                        newValue = secondsRemaining.toString()
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
                        (viewModel.getPrefOfChip(activity, chipType.valueRemainingPrefKey)?.toInt() ?: 0)

                    val millisSince1970 = Calendar.getInstance().timeInMillis

                    activity.setBackgroundAlert(secondsRemainings * 1000L, millisSince1970)

                    secondsRemainings
                },
                onBackToHome = {
                    navController.navigate(Section.Setup.baseRoute) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}