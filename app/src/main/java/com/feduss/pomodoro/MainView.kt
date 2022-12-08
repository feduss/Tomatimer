package com.feduss.pomodoro

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun MainActivity(navController: NavHostController,
                 startDestination: String = Section.Setup.baseRoute,
                 chips: ArrayList<Chip>) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Section.Setup.baseRoute) {
            SetupView(
                chips = chips,
                onChipClicked = { title, value, unit ->
                    val args = listOf(title, value)
                    val optionalArgs = mapOf(Pair(OptionalParams.Unit.name, unit))
                    navController.navigate(Section.Edit.withArgs(args, optionalArgs)) {
                        launchSingleTop = true
                    }
                },
                onPlayIconClicked = {
                    navController.navigate(Section.Timer.baseRoute) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable( route = Section.Edit.parametricRoute, arguments = listOf(
                navArgument(Params.Title.name) { type = NavType.StringType },
                navArgument(Params.Value.name) { type = NavType.StringType },
                navArgument(Params.Unit.name) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { navBackStackEntry ->
            val title: String = navBackStackEntry.arguments?.getString(Params.Title.name) ?: ""
            val value: String = navBackStackEntry.arguments?.getString(Params.Value.name) ?: ""
            val unit: String = navBackStackEntry.arguments?.getString(Params.Unit.name) ?: ""
            EditView(sectionTitle = title, startValue = value, unit = unit)
        }
        composable(route = Section.Timer.baseRoute) {
            TimerView()
        }
    }
}