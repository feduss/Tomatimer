package com.feduss.tomato.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.curvedText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.feduss.tomatimer.entity.enums.OptionalParams
import com.feduss.tomatimer.entity.enums.Params
import com.feduss.tomatimer.entity.enums.Section
import com.feduss.tomatimer.entity.models.Chip
import com.feduss.tomato.factory.getEditViewModel
import com.feduss.tomato.factory.getSetupViewModel
import com.feduss.tomato.factory.getTimerViewModel
import com.feduss.tomato.view.edit.EditView
import com.feduss.tomato.view.notification.NotificationViewController
import com.feduss.tomato.view.setup.SetupView
import com.feduss.tomato.view.timer.TimerView
import com.feduss.tomato.viewmodel.edit.EditViewModel
import com.feduss.tomato.viewmodel.setup.SetupViewModel
import com.feduss.tomato.viewmodel.timer.TimerViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.layout.scrollAway
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scrollable
import java.util.*

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MainActivity(
    context: Context,
    activity: MainViewController,
    navController: NavHostController,
    chips: List<Chip>,
    startDestination: String = Section.Setup.baseRoute,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    
    var endCurvedText by remember {
        mutableStateOf("")
    }

    val timeSource = TimeTextDefaults.timeSource(
        DateFormat.getBestDateTimePattern(Locale.getDefault(), "HH:mm")
    )

    val color = Color(("#E3BAFF".toColorInt()))

    val notificationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false) -> {

            } else -> {

            }
        }
    }

    val setupColumnState = rememberColumnState()

    val timeTextModifier = Modifier.scrollAway(scalingLazyColumnState = setupColumnState)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    )
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppScaffold(
        timeText = {
            if (endCurvedText.isNotEmpty()) {
                TimeText(
                    modifier = timeTextModifier,
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
                    modifier = timeTextModifier,
                    timeSource = timeSource,
                )
            }

        },
    ) {
        SwipeDismissableNavHost(
            modifier = Modifier.background(Color.Black),
            navController = navController,
            startDestination = startDestination
        ) {

            composable(route = Section.Setup.baseRoute) {
                val setupViewModel: SetupViewModel = getSetupViewModel(activity, chips)

                SetupView(
                    context,
                    navController,
                    setupViewModel,
                    setupColumnState
                ) { openAppSettings(activity) }
            }

            composable(route = Section.Edit.parametricRoute, arguments = listOf(
                navArgument(Params.Tag.name) { type = NavType.StringType }
            )
            ) { navBackStackEntry ->
                val tag: Int? =
                    navBackStackEntry.arguments?.getString(Params.Tag.name)?.toIntOrNull()
                tag?.let { tagNotNull ->
                    val chip = chips[tagNotNull]
                    val editViewModel: EditViewModel = getEditViewModel(activity, chip)
                    EditView(
                        context,
                        navController,
                        editViewModel
                    )
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