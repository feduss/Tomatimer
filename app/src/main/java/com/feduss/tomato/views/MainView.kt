package com.feduss.tomato.views

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.feduss.tomato.MainViewController
import com.feduss.tomato.views.notification.NotificationViewController
import com.feduss.tomato.SetupView
import com.feduss.tomato.enums.OptionalParams
import com.feduss.tomato.enums.Params
import com.feduss.tomato.enums.Section
import com.feduss.tomato.models.Chip
import com.feduss.tomato.views.edit.EditViewModel
import com.feduss.tomato.views.setup.SetupViewModel
import com.feduss.tomato.views.timer.TimerView
import com.feduss.tomato.views.timer.TimerViewModel
import kotlin.system.exitProcess

@Composable
fun MainActivity(
    context: Context,
    activity: MainViewController,
    navController: NavHostController,
    chips: List<Chip>,
    startDestination: String = Section.Setup.baseRoute
) {

    SwipeDismissableNavHost(
        modifier = Modifier.background(Color.Black),
        navController = navController,
        startDestination = startDestination
    ) {

        composable(route = Section.Setup.baseRoute) {
            val setupViewModel = SetupViewModel(chips) //TODO: impl viewmodel factory
            SetupView(context, navController, setupViewModel, closeApp = { closeApp(activity) })
        }

        composable(route = Section.Edit.parametricRoute, arguments = listOf(
            navArgument(Params.Tag.name) { type = NavType.StringType }
        )
        ) { navBackStackEntry ->
            val tag: Int? = navBackStackEntry.arguments?.getString(Params.Tag.name)?.toIntOrNull()
            tag?.let { tagNotNull ->
                val chip = chips[tagNotNull]
                val editViewModel = EditViewModel(chip) //TODO: impl viewmodel factory
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

            //TODO: impl viewmodel factory
            val viewModel = TimerViewModel(
                chips,
                initialChipIndex,
                initialCycle,
                initialTimerSeconds
            )
            TimerView(
                context = context,
                navController = navController,
                viewModel = viewModel,
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