package com.feduss.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

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
                    viewModel.userHasUpdatedSettings(activity, type, newValue)
                    navController.popBackStack()
                })
            }
        }
        composable(route = Section.Timer.baseRoute) {
            TimerView(viewModel.getData(activity))
        }
    }
}