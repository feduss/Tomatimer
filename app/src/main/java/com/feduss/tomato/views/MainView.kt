package com.feduss.tomato

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.feduss.tomato.enums.OptionalParams
import com.feduss.tomato.enums.Params
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.enums.Section
import com.feduss.tomato.utils.AlarmUtils
import com.feduss.tomato.utils.NotificationUtils
import com.feduss.tomato.utils.PrefsUtils
import com.feduss.tomato.views.EditView
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
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                onRestoreSavedTimerFlow = {
                    val chipIndexFromPref = PrefsUtils.getPref(activity, PrefParamName.CurrentChip.name)
                    val cycleIndexFromPref = PrefsUtils.getPref(activity, PrefParamName.CurrentCycle.name)
                    var secondsRemainingFromPref = PrefsUtils.getPref(activity, PrefParamName.SecondsRemaining.name)

                    //If secondsRemainingFromPref == null == timer is not paused
                    //Then try to restore this seconds from the background alarm, if set
                    if (secondsRemainingFromPref == null && chipIndexFromPref != null && cycleIndexFromPref != null) {
                        secondsRemainingFromPref =
                            getSecondsFromAlarmTime(activity, secondsRemainingFromPref)
                    }

                    if(chipIndexFromPref != null && cycleIndexFromPref != null && secondsRemainingFromPref != null) {
                        navController.navigate(
                            Section.Timer.withArgs(
                            optionalArgs = mapOf(
                                Pair(OptionalParams.ChipIndex.name, chipIndexFromPref),
                                Pair(OptionalParams.CycleIndex.name, cycleIndexFromPref),
                                Pair(OptionalParams.TimerSeconds.name, secondsRemainingFromPref)
                            )
                        )){
                            popUpTo(0)
                            launchSingleTop = true
                        }
                        PrefsUtils.setPref(activity, PrefParamName.CurrentChip.name, null)
                        PrefsUtils.setPref(activity, PrefParamName.CurrentCycle.name, null)
                        PrefsUtils.setPref(activity, PrefParamName.SecondsRemaining.name, null)
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
                    PrefsUtils.setPref(activity, type.valuePrefKey, newValue)
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
                    PrefsUtils.setPref(
                        context = activity,
                        pref = PrefParamName.CurrentChip.name,
                        newValue = chipType.tag.toString()
                    )

                    //Save the current cycle
                    PrefsUtils.setPref(
                        context = activity,
                        pref = PrefParamName.CurrentCycle.name,
                        newValue = currentCycle?.toString()
                    )

                    //Save the timer seconds remaining
                    PrefsUtils.setPref(
                        context = activity,
                        pref = PrefParamName.SecondsRemaining.name,
                        newValue = secondsRemaining?.toString()
                    )

                    AlarmUtils.removeBackgroundAlert(activity)
                    NotificationUtils.removeNotification(activity)
                },
                onTimerStartedOrResumed = { chipType, chipTitle, currentChip, currentCycle, secondsRemainings ->
                    val seconds =
                        secondsRemainings ?:
                        (PrefsUtils.getPref(activity, PrefParamName.SecondsRemaining.name)?.toInt() ?: 0)

                    val millisSince1970 = System.currentTimeMillis()

                    AlarmUtils.setBackgroundAlert(activity, chipTitle, currentChip, currentCycle, seconds * 1000L, millisSince1970)
                    NotificationUtils.setNotification(activity, chipTitle, seconds.toLong())

                    seconds
                },
                onBackToHome = { removeBackgroundAlert ->
                    if (removeBackgroundAlert) {
                        AlarmUtils.removeBackgroundAlert(activity)
                        NotificationUtils.removeNotification(activity)
                    }

                    navController.navigate(Section.Setup.baseRoute) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

private fun getSecondsFromAlarmTime(
    activity: MainActivityViewController,
    secondsRemainingFromPref: String?
): String? {
    var secondsRemainingFromPref1 = secondsRemainingFromPref
    val alarmSetTime = PrefsUtils.getPref(activity, PrefParamName.AlarmSetTime.name)?.toLong() ?: 0L
    val nowSeconds = Calendar.getInstance().timeInMillis / 1000L

    secondsRemainingFromPref1 = (alarmSetTime - nowSeconds).toString()
    return secondsRemainingFromPref1
}