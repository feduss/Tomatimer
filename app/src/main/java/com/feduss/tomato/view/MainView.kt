package com.feduss.tomato.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.feduss.timerwear.lifecycle.OnLifecycleEvent
import com.feduss.tomatimer.entity.enums.OptionalParams
import com.feduss.tomatimer.entity.enums.Params
import com.feduss.tomatimer.entity.enums.Section
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomato.uistate.factory.getEditViewModel
import com.feduss.tomato.uistate.factory.getSetupViewModel
import com.feduss.tomato.uistate.factory.getTimerViewModel
import com.feduss.tomato.view.component.PageView
import com.feduss.tomato.view.edit.EditView
import com.feduss.tomato.view.notification.NotificationViewController
import com.feduss.tomato.view.setup.SetupView
import com.feduss.tomato.view.timer.TimerView
import com.feduss.tomato.uistate.viewmodel.edit.EditViewModel
import com.feduss.tomato.uistate.viewmodel.setup.SetupViewModel
import com.feduss.tomato.uistate.viewmodel.timer.TimerViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MainActivity(
    context: Context,
    activity: MainViewController,
    chips: List<Chip>,
    startDestination: String = Section.Setup.baseRoute,
) {

    RequestPermissions()

    var endCurvedText by remember {
        mutableStateOf("")
    }

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val navHostState =
        rememberSwipeDismissableNavHostState(swipeToDismissBoxState = swipeToDismissBoxState)
    val navController = rememberSwipeDismissableNavController()

    AppScaffold(
        timeText = {}
    ) {
        SwipeDismissableNavHost(
            startDestination = startDestination,
            navController = navController,
            state = navHostState
        ) {

            composable(route = Section.Setup.baseRoute) {
                val setupViewModel: SetupViewModel = getSetupViewModel(activity, chips)

                PageView(endCurvedText = endCurvedText) {
                    SetupView(
                        context = context,
                        navController = navController,
                        viewModel = setupViewModel,
                        columnState =  it
                    ) {
                        openAppSettings(activity)
                    }
                }
            }

            composable(
                route = Section.Edit.parametricRoute,
                arguments = listOf(
                    navArgument(Params.Tag.name) {
                        type = NavType.StringType
                    }
                )
            ) { navBackStackEntry ->
                val tag: Int? =
                    navBackStackEntry.arguments?.getString(Params.Tag.name)?.toIntOrNull()
                tag?.let { tagNotNull ->
                    val chip = chips[tagNotNull]
                    val editViewModel: EditViewModel = getEditViewModel(activity, chip)

                    PageView(endCurvedText = endCurvedText) {
                        EditView(
                            context = context,
                            navController = navController,
                            viewModel = editViewModel
                        )
                    }
                }
            }
            composable(
                route = Section.Timer.parametricRoute,
                arguments = listOf(
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
                    navBackStackEntry.arguments?.getString(OptionalParams.ChipIndex.name)?.toInt()
                        ?: 0
                val initialCycle =
                    navBackStackEntry.arguments?.getString(OptionalParams.CycleIndex.name)?.toInt()
                        ?: 0
                val initialTimerSeconds =
                    navBackStackEntry.arguments?.getString(OptionalParams.TimerSeconds.name)
                        ?.toInt() ?: 0

                val timerViewModel: TimerViewModel = getTimerViewModel(
                    activity,
                    chips,
                    initialChipIndex,
                    initialCycle,
                    initialTimerSeconds
                )

                PageView(endCurvedText = endCurvedText) {
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
    }
}

@Composable
private fun RequestPermissions() {
    val notificationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false) -> {}
            permissions.getOrDefault(Manifest.permission.USE_EXACT_ALARM, false) -> {}
            else -> {

            }
        }
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                val permissions = ArrayList<String>()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions.addAll(
                        listOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.USE_EXACT_ALARM
                        )
                    )
                }

                permissions.add(Manifest.permission.VIBRATE)

                notificationPermissionRequest.launch(permissions.toTypedArray())
            }

            else -> {}
        }
    }
}

private fun openNotification(context: Context, activity: MainViewController) {
    val notificationIntent = Intent(context, NotificationViewController::class.java)
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    activity.startActivity(notificationIntent)
    activity.finish()
}

fun openAppSettings(activity: MainViewController) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", activity.packageName, null)
    )
    activity.startActivity(intent)
}