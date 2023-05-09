package com.feduss.tomato.views

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.navigation.composable
import com.feduss.tomato.MainViewController
import com.feduss.tomato.views.notification.NotificationViewController
import com.feduss.tomato.views.setup.SetupView
import com.feduss.tomato.enums.OptionalParams
import com.feduss.tomato.enums.Params
import com.feduss.tomato.enums.Section
import com.feduss.tomato.models.Chip
import com.feduss.tomato.views.edit.EditView
import com.feduss.tomato.views.edit.EditViewModel
import com.feduss.tomato.views.edit.EditViewModelFactory
import com.feduss.tomato.views.setup.SetupViewModel
import com.feduss.tomato.views.setup.SetupViewModelFactory
import com.feduss.tomato.views.timer.TimerView
import com.feduss.tomato.views.timer.TimerViewModel
import com.feduss.tomato.views.timer.TimerViewModelFactory
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scrollable
import java.util.*
import kotlin.system.exitProcess

@OptIn(ExperimentalHorologistComposeLayoutApi::class)
@Composable
fun MainActivity(
    context: Context,
    activity: MainViewController,
    navController: NavHostController,
    chips: List<Chip>,
    startDestination: String = Section.Setup.baseRoute
) {
    
    var endCurvedText by remember() {
        mutableStateOf("")
    }

    val timeSource = TimeTextDefaults.timeSource(
        DateFormat.getBestDateTimePattern(Locale.getDefault(), "HH:mm")
    )

    val color = Color(("#E3BAFF".toColorInt()))

    WearNavScaffold(
        modifier = Modifier.background(Color.Black),
        timeText = {
            if (endCurvedText.isNotEmpty()) {
                TimeText(
                    timeSource = timeSource,
                    endCurvedContent = {
                        curvedText(
                            text = endCurvedText,
                            color = color
                        )
                    }
                )
            } else {
                TimeText(
                    timeSource = timeSource,
                )
            }

        },
        navController = navController,
        startDestination = startDestination
    ) {

        scrollable(route = Section.Setup.baseRoute) {
            val setupViewModel: SetupViewModel = viewModel(factory = SetupViewModelFactory(chips))
            SetupView(
                context,
                navController,
                setupViewModel,
                it
            ) { closeApp(activity) }
        }

        composable(route = Section.Edit.parametricRoute, arguments = listOf(
            navArgument(Params.Tag.name) { type = NavType.StringType }
        )
        ) { navBackStackEntry ->
            val tag: Int? = navBackStackEntry.arguments?.getString(Params.Tag.name)?.toIntOrNull()
            tag?.let { tagNotNull ->
                val chip = chips[tagNotNull]
                val editViewModel: EditViewModel = viewModel(factory = EditViewModelFactory(chip))
                EditView(context, navController, editViewModel)
            }
        }
        composable(
            route = Section.Timer.parametricRoute, arguments = listOf(
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
            )
        ) { navBackStackEntry ->
            val initialChipIndex =
                navBackStackEntry.arguments?.getString(OptionalParams.ChipIndex.name)?.toInt() ?: 0
            val initialCycle =
                navBackStackEntry.arguments?.getString(OptionalParams.CycleIndex.name)?.toInt() ?: 0
            val initialTimerSeconds =
                navBackStackEntry.arguments?.getString(OptionalParams.TimerSeconds.name)?.toInt() ?: 0

            val timerViewModel: TimerViewModel = viewModel(
                factory = TimerViewModelFactory(chips,
                    initialChipIndex,
                    initialCycle,
                    initialTimerSeconds
                )
            )

            TimerView(
                context = context,
                navController = navController,
                viewModel = timerViewModel,
                onTimerSet = { hourTimerEnd: String ->
                    endCurvedText = hourTimerEnd
                },
                openNotification = { openNotification(context, activity) }
            )
        }
    }
}

private fun closeApp(activity: MainViewController) {
    activity.finishAndRemoveTask()
    exitProcess(0)
}

private fun openNotification(context: Context, activity: MainViewController) {
    val notificationIntent = Intent(context, NotificationViewController::class.java)
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    activity.startActivity(notificationIntent)
    activity.finish()
}