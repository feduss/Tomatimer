package com.feduss.tomato.views

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.feduss.tomato.MainActivityViewController
import com.feduss.tomato.MainActivityViewModel
import com.feduss.tomato.NotificationActivity
import com.feduss.tomato.SetupView
import com.feduss.tomato.enums.OptionalParams
import com.feduss.tomato.enums.Params
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.enums.Section
import com.feduss.tomato.utils.PrefsUtils
import java.util.Calendar
import kotlin.system.exitProcess

@Composable
fun MainActivity(navController: NavHostController,
                 context: Context,
                 viewController: MainActivityViewController,
                 viewModel: MainActivityViewModel
) {
    val startDestination = Section.Setup.baseRoute

    SwipeDismissableNavHost(
        modifier = Modifier.background(Color.Black),
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Section.Setup.baseRoute) {
            val chips by remember {
                mutableStateOf(viewModel.getData(context))
            }
            SetupView(
                chips = chips,
                onChipClicked = { tag ->
                    val args = listOf(tag)
                    navController.navigate(Section.Edit.withArgs(args))
                },
                onPlayIconClicked = {
                    navController.navigate(Section.Timer.baseRoute)
                },
                onRestoreSavedTimerFlow = {
                    val chipIndexFromPref = PrefsUtils.getPref(context, PrefParamName.CurrentTimerIndex.name)
                    val cycleIndexFromPref = PrefsUtils.getPref(context, PrefParamName.CurrentCycle.name)
                    var secondsRemainingFromPref = PrefsUtils.getPref(context, PrefParamName.SecondsRemaining.name)

                    //If secondsRemainingFromPref == null --> timer was not paused
                    //If yes, try to restore this seconds from the background alarm, if set
                    if (secondsRemainingFromPref == null && chipIndexFromPref != null && cycleIndexFromPref != null) {
                        secondsRemainingFromPref =
                            getSecondsFromAlarmTime(context)
                    }
                    //If secondsRemainingFromPref == 0, the user early return in notification activity
                    else if (secondsRemainingFromPref.equals("0")) {
                        secondsRemainingFromPref = null
                        PrefsUtils.cancelTimer(context)
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
                        PrefsUtils.cancelTimer(context)
                    }
                },
                onCloseApp = {
                    viewController.finish()
                    exitProcess(0)
                }
            )
        }
        composable( route = Section.Edit.parametricRoute, arguments = listOf(
                navArgument(Params.Tag.name) { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val tag: Int? = navBackStackEntry.arguments?.getString(Params.Tag.name)?.toIntOrNull()
            tag?.let { tagNotNull ->
                val chip = viewModel.getData(context)[tagNotNull]
                EditView(chip, onConfirmClicked = { type, newValue ->
                    PrefsUtils.setPref(context, type.valuePrefKey, newValue)
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
                chips = viewModel.getData(context),
                onSaveCurrentTimerData = { chipType, currentTimerName, currentCycle, secondsRemaining ->

                    //Save the current chip type
                    PrefsUtils.setPref(
                        context = context,
                        pref = PrefParamName.CurrentChipType.name,
                        newValue = chipType.stringValue
                    )

                    //Save the current chip index
                    PrefsUtils.setPref(
                        context = context,
                        pref = PrefParamName.CurrentTimerIndex.name,
                        newValue = chipType.tag.toString()
                    )

                    //Save the current timer title
                    PrefsUtils.setPref(
                        context = context,
                        pref = PrefParamName.CurrentTimerName.name,
                        newValue = currentTimerName
                    )

                    //Save the current cycle
                    PrefsUtils.setPref(
                        context = context,
                        pref = PrefParamName.CurrentCycle.name,
                        newValue = currentCycle?.toString()
                    )

                    //Save the timer seconds remaining
                    PrefsUtils.setPref(
                        context = context,
                        pref = PrefParamName.SecondsRemaining.name,
                        newValue = secondsRemaining?.toString()
                    )
                },
                onLoadTimerSecondsRemainings = {
                    (PrefsUtils.getPref(context, PrefParamName.SecondsRemaining.name)?.toInt() ?: 0)
                },
                onSetTimerState = { isTimerActive ->
                    val stringValue = if (isTimerActive) "true" else "false"
                    PrefsUtils.setPref(context, PrefParamName.IsTimerActive.name, stringValue)
                },
                onBackToHome = {
                    PrefsUtils.cancelTimer(context)
                    navController.popBackStack()
                },
                onTimerExpired = {
                    PrefsUtils.setPref(context, PrefParamName.IsTimerActive.name, "false")

                    val notificationIntent = Intent(context, NotificationActivity::class.java)
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    viewController.startActivity(notificationIntent)
                    viewController.finish()
                }
            )
        }
    }
}

private fun getSecondsFromAlarmTime(
    context: Context
): String {
    val alarmSetTime = PrefsUtils.getPref(context, PrefParamName.AlarmSetTime.name)?.toLong() ?: 0L
    val nowSeconds = Calendar.getInstance().timeInMillis / 1000L

    return (alarmSetTime - nowSeconds).toString()
}